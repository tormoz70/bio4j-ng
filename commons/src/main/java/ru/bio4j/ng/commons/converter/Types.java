package ru.bio4j.ng.commons.converter;

import ru.bio4j.ng.commons.utils.Strings;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Types {

    public static Object wrapPrimitive(Object value) {
        Class<?> inType = (value == null) ? null : value.getClass();
        if (inType == null || !inType.isPrimitive())
            return value;
        if (inType == boolean.class)
            return new Boolean((boolean) value);
        if (inType == byte.class)
            return new Byte((byte) value);
        if (inType == short.class)
            return new Short((short) value);
        if (inType == int.class)
            return new Integer((int) value);
        if (inType == long.class)
            return new Long((long) value);
        if (inType == char.class)
            return new Character((char) value);
        if (inType == float.class)
            return new Float((float) value);
        if (inType == double.class)
            return new Double((double) value);
        return value;
    }

    public static Class<?> wrapPrimitiveType(Class<?> type) {
        if (type != null && !type.isPrimitive())
            return type;
        if (type == boolean.class)
            return Boolean.class;
        if (type == byte.class)
            return Byte.class;
        if (type == short.class)
            return Short.class;
        if (type == int.class)
            return Integer.class;
        if (type == long.class)
            return Long.class;
        if (type == char.class)
            return Character.class;
        if (type == float.class)
            return Float.class;
        if (type == double.class)
            return Double.class;
        return type;
    }

    public static Double parsDouble(String inValue) {
        DecimalFormat fmt = new DecimalFormat("##############################.################");
        DecimalFormatSymbols dfs = fmt.getDecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        fmt.setDecimalFormatSymbols(dfs);
        try {
            Object vval = fmt.parse(inValue);
            if(vval.getClass() == Double.class)
                return (Double)vval;
            if(vval.getClass() == Long.class)
                return ((Long)vval).doubleValue();
            return null;
        } catch (ParseException e) {
            return null;
        }
    }

    public static <T> T string2Number(String inValue, Class<T> targetType) {
        inValue = Strings.isNullOrEmpty(inValue) ? "0" : inValue.trim().replace(" ", "").replace(',', '.');
        return number2Number(parsDouble(inValue), targetType);
    }

    public static <T> T char2Number(Character inValue, Class<T> targetType) {
        inValue = inValue == null ? '0' : inValue;
        return number2Number(parsDouble(""+inValue), targetType);
    }

    public static <T> T number2Number(Number inValue, Class<T> targetType) {
        if(inValue != null) {
            if (targetType == Byte.class)
                return (T) new Byte(inValue.byteValue());
            if (targetType == Short.class)
                return (T) new Short(inValue.shortValue());
            if (targetType == Integer.class)
                return (T) new Integer(inValue.intValue());
            if (targetType == Long.class)
                return (T) new Long(inValue.longValue());
            if (targetType == Float.class)
                return (T) new Float(inValue.floatValue());
            if (targetType == Double.class)
                return (T) new Double(inValue.doubleValue());
            if (targetType == BigDecimal.class)
                return (T) new BigDecimal(inValue.doubleValue());
            if (targetType == BigInteger.class)
                return (T) new BigInteger(inValue.toString());
        }
        return null;
    }

    public static boolean typeIsDate(Class<?> type) {
        return Date.class.isAssignableFrom(type);
    }

    public static <T> T date2Date(java.util.Date inValue, Class<T> targetType) {
        if (targetType == java.util.Date.class)
            return (T)new java.util.Date(inValue.getTime());
        if (targetType == java.sql.Date.class)
            return (T)new java.sql.Date(inValue.getTime());
        if (targetType == java.sql.Timestamp.class)
            return (T)new java.sql.Timestamp(inValue.getTime());
        return null;
    }

    public static boolean typeIsInteger(Class<?> type) {
        if(Arrays.asList(int.class, Integer.class, byte.class, Byte.class,
                short.class, Short.class, long.class, Long.class).contains(type))
            return true;
        return false;
    }

    public static boolean typeIsNumber(Class<?> type) {
        if(Arrays.asList(int.class, byte.class, short.class, long.class, float.class, double.class).contains(type))
            return true;
        return Number.class.isAssignableFrom(type);
    }

    public static boolean typeIsReal(Class<?> type) {
        if(Arrays.asList(float.class, double.class).contains(type))
            return true;
        return Number.class.isAssignableFrom(type);
    }

    public static Date parsDate(String value) throws ConvertValueException {
        try {
            return DateTimeParser.getInstance().pars(value);
        } catch (DateParseException ex) {
            throw new ConvertValueException(value, String.class, Date.class);
        }
    }

    public static void nop(){
        return;
    }

    private static final String CLASS_NAME_PREFIX = "class ";
    private static final String INTERFACE_NAME_PREFIX = "interface ";
    public static String getClassName(Type type) {
        if (type==null) {
            return "";
        }
        String className = type.toString();
        if (className.startsWith(CLASS_NAME_PREFIX)) {
            className = className.substring(CLASS_NAME_PREFIX.length());
        } else if (className.startsWith(INTERFACE_NAME_PREFIX)) {
            className = className.substring(INTERFACE_NAME_PREFIX.length());
        }
        return className;
    }

    public static Class<?> getClass(Type type) {
        String className = getClassName(type);
        if (className==null || className.isEmpty()) {
            return null;
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }


	public static Date parse(String str, String formatStr) {
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		try {
	        return format.parse(str);
        } catch (ParseException e) {
        	return null;
        }
	}
	
	public static Date minValue(){
		return new Date(Long.MIN_VALUE);
	}
	public static Date maxValue(){
		return new Date(Long.MAX_VALUE);
	}

    public static Boolean parsBoolean(String value) {
        if(Strings.isNullOrEmpty(value))
            return false;
        return  value.toLowerCase().equals("true") ||
                value.toLowerCase().equals("t") ||
                value.toLowerCase().equals("1") ||
                value.toLowerCase().equals("yes") ||
                value.toLowerCase().equals("y");
    }

    public static Boolean parsBoolean(Character value) {
        if(value == null)
            return false;
        return  Character.compare(Character.toLowerCase(value), 't') == 0 ||
                Character.compare(Character.toLowerCase(value), '1') == 0 ||
                Character.compare(Character.toLowerCase(value), 'y') == 0;
    }

    public static <T> T parsEnum(String value, Class<T> type) {
        if(!type.isEnum())
            throw new IllegalArgumentException("Parameter type mast be instance of Enum!");
        Field[] flds = type.getDeclaredFields();
        for(Field f : flds)
            if(Strings.compare(f.getName(), value, true))
                try {
                    return (T)f.get(type);
                } catch (IllegalAccessException e) {
                    return null;
                }
        return null;
    }

    public static boolean isPrimitiveOrWrapper(Class<?> type) {
        return (type.isPrimitive() && type != void.class) ||
                type == Double.class || type == Float.class || type == Long.class ||
                type == Integer.class || type == Short.class || type == Character.class ||
                type == Byte.class || type == Boolean.class || type == String.class || type == Date.class;
    }
}
