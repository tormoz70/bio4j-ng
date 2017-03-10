package ru.bio4j.ng.commons.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.types.Prop;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;

import javax.servlet.ServletContext;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.beans.XMLEncoder;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

public class Utl {
    private static final Logger LOG = LoggerFactory.getLogger(Utl.class);


    /**
     * Вытаскивает имя вайла из полного пути
     * @param filePath
     * @return
     */
    public static String fileName(String filePath) {
        if(isNullOrEmpty(filePath)){
            int p = filePath.lastIndexOf(File.separator);
            if (p >= 0)
                return filePath.substring(p+1);
            return filePath;
        }
        return filePath;
    }

    /**
     * Вытаскивает расширение файла из полного пути
     * @param fileName
     * @return
     */
    public static String fileExt(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = fileName.lastIndexOf(File.separator);

        if (i > p) {
            extension = fileName.substring(i+1);
        }
        return extension;
    }

    public static String fileWithoutExt(String fileName) {
        if(Strings.isNullOrEmpty(fileName))
            return fileName;
        return fileName.replaceFirst("[.][^.]+$", "");
    }

    /**
     * Приводит LongToInt, если это возможно
     * @param l
     * @return
     */
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    /**
     * Читает из потока заданное кол-во байтов и возвращает их в виде массива
     * @param in
     * @param length
     * @return
     * @throws IOException
     */
    public static byte[] readStream(InputStream in, int length) throws IOException {
        if (length > (Integer.MAX_VALUE - 5))
            throw new IllegalArgumentException("Parameter \"length\" too big!");
        byte[] buff = new byte[Utl.safeLongToInt(length)];
        int readed = in.read(buff);
        return buff;
    }

	/**
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String readStream(InputStream in, String encoding) throws IOException {
		InputStreamReader is = new InputStreamReader(in, encoding);
		StringBuilder sb=new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read = br.readLine();
		while(read != null) {
		    sb.append(read);
            sb.append(System.lineSeparator());
		    read =br.readLine();
		}
		return sb.toString();
	}

    public static String readStream(InputStream in) throws IOException {
        return readStream(in, "UTF-8");
    }


	/**
	 * @param clazz
	 * @param in
	 * @return
	 * @throws JAXBException
	 */
	public static <T> T unmarshalXml(Class<T> clazz, InputStream in) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Object obj = jaxbUnmarshaller.unmarshal(in);
		if(obj == null) return null;
		if(obj.getClass() == clazz)
			return (T)obj;
		return null;
	}

    /**
     * Checks two classes is assignable
     * @param clazz1
     * @param clazz2
     * @return boolean
     * @throws
     */
	public static boolean typesIsAssignable(Class<?> clazz1, Class<?> clazz2) {
		if((clazz1 == null) && (clazz2 == null)) return true;
		if(((clazz1 != null) && (clazz2 == null)) || ((clazz1 == null) && (clazz2 != null))) return false;
		return (clazz1 == clazz2) || clazz1.isAssignableFrom(clazz2) || clazz2.isAssignableFrom(clazz1);
	}

    /**
     * Checks two classes is the same
     * @param clazz1
     * @param clazz2
     * @return boolean
     * @throws
     */
    public static boolean typesIsSame(Class<?> clazz1, Class<?> clazz2) {
        if((clazz1 == null) || (clazz2 == null)) return true;
        if(((clazz1 != null) && (clazz2 == null)) || ((clazz1 == null) && (clazz2 != null))) return false;
        return clazz1.getName().equals(clazz2.getName());
    }

	/**
	 * @param annotationType - Type of annotation to find
	 * @param clazz - Annotated type
	 * @return - Annotation object
	 */
	public static <T extends Annotation> T findAnnotation(Class<T> annotationType, Class<?> clazz) {
		for (Annotation annotation : clazz.getDeclaredAnnotations()) {
			Class<?> atype = annotation.annotationType();
		    if(typesIsAssignable(atype, annotationType))
		    	return (T)annotation;
		}
		return null;
	}

    public static <T extends Annotation> T findAnnotation(Class<T> annotationType, Field field) {
        for (Annotation annotation : field.getDeclaredAnnotations()) {
            Class<?> atype = annotation.annotationType();
            if(typesIsAssignable(atype, annotationType))
                return (T)annotation;
        }
        return null;
    }


	/**
	 * @param packageName
	 * @return
	 */
	public static String pkg2path(String packageName) {
		return (isNullOrEmpty(packageName) ? null : "/"+packageName.replace('.', '/')+"/");
	}

	/**
	 * @param path
	 * @return
	 */
	public static String path2pkg(String path) {
		if(isNullOrEmpty(path)) return null;
		String result = path.replace('/', '.');
		if(result.charAt(0) == '.')
			result = result.substring(1);
		if(result.charAt(result.length()-1) == '.')
			result = result.substring(0, result.length()-1);
		return result;
	}

	/**
	 * @param path
	 * @return
	 */
	public static String classNameFromPath(String path) {
		if(isNullOrEmpty(path)) return null;
		return path.endsWith(".class") ? path2pkg(path.replaceAll("\\.class$", "")) : null;
	}


    private static void extractAllObjectFields(List<Field> fields, Class<?> type) {
        for (Field field: type.getDeclaredFields())
            fields.add(field);
        if (type.getSuperclass() != null)
            extractAllObjectFields(fields, type.getSuperclass());
    }

    public static List<Field> getAllObjectFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        extractAllObjectFields(fields, type);
        return fields;
    }

    public static String buildBeanStateInfo(Object bean, String beanName, String tab) {
        if(tab == null) tab = "";
        final String attrFmt = tab + " - %s : %s;\n";
        StringBuilder out = new StringBuilder();
        Class<?> type = bean.getClass();
        String bnName = isNullOrEmpty(beanName) ? type.getName() : beanName;
        out.append(String.format(tab + "%s {\n", bnName));
        for(java.lang.reflect.Field fld : getAllObjectFields(type)) {
            Object val;
            try {
                fld.setAccessible(true);
                val = fld.get(bean);
            } catch (IllegalAccessException ex) {
                val = ex.toString();
            }
            String valStr = (val instanceof String) ? ((String)val).trim() : null;
            if(!Strings.isNullOrEmpty(valStr) && valStr.indexOf("\n") >= 0) {
                valStr = valStr.substring(0, valStr.indexOf("\n")) + "...";
            } else
                valStr = ""+val;
            out.append(String.format(attrFmt, fld.getName(), valStr));
        }
        out.append(tab + "}");
        return out.toString();
    }

    public static String dictionaryInfo(Dictionary dict, String beanName, String tab) {
        if(tab == null) tab = "";
        final String attrFmt = tab + " - %s : %s;\n";
        StringBuilder out = new StringBuilder();
        out.append(String.format(tab + "%s {\n", beanName));
        for (Enumeration e = dict.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Object val = dict.get(key);
            out.append(String.format(attrFmt, key, val));
        }
        out.append(tab + "}");
        return out.toString();
    }

    public static boolean applyValuesToBeanFromDict(Dictionary vals, Object bean) throws ApplyValuesToBeanException {
        boolean result = false;
        if(vals == null)
            throw new IllegalArgumentException("Argument \"vals\" cannot be null!");
        if(bean == null)
            throw new IllegalArgumentException("Argument \"bean\" cannot be null!");
        Class<?> type = bean.getClass();
        for(java.lang.reflect.Field fld : getAllObjectFields(type)) {
            String fldName = fld.getName();
            Prop p = findAnnotation(Prop.class, fld);
            if(p != null)
                fldName = p.name();
            Object valObj = vals.get(fldName);
            if(valObj != null){
                try {
                    Object val = (fld.getType() == Object.class) ? valObj : Converter.toType(valObj, fld.getType());
                    fld.setAccessible(true);
                    fld.set(bean, val);
                    if(!result) result = true;
                } catch (Exception e) {
                    throw new ApplyValuesToBeanException(fldName, String.format("Can't set value %s to field. Msg: %s", valObj, e.getMessage()));
                }
            }
        }
        return result;
    }

    private static Field findFieldOfBean(Class<?> type, String fieldName) {
        for(java.lang.reflect.Field fld : type.getDeclaredFields()) {
            if(fld.getName().equals(fieldName))
                return fld;
        }
        return null;
    }

    public static Object arrayCopyOf(Object original) {
//        T[] copy = ((Object)newType == (Object)Object[].class)
//                ? (T[]) new Object[newLength]
//                : (T[]) Array.newInstance(newType.getComponentType(), newLength);
//        System.arraycopy(original, 0, copy, 0,
//                Math.min(original.length, newLength));
//        return copy;
        if(original != null && original.getClass().isArray()) {
            int l = ((Object[])original).length;
            Class<?> originalType = original.getClass();
            Object rslt = Array.newInstance(originalType.getComponentType(), l);
            System.arraycopy(original, 0, rslt, 0, l);
            return rslt;
        }
        return null;
    }

    public static boolean applyValuesToBeanFromBean(Object srcBean, Object bean) throws ApplyValuesToBeanException {
        boolean result = false;
        if(srcBean == null)
            throw new IllegalArgumentException("Argument \"srcBean\" cannot be null!");
        if(bean == null)
            throw new IllegalArgumentException("Argument \"bean\" cannot be null!");
        Class<?> srcType = srcBean.getClass();
        Class<?> type = bean.getClass();
        for(java.lang.reflect.Field fld : getAllObjectFields(type)) {
            String fldName = fld.getName();
            Field srcFld = findFieldOfBean(srcType, fldName);
            if(srcFld == null)
                continue;
            try {
                srcFld.setAccessible(true);
                Object valObj = srcFld.get(srcBean);
                if(valObj != null) {
                    Object val;
                    if(valObj.getClass().isArray()) {
                        val = arrayCopyOf(valObj);
                    } else {
                        val = (fld.getType() == Object.class) ? valObj : Converter.toType(valObj, fld.getType());
                    }
                    fld.setAccessible(true);
                    fld.set(bean, val);
                    result = true;
                }
            } catch (Exception e) {
                String msg = String.format("Can't set value to field. Msg: %s", e.getMessage());
                throw new ApplyValuesToBeanException(fldName, msg, e);
            }
        }
        return result;
    }

    public static <T> boolean arrayContains(T[] array, T item) {
        for (T itm : array)
            if (itm == item)
                return true;
        return false;
    }

    public static <T> T nvl(T a, T b) {
        return (a == null)?b:a;
    }

    public static Object cloneBean(Object bean) throws Exception {
        if(bean != null && !bean.getClass().isPrimitive()) {
            Class<?> type = bean.getClass();
            Object newBean = type.newInstance();
            applyValuesToBeanFromBean(bean, newBean);
            return newBean;
        }
        return null;
    }

    public static String normalizePath(String path, char pathSeparator) {
        if(isNullOrEmpty(path))
            return null;
        String rslt = path;
        if(pathSeparator == (char)0)
            pathSeparator =  File.separatorChar;
        rslt = rslt.replace('\\', pathSeparator);
        rslt = rslt.replace('/', pathSeparator);
        rslt = rslt.endsWith(""+pathSeparator) ? rslt : rslt + pathSeparator;
        return rslt;
    }
    public static String normalizePath(String path) {
        return normalizePath(path, (char)0);
    }

    // хз пока не получилось...
//    public static Class<?> getTypeParams(Object obj) {
//        if(obj == null)
//            throw new IllegalArgumentException("Param obj cannot be null!!");
//        Class<?> clazz = obj.getClass();
//        TypeVariable<?>[] params = clazz.getTypeParameters();
//        if(params.length > 0) {
//            TypeVariable<?> paramFirst = params[0];
//            GenericDeclaration gd = paramFirst.getGenericDeclaration();
//            String pname = paramFirst.getName();
//            for(java.lang.reflect.Field fld : clazz.getDeclaredFields()) {
//                if(pname.equals("" + fld.getGenericType())) {
//                    fld.setAccessible(true);
//                    Object fldVal;
//                    try {
//                        fldVal=fld.get(obj);
//                    } catch(IllegalAccessException e) {
//                        fldVal=null;
//                    }
//                    return (fldVal != null) ? fldVal.getClass() : null;
//                }
//            }
//            return null;
//        }
//        return null;
//    }


    public static <T> T getService(ServletContext servletContext, Class<T> serviceInterface) {
        BundleContext bundleContext = (BundleContext) servletContext.getAttribute("osgi-bundlecontext");
        if (bundleContext == null)
            throw new IllegalStateException("osgi-bundlecontext not registered!");

        ServiceReference<T> serviceReference = bundleContext.getServiceReference(serviceInterface);
        if(serviceReference != null)
            return (T)bundleContext.getService(serviceReference);
        else
            throw new IllegalStateException(String.format("Service %s not registered!", serviceInterface.getName()));
    }

    public static String extractModuleKey(String bioCode) {
        String[] bioCodeParts = Strings.split(bioCode, "@");
        if(bioCodeParts.length == 2)
            return bioCodeParts[0];
        return null;
    }
    public static String extractBioPath(String bioCode, String pathSeparator) {
        String[] bioCodeParts = Strings.split(bioCode, "@");
        if(bioCodeParts.length == 2) {
            String path = pathSeparator + bioCodeParts[1].replace(".", pathSeparator);
            return path;
        } else if(bioCodeParts.length == 1) {
            return pathSeparator + bioCode.replace(".", pathSeparator);
        }
        return null;
    }

    public static final String DEFAULT_BIO_PATH_SEPARATOR = "/";
    public static String extractBioPath(String bioCode) {
        return extractBioPath(bioCode, DEFAULT_BIO_PATH_SEPARATOR);
    }

    public static String extractBioParentPath(String bioCode, String pathSeparator) {
        String[] bioCodeParts = Strings.split(bioCode, ".");
        if(bioCodeParts.length > 1) {
            bioCodeParts = Arrays.copyOf(bioCodeParts, bioCodeParts.length - 1);
            bioCode = Strings.combineArray(bioCodeParts, ".");
            return extractBioPath(bioCode, pathSeparator);
        }
        return pathSeparator;
    }

    public static String extractBioParentPath(String bioCode) {
        return extractBioParentPath(bioCode, DEFAULT_BIO_PATH_SEPARATOR);
    }

    public static Byte[] toObjects(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];
        int i = 0;
        for (byte b : bytesPrim) bytes[i++] = b; //Autoboxing
        return bytes;

    }
    public static byte[] toPrimitives(Byte[] oBytes) {

        byte[] bytes = new byte[oBytes.length];
        for(int i = 0; i < oBytes.length; i++){
            bytes[i] = oBytes[i];
        }
        return bytes;
    }

    public static Boolean confIsEmpty(Dictionary conf) {
        if(conf == null || conf.isEmpty())
            return true;
        int count = 0;
        String componentKey = "component";
        for (Enumeration e = conf.keys(); e.hasMoreElements();) {
            e.nextElement();
            count++;
        }
        return (count == 1 && conf.get(componentKey) != null);
    }

    public static void encode2xml(Object object, OutputStream stream) throws Exception {
        JAXBContext context = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(object, stream);
    }

    public static String md5(String fileName) throws IOException {
        String md5 = null;
        try (FileInputStream fis = new FileInputStream(new File(fileName))) {
            md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
        }
        return md5;
    }

    public static Path storeInputStream(InputStream inputStream, Path path) throws IOException {
        Files.createDirectories(path.getParent());
        try(OutputStream out = new FileOutputStream(new File(path.toString()))) {
            int read = 0;
            final byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
        }
        return path;
    }

    public static String storeInputStream(InputStream inputStream, String path) throws IOException {
        return storeInputStream(inputStream, Paths.get(path)).toString();
    }

    public static void storeString(String text, String path, String encoding) throws IOException {
        try (PrintStream out = new PrintStream(new FileOutputStream(path), true, encoding)) {
            out.print(text);
        }
    }
    public static void storeString(String text, String path) throws IOException {
        storeString(text, path, "utf-8");
    }

    public static InputStream openFile(String filePath) throws IOException {
        File file = new File(filePath);
        if(file.exists()){
            InputStream inputStream = new FileInputStream(file);
            return inputStream;
        }
        return null;
    }

    public static String generateUUID(){
        UUID uuid = UUID.randomUUID();
        String hex = uuid.toString().replace("-", "").toLowerCase();
        return hex;
    }

    public static List<Param> beanToParams(Object bean) throws Exception {
        List<Param> result = new ArrayList<>();
        if(bean == null)
            return result;
        Class<?> srcType = bean.getClass();
        for(java.lang.reflect.Field fld : getAllObjectFields(srcType)) {
            String paramName = fld.getName();
            if(!paramName.toLowerCase().startsWith("p_"))
                paramName = "p_" + paramName.toLowerCase();
            fld.setAccessible(true);
            Object valObj = fld.get(bean);
            Param.Direction direction = Param.Direction.IN;
            Prop prp = fld.getAnnotation(Prop.class);
            MetaType metaType = MetaTypeConverter.read(fld.getType());
            if(prp != null) {
                if(!Strings.isNullOrEmpty(prp.name()))
                    paramName = prp.name().toLowerCase();
                if(prp.metaType() != MetaType.UNDEFINED)
                    metaType = prp.metaType();
                direction = prp.direction();
            }
            result.add(Param.builder()
                    .name(paramName)
                    .type(metaType)
                    .direction(direction)
                    .value(valObj)
                    .build());
        }
        return result;
    }

}

