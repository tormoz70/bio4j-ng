package ru.bio4j.ng.commons.utils;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.filter.Expression;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;
import ru.bio4j.ng.model.transport.Prop;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;
import static ru.bio4j.ng.model.transport.jstore.filter.FilterBuilder.*;

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
    public static String fileNameExt(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = fileName.lastIndexOf(File.separator);

        if (i > p) {
            extension = fileName.substring(i+1);
        }
        return extension;
    }

    public static String fileNameWithoutExt(String fileName) {
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
	public static String readStream(InputStream in, String encoding, boolean addLineSeparator) throws IOException {
		InputStreamReader is = new InputStreamReader(in, encoding);
		StringBuilder sb=new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read = br.readLine();
		while(read != null) {
		    sb.append(read);
		    if(addLineSeparator)
                sb.append(System.lineSeparator());
		    read=br.readLine();
		}
		return sb.toString();
	}

    public static String readStream(InputStream in, String encoding) throws IOException {
	    return readStream(in, encoding, true);
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

    public static String fieldValueAsString(Object bean, java.lang.reflect.Field field){
        Object val;
        try {
            field.setAccessible(true);
            val = field.get(bean);
        } catch (IllegalAccessException ex) {
            val = ex.toString();
        }
        String valStr = (val instanceof String) ? ((String)val).trim() : null;
        if(!Strings.isNullOrEmpty(valStr) && valStr.indexOf("\n") >= 0) {
            return valStr.substring(0, valStr.indexOf("\n")) + "...";
        } else
            return ""+val;
    }

    public static Object fieldValueAsObject(Object bean, java.lang.reflect.Field field){
        Object val;
        try {
            field.setAccessible(true);
            val = field.get(bean);
        } catch (IllegalAccessException ex) {
            val = ex.toString();
        }
        return val;
    }

    public static Object fieldValueAsObject(Object bean, String fieldName){
        Object val = null;
        if(bean != null) {
            java.lang.reflect.Field field = findFieldOfBean(bean.getClass(), fieldName);
            if(field != null) {
                try {
                    field.setAccessible(true);
                    val = field.get(bean);
                } catch (IllegalAccessException ex) {
                    val = ex.toString();
                }
            }
        }
        return val;
    }


    private static Boolean checkFilter(String fieldName, String excludeFields) {
        String[] fields2exclude = Strings.split(excludeFields, ";");
        for(String field2exclude : fields2exclude){
            if(field2exclude.equalsIgnoreCase(fieldName))
                return false;
        }
        return true;
    }

    public static String buildBeanStateInfo(Object bean, String beanName, String tab, String excludeFields) {
        if(tab == null) tab = "";
        if(bean == null)
            return tab + (Strings.isNullOrEmpty(beanName) ? "null" : beanName+" null");
        final String attrFmt = tab + " - %s : %s;\n";
        StringBuilder out = new StringBuilder();
        Class<?> type = bean.getClass();
        String bnName = isNullOrEmpty(beanName) ? type.getName() : beanName;
        out.append(String.format(tab + "%s {\n", bnName));
        for(java.lang.reflect.Field fld : getAllObjectFields(type)) {
            if(checkFilter(fld.getName(), excludeFields))
                out.append(String.format(attrFmt, fld.getName(), fieldValueAsString(bean, fld)));
        }
        out.append(tab + "}");
        return out.toString();
    }

    public static String buildBeanStateInfo(Object bean, String beanName, String tab) {
        return buildBeanStateInfo(bean, beanName, tab, null);
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

    public static boolean applyValuesToBeanFromABean(ABean vals, Object bean) throws ApplyValuesToBeanException {
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

    public static void applyValuesToABeanFromABean(ABean srcBean, ABean dstBean, boolean addIfNotExists) throws ApplyValuesToBeanException {
        boolean result = false;
        if(srcBean == null)
            throw new IllegalArgumentException("Argument \"srcBean\" cannot be null!");
        if(dstBean == null)
            throw new IllegalArgumentException("Argument \"dstBean\" cannot be null!");
        for(String key : srcBean.keySet()) {
            if(dstBean.containsKey(key))
                dstBean.put(key, srcBean.get(key));
            else{
                if(addIfNotExists)
                    dstBean.put(key, srcBean.get(key));
            }
        }
    }

    private static Field findFieldOfBean(Class<?> type, String fieldName) {
        for(java.lang.reflect.Field fld : getAllObjectFields(type)) {
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
                        val = (fld.getType().equals(Object.class) || fld.getType().equals(valObj.getClass())) ? valObj : Converter.toType(valObj, fld.getType());
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

    public static <T> T cloneBean1(T bean, Class<T> clazz) throws Exception {
        if(bean != null && !clazz.isPrimitive()) {
            T newBean = clazz.newInstance();
            applyValuesToBeanFromBean(bean, newBean);
            return newBean;
        }
        return null;
    }

    public static String preparePath(String path, char pathSeparator) {
        if(isNullOrEmpty(path))
            return null;
        String rslt = path;
        if(pathSeparator == (char)0)
            pathSeparator =  File.separatorChar;
        rslt = rslt.replace('\\', pathSeparator);
        rslt = rslt.replace('/', pathSeparator);
        return rslt;
    }

    public static String normalizePath(String path, char pathSeparator) {
        if(isNullOrEmpty(path))
            return null;
        if(pathSeparator == (char)0)
            pathSeparator =  File.separatorChar;
        String rslt = preparePath(path, pathSeparator);
        rslt = rslt.endsWith(""+pathSeparator) ? rslt : rslt + pathSeparator;
        return rslt;
    }
    public static String normalizePath(String path) {
        return normalizePath(path, (char)0);
    }

    public static String generateTmpFileName(final String tmpPath, final String fileName) {
        String randomUUIDString = UUID.randomUUID().toString().replaceAll("-", "");
        return String.format("%s%s-$(%s).%s", Utl.normalizePath(tmpPath), Utl.fileNameWithoutExt(fileName), randomUUIDString, Utl.fileNameExt(fileName));
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
//                if(pname.compare("" + fld.getGenericType())) {
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


    public static String md5(String fileName) throws IOException {
        String md5 = null;
        try (FileInputStream fis = new FileInputStream(new File(fileName))) {
            md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
        }
        return md5;
    }

    public static int storeInputStream(InputStream inputStream, Path path) throws IOException {
        int len = 0;
        Files.createDirectories(path.getParent());
        try(OutputStream out = new FileOutputStream(new File(path.toString()))) {
            int read = 0;
            final byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
                len += read;
            }
        }
        return len;
    }

    public static Path storeBlob(byte[] blob, Path path) throws IOException {
        Files.createDirectories(path.getParent());
        try(OutputStream out = new FileOutputStream(new File(path.toString()))) {
                out.write(blob);
        }
        return path;
    }

    public static Path storeBlob(byte[] blob, String path) throws IOException {
        return storeBlob(blob, Paths.get(path));
    }

    public static int storeInputStream(InputStream inputStream, String path) throws IOException {
        return storeInputStream(inputStream, Paths.get(path));
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
        } else
            throw new FileNotFoundException(String.format("File \"%s\" not found!", filePath));
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
            if(paramName.equals("this$1")) continue;
            if(!paramName.toLowerCase().startsWith("p_"))
                paramName = "p_" + paramName.toLowerCase();
            fld.setAccessible(true);
            Object valObj = fld.get(bean);
            Param.Direction direction = Param.Direction.UNDEFINED;
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

    public static List<Param> abeanToParams(ABean bean) throws Exception {
        List<Param> result = new ArrayList<>();
        if(bean == null)
            return result;
        for(String key : bean.keySet()) {
            String paramName = key;
            if(paramName.equals("this$1")) continue;
            if(!paramName.toLowerCase().startsWith("p_"))
                paramName = "p_" + paramName.toLowerCase();
            Object valObj = bean.get(key);
            result.add(Param.builder().name(paramName).value(valObj).build());
        }
        return result;
    }

    public static List<Param> hashmapToParams(HashMap<String, Object> bean) throws Exception {
        List<Param> result = new ArrayList<>();
        if(bean == null)
            return result;
        for(String key : bean.keySet()) {
            String paramName = key;
            if(paramName.equals("this$1")) continue;
            if(!paramName.toLowerCase().startsWith("p_"))
                paramName = "p_" + paramName.toLowerCase();
            Object valObj = bean.get(key);
            result.add(Param.builder().name(paramName).value(valObj).build());
        }
        return result;
    }

    public static List<Param> anjsonToParams(String anjson) throws Exception {
        List<Param> result = new ArrayList<>();
        if(Strings.isNullOrEmpty(anjson))
            return result;

        HashMap bioParamsContainer = Jsons.decode(anjson);
        if(bioParamsContainer.containsKey("bioParams")) {
            List<HashMap<String, Object>> bioParamsArray = (List)bioParamsContainer.get("bioParams");
            for (HashMap<String, Object> prm : bioParamsArray) {
                String paramName = (String)prm.get("name");
                if (!paramName.toLowerCase().startsWith("p_"))
                    paramName = "p_" + paramName.toLowerCase();
                Object valObj = prm.get("value");
                result.add(Param.builder().name(paramName).value(valObj).build());
            }
        }
        return result;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static Document loadXmlDocument(InputStream inputStream) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setValidating(false);
        DocumentBuilder builder = f.newDocumentBuilder();
        return builder.parse(inputStream);
    }

    public static Document loadXmlDocument(String fileName) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setValidating(false);
        DocumentBuilder builder = f.newDocumentBuilder();
        InputStream inputStream = openFile(fileName);
        return builder.parse(inputStream);
    }

    public static void writeInputToOutput(InputStream input, OutputStream output) throws Exception {
        BufferedInputStream buf = null;
        try {
            buf = new BufferedInputStream(input);
            int readBytes = 0;
            while ((readBytes = buf.read()) != -1)
                output.write(readBytes);
        } finally {
            if (output != null)
                output.flush();
            output.close();
            if (buf != null)
                buf.close();
        }
    }


    public static void writeFileToOutput(File file, OutputStream output) throws Exception {
        writeInputToOutput(new FileInputStream(file), output);
    }

    public static void deleteFile(Path filePath, Boolean silent) throws Exception {
        try {
            Files.delete(filePath);
        } catch(Exception e){
            if(!silent)
                throw e;
        }
    }

    public static void deleteFile(String filePath, Boolean silent) throws Exception {
        deleteFile(Paths.get(filePath), silent);
    }

    public static LoginRec parsLogin(String login) {
        LoginRec rslt = new LoginRec();
        String[] loginParts = Strings.split(login, "/");
        if(loginParts.length > 0)
            rslt.setUsername(loginParts[0]);
        if(loginParts.length > 1)
            rslt.setPassword(loginParts[1]);
        return rslt;
    }

    public static <T extends Enum<T>> T enumValueOf(
            Class<T> enumeration, String name) {

        for (T enumValue : enumeration.getEnumConstants()) {
            if (enumValue.name().equalsIgnoreCase(name)) {
                return enumValue;
            }
        }

        throw new IllegalArgumentException(String.format(
                "There is no value with name '%s' in Enum %s",
                name, enumeration.getName()
        ));
    }

    public static Filter restoreSimpleFilter(String simpleFilter){
        try {
            if (simpleFilter.length() == 0 || !simpleFilter.startsWith("{") || !simpleFilter.endsWith("}"))
                throw new Exception(String.format("Error in structure of filter \"%s\"!", simpleFilter));
            simpleFilter = simpleFilter.substring(1, simpleFilter.length()-1);
            Filter rslt = new Filter();
            String[] items = Strings.split(simpleFilter, ",");

            Expression rootAnd = and();
            for (String item : items) {
                String[] fildvalItems = Strings.split(item, ":");
                if (fildvalItems.length != 2)
                    throw new Exception(String.format("Error in structure of item \"%s\"!", item));
                String checkedFieldName = Regexs.find(fildvalItems[0], "\\w+", Pattern.CASE_INSENSITIVE);
                if (!fildvalItems[0].equals(checkedFieldName))
                    throw new Exception(String.format("Error in fieldName of item \"%s\"!", item));
                String[] fieldValues = Strings.split(fildvalItems[1], "\"|\"");
                if (fieldValues.length == 0 || !fildvalItems[1].startsWith("\"") || !fildvalItems[1].endsWith("\""))
                    throw new Exception(String.format("Error in fieldValue of item \"%s\"!", item));
                fieldValues[0] = fieldValues[0].substring(1);
                fieldValues[fieldValues.length-1] = fieldValues[fieldValues.length-1].substring(0, fieldValues[fieldValues.length-1].length()-1);
                if(fieldValues.length > 1) {
                    Expression itemOr = or();
                    for (String fieldValue : fieldValues)
                        itemOr.add(contains(checkedFieldName, fieldValue, true));
                    rootAnd.add(itemOr);
                } else
                    rootAnd.add(contains(checkedFieldName, fieldValues[0], true));
            }
            rslt.add(rootAnd);
            return rslt;
        } catch (Exception e) {
            LOG.error("Error parsing simple filter \"{}\". Msg: {}", simpleFilter, e.getMessage());
            return null;
        }
    }
    public static List<Sort> restoreSimpleSort(String simpleSort){
        List<Sort> rslt = new ArrayList<>();
        try {
            Sort sort;
            String[] items = Strings.split(simpleSort, ",", ";", "|");
            for (String item : items) {
                String checkedItem = Regexs.find(item, "(\\+|\\-)\\w+", Pattern.CASE_INSENSITIVE);
                if (item.equals(checkedItem)) {
                    sort = new Sort();
                    sort.setFieldName(item.substring(1));
                    sort.setDirection(item.substring(0, 1).equals("+") ? Sort.Direction.ASC : Sort.Direction.DESC);
                    rslt.add(sort);
                } else
                    return null;
            }
        } catch (Exception e) {
            LOG.error("Error parsing simple sort \"{}\". Msg: {}", simpleSort, e.getMessage());
        }
        return rslt;
    }


}

