package ru.bio4j.ng.annot;

import ru.bio4j.ng.service.api.AppServiceTypesProvider;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

@SupportedAnnotationTypes("ru.bio4j.ng.service.api.AppServiceTypesProvider")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AnnotationProcessorImpl extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }

        Map<String,Set<String>> services = new HashMap<String, Set<String>>();
        
        Elements elements = processingEnv.getElementUtils();

        // discover services from the current compilation sources
        for (Element e : roundEnv.getElementsAnnotatedWith(AppServiceTypesProvider.class)) {
            AppServiceTypesProvider a = e.getAnnotation(AppServiceTypesProvider.class);
            if(a==null) {
                continue;
            } // input is malformed, ignore
            if (!e.getKind().isClass() && !e.getKind().isInterface()) {
                continue;
            } // ditto
            TypeElement type = (TypeElement)e;
            TypeElement contract = getContract(type, a);
            if(contract==null) {
                continue;
            } // error should have already been reported

            String cn = elements.getBinaryName(contract).toString();
            Set<String> v = services.get(cn);
            if(v==null) {
                services.put(cn,v=new TreeSet<String>());
            }
            v.add(elements.getBinaryName(type).toString());
        }

        // also load up any existing values, since this compilation may be partial
        Filer filer = processingEnv.getFiler();
        for (Map.Entry<String,Set<String>> e : services.entrySet()) {
            try {
                String contract = e.getKey();
                FileObject f = filer.getResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/services/" +contract);
                BufferedReader r = new BufferedReader(new InputStreamReader(f.openInputStream(), "UTF-8"));
                String line;
                while((line=r.readLine())!=null) {
                    e.getValue().add(line);
                }
                r.close();
            } catch (FileNotFoundException x) {
                // doesn't exist
            } catch (IOException x) {
                processingEnv.getMessager().printMessage(Kind.ERROR,"Failed to load existing service definition files: "+x);
            }
        }

        // now write them back out
        for (Map.Entry<String,Set<String>> e : services.entrySet()) {
            try {
                String contract = e.getKey();
                processingEnv.getMessager().printMessage(Kind.NOTE,"Writing META-INF/services/"+contract);
                FileObject f = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/services/" +contract);
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(f.openOutputStream(), "UTF-8"));
                for (String value : e.getValue()) {
                    pw.println(value);
                }
                pw.close();
            } catch (IOException x) {
                processingEnv.getMessager().printMessage(Kind.ERROR,"Failed to write service definition files: "+x);
            }
        }

        return false;
    }

    private TypeElement getContract(TypeElement type, AppServiceTypesProvider a) {
        // explicitly specified?
        try {
            a.value();
            throw new AssertionError();
        } catch (MirroredTypeException e) {
            TypeMirror m = e.getTypeMirror();
            if (m.getKind()== TypeKind.VOID) {
                // contract inferred from the signature
                boolean hasBaseClass = type.getSuperclass().getKind()!=TypeKind.NONE && !isObject(type.getSuperclass());
                boolean baseHasInterfaces = false;
                TypeElement baseTypeElement = null;
                boolean hasInterfaces = !type.getInterfaces().isEmpty();

                if(hasBaseClass) {
                    baseTypeElement = (TypeElement)((DeclaredType)type.getSuperclass()).asElement();
                    baseHasInterfaces = !baseTypeElement.getInterfaces().isEmpty();
                }

                if(hasInterfaces)
                    return (TypeElement)((DeclaredType)type.getInterfaces().get(0)).asElement();
                if(hasBaseClass && baseHasInterfaces)
                    return (TypeElement)((DeclaredType)baseTypeElement.getInterfaces().get(0)).asElement();
                if(hasBaseClass)
                    return (TypeElement)((DeclaredType)type.getSuperclass()).asElement();

                error(type, "hasInterfaces:"+hasInterfaces+"; hasBaseClass:"+hasBaseClass+"; Contract type was not specified, but it couldn't be inferred.");
                return null;
            }

            if (m instanceof DeclaredType) {
                DeclaredType dt = (DeclaredType) m;
                return (TypeElement)dt.asElement();
            } else {
                error(type, "Invalid type specified as the contract");
                return null;
            }
        }


    }

    private boolean isObject(TypeMirror t) {
        if (t instanceof DeclaredType) {
            DeclaredType dt = (DeclaredType) t;
            return((TypeElement)dt.asElement()).getQualifiedName().toString().equals("java.lang.Object");
        }
        return false;
    }

    private void error(Element source, String msg) {
        processingEnv.getMessager().printMessage(Kind.ERROR,msg,source);
    }
}
