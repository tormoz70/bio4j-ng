package ru.bio4j.ng.commons.utils;

//import flexjson.*;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import sun.org.mozilla.javascript.internal.json.JsonParser;

import flexjson.*;
import flexjson.transformer.AbstractTransformer;
import flexjson.transformer.DateTransformer;
import ru.bio4j.ng.commons.converter.Types;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.MetaType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;

public class Jsons {
	public static String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static class ExceptionTransformer extends AbstractTransformer {

        public void transform(Object value) {
            JSONContext context = getContext();
            String valueStr = new JSONSerializer()
                    .exclude("cause", "localizedMessage", "stackTraceDepth","stackTrace")
                    .serialize(value);
            context.write(valueStr);
        }

    }

    public static class MetaTypeTransformer extends AbstractTransformer {

        public void transform(Object value) {
            JSONContext context = getContext();
            String valueStr = "\""+value.toString().toLowerCase()+"\"";
            context.write(valueStr);
        }

    }

	public static String encode(Object object) {
		JSONSerializer serializer = new JSONSerializer();
		return serializer
                .exclude("class")
				.transform(new DateTransformer(DATE_TIME_FORMAT), Date.class)
				.transform(new ExceptionTransformer(), Exception.class)
                .transform(new MetaTypeTransformer(), MetaType.class)
				.deepSerialize(object);
	}

//    public static String encode(Object object) throws Exception {
//        ObjectMapper mapper = new ObjectMapper();
//        return mapper.writeValueAsString(object);
//    }

//	public static Object decode(String json) {
//		JSONDeserializer<Object> deserializer = new JSONDeserializer<Object>().use(Date.class, new ObjectFactory() {
//			@Override
//			public Object instantiate(ObjectBinder context, Object value, Type targetType, @SuppressWarnings("rawtypes") Class targetClass) {
//				return Types.parse((String) value, csDateTimeFormat);
//			}
//		});
//		return deserializer.deserialize(json);
//	}

    private static <T> JSONDeserializer<T> createDeserializer(T target) {
        return new JSONDeserializer<T>()
                .use(Date.class, new ObjectFactory() {
                    @Override
                    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) throws Exception {
                        return Types.parse((String) value, DATE_TIME_FORMAT);
                    }
                })
                .use(MetaType.class, new ObjectFactory() {
                    @Override
                    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) throws Exception {
                        String valStr = (String)value;
                        return MetaType.decode(valStr);
                    }
                })
                .use(StackTraceElement.class, new ObjectFactory() {
                    @Override
                    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) throws Exception {
                        HashMap<String, ?> vals = (HashMap<String, ?>) value;
                        String className = (String) vals.get("className");
                        String methodName = (String) vals.get("methodName");
                        String fileName = (String) vals.get("fileName");
                        int lineNumber = (Integer) vals.get("lineNumber");
                        return new StackTraceElement(className, methodName, fileName, lineNumber);
                    }
                })
                .use(Exception.class, new ObjectFactory() {
                    @Override
                    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) throws Exception {
                        HashMap<String, ?> vals = (HashMap<String, ?>) value;
                        String message = (String) vals.get("message");
                        return new Exception(message);
                    }
                })
                .use(BioError.class, new ObjectFactory() {
                    @Override
                    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) throws Exception {
                        HashMap<String, ?> vals = (HashMap<String, ?>) value;
                        String message = (String) vals.get("message");
                        Constructor<?> ctor = targetClass.getConstructor(String.class);
                        BioError object = (BioError)ctor.newInstance(new Object[]{message});
                        return object;
                    }
                });

    }
    private static <T> JSONDeserializer<T> createDeserializer() {
        return createDeserializer(null);
    }

    public static <T> T decode(String json, T target) throws Exception {
        return createDeserializer(target).deserializeInto(json, target);
    }

	public static <T> T decode(String json, Class<T> targetClass) throws Exception {
        if(targetClass == null)
            throw new IllegalAccessException("Parameter targetClass cannot be null!");
        T newResult = null;
        newResult = targetClass.newInstance();
		return decode(json, newResult);
	}

    public static <T> T decode(String json, ObjectFactory factory) throws Exception {
        JSONDeserializer<T> d = createDeserializer();
        return d.deserialize(json, factory);
    }

}
