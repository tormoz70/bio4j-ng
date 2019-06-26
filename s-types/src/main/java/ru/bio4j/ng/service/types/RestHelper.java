package ru.bio4j.ng.service.types;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.Utl;

import java.lang.annotation.Annotation;
import java.util.Enumeration;

public class RestHelper {
    private static final Logger LOG = LoggerFactory.getLogger(RestServiceBase.class);

    private RestHelper() { /* hidden constructor */ }

    public static RestHelperMethods getInstance() {
        return SingletonContainer.INSTANCE;
    }

    public static AppServiceTypeGetters getAppTypes() {
        return SingletonContainer.SRVTYPES;
    }

    private static Class<?> findClassInBundleByAnnotation(Class<? extends Annotation> annotationType) {
        BundleContext bundleContext = Utl.getBundleContext(ServletContextHolder.getServletContext());
        Class<?> resultClazz = null;
        try {
            Enumeration<?> classes = bundleContext.getBundle().findEntries("/", "*.class", true);
            if(classes.hasMoreElements()) {
                while (true){
                    String elem = classes.nextElement().toString();
                    //bundle://95.0:0/WEB-INF/classes/ru/fk/ekb/rapi/restful/models/FilmStat.class
                    final String csClassNameBgn = "WEB-INF/classes/";
                    final String csClassNameEnd = ".class";
                    String className = elem.substring(elem.indexOf(csClassNameBgn)+csClassNameBgn.length(), elem.indexOf(csClassNameEnd)).replaceAll("/", ".");
                    Class<?> currClazz = bundleContext.getBundle().loadClass(className);
                    if(currClazz != null) {
                        boolean isAnnoted = currClazz.isAnnotationPresent(annotationType);
                        LOG.debug(String.format(" --- found Clazz: %s, isAnnoted: %s, in bundle:%s", currClazz.getName(), isAnnoted, bundleContext.getBundle().getSymbolicName()));
                        if(isAnnoted && resultClazz == null)
                            resultClazz = currClazz;
                    } else
                        LOG.debug(String.format(" --- Not found Clazz: %s, in bundle:%s", className, bundleContext.getBundle().getSymbolicName()));
                    if(!classes.hasMoreElements()) break;
                }
            }
            LOG.debug(String.format("Found %s: %s", annotationType, resultClazz));
        } catch (Exception e) {
            LOG.error(String.format("Unexpected error while finding class in bundle %s by annotation %s!", bundleContext.getBundle().getSymbolicName(), annotationType), e);
            resultClazz = null;
        }
        return resultClazz;
    }
//if (YourInterface.class.isAssignableFrom(clazz)) {
    private static Class<?> findClassInBundleByInterface(Class interfaceType) {
        BundleContext bundleContext = Utl.getBundleContext(ServletContextHolder.getServletContext());
        Class<?> resultClazz = null;
        try {
            Enumeration<?> classes = bundleContext.getBundle().findEntries("/", "*.class", true);
            if(classes.hasMoreElements()) {
                while (true){
                    String elem = classes.nextElement().toString();
                    //bundle://95.0:0/WEB-INF/classes/ru/fk/ekb/rapi/restful/models/FilmStat.class
                    final String csClassNameBgn = "WEB-INF/classes/";
                    final String csClassNameEnd = ".class";
                    String className = elem.substring(elem.indexOf(csClassNameBgn)+csClassNameBgn.length(), elem.indexOf(csClassNameEnd)).replaceAll("/", ".");
                    Class<?> currClazz = bundleContext.getBundle().loadClass(className);
                    if(currClazz != null) {
                        boolean isService = Utl.typeHasInterface(currClazz, interfaceType);
                        LOG.debug(String.format(" --- found Clazz: %s, isServise: %s, in bundle:%s", currClazz.getName(), isService, bundleContext.getBundle().getSymbolicName()));
                        if(isService && resultClazz == null)
                            resultClazz = currClazz;
                    } else
                        LOG.debug(String.format(" --- Not found Clazz: %s, in bundle:%s", className, bundleContext.getBundle().getSymbolicName()));
                    if(!classes.hasMoreElements()) break;
                }
            }
            LOG.debug(String.format("Found %s: %s", interfaceType, resultClazz));
        } catch (Exception e) {
            LOG.error(String.format("Unexpected error while finding class in bundle %s by interface %s!", bundleContext.getBundle().getSymbolicName(), interfaceType), e);
            resultClazz = null;
        }
        return resultClazz;
    }

    private static <T> T createMethodsProvider(Class<T> interfaceType, Class<?> defaultImpl) {
        T result = null;
        Class<?> implClass = findClassInBundleByInterface(interfaceType);
        if (implClass == null)
            implClass = defaultImpl;
        try {
            result = (T)implClass.newInstance();
        } catch (Exception e) {
            LOG.error(String.format("Unexpected error while creating %s: %s", interfaceType.getName(), implClass.getName()));
        }
        LOG.debug(String.format("Found implementation for %s: %s", interfaceType.getName(), implClass.getName()));
        return result;
    }

    private static class SingletonContainer {
        public static final AppServiceTypeGetters SRVTYPES;
        public static final RestHelperMethods INSTANCE;

        static {
            INSTANCE = createMethodsProvider(RestHelperMethods.class, DefaultRestHelperMethods.class);
            SRVTYPES = createMethodsProvider(AppServiceTypeGetters.class, DefaultAppServiceTypes.class);
        }
    }
}
