package ru.bio4j.ng.commons.utils;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Dictionary;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.felix.ipojo.annotations.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.Types;
import ru.bio4j.ng.commons.types.Prop;

import static ru.bio4j.ng.commons.utils.Strings.*;

public class Utl {
    private static final Logger LOG = LoggerFactory.getLogger(Utl.class);


    /**
     * Вытаскивает имя вайла из полного пути
     * @param filePath
     * @return
     */
    public static String fileName(String filePath) {
        if(isNullOrEmpty(filePath)){
            int p = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
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
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i+1);
        }
        return extension;
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
	

    public static String buildBeanStateInfo(Object bean, String beanName, String tab) {
        if(tab == null) tab = "";
        final String attrFmt = tab + " - %s : %s;\n";
        StringBuilder out = new StringBuilder();
        Class<?> type = bean.getClass();
        String bnName = isNullOrEmpty(beanName) ? type.getName() : beanName;
        out.append(String.format(tab + "%s {\n", bnName));
        for(java.lang.reflect.Field fld : type.getDeclaredFields()) {
            Object val;
            try {
                fld.setAccessible(true);
                val = fld.get(bean);
            } catch (IllegalAccessException ex) {
                val = ex.toString();
            }
            out.append(String.format(attrFmt, fld.getName(), val));
        }
        out.append(tab + "}");
        return out.toString();
    }

    public static boolean applyValuesToBean(Dictionary vals, Object bean) throws ApplyValuesToBeanException {
        boolean result = false;
        if(vals == null)
            throw new IllegalArgumentException("Argument \"vals\" cannot be null!");
        if(bean == null)
            throw new IllegalArgumentException("Argument \"bean\" cannot be null!");
        Class<?> type = bean.getClass();
        for(java.lang.reflect.Field fld : type.getDeclaredFields()) {
            String fldName = fld.getName();
            Prop p = findAnnotation(Prop.class, fld);
            if(p != null)
                fldName = p.name();
            Object valStr = vals.get(fldName);
            if(valStr != null){
                try {
                    Object val = Converter.toType(valStr, fld.getType());
                    fld.setAccessible(true);
                    fld.set(bean, val);
                    if(!result) result = true;
                } catch (Exception e) {
                    throw new ApplyValuesToBeanException(fldName, String.format("Can't set value %s to field. Msg: %s", valStr, e.getMessage()));
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

    public static boolean applyValuesToBean(Object srcBean, Object bean) throws ApplyValuesToBeanException {
        boolean result = false;
        if(srcBean == null)
            throw new IllegalArgumentException("Argument \"srcBean\" cannot be null!");
        if(bean == null)
            throw new IllegalArgumentException("Argument \"bean\" cannot be null!");
        Class<?> srcType = srcBean.getClass();
        Class<?> type = bean.getClass();
        for(java.lang.reflect.Field fld : type.getDeclaredFields()) {
            String fldName = fld.getName();
            Field srcFld = findFieldOfBean(srcType, fldName);
            if(srcFld == null)
                continue;
            try {
                srcFld.setAccessible(true);
                Object valStr = srcFld.get(srcBean);
                if(valStr != null){
                    Object val = Converter.toType(valStr, fld.getType());
                    fld.setAccessible(true);
                    fld.set(bean, val);
                    result = true;
                }
            } catch (Exception e) {
                throw new ApplyValuesToBeanException(fldName, String.format("Can't set value to field. Msg: %s", e.getMessage()));
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
        return BeanUtils.cloneBean(bean);
    }

    public static String normalizePath(String path, char pathSeparator) {
        if(isNullOrEmpty(path))
            return null;
        String rslt = path;
        if(pathSeparator == (char)0)
            pathSeparator =  File.separatorChar;
        rslt = rslt.replace('\\', pathSeparator);
        rslt = rslt.replace('/', pathSeparator);
        rslt = (rslt.charAt(rslt.length()-1) == pathSeparator) ? rslt : rslt + pathSeparator;
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

}

