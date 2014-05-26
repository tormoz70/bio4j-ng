package ru.bio4j.ng.commons.utils;

//import flexjson.*;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import sun.org.mozilla.javascript.internal.json.JsonParser;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import flexjson.transformer.DateTransformer;
import ru.bio4j.ng.commons.converter.Types;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;

public class Jsons {
	public static String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	public static String encode(Object object) {
		JSONSerializer serializer = new JSONSerializer();
		return serializer
                .exclude("class")
				.transform(new DateTransformer(DATE_TIME_FORMAT), Date.class)
				.transform(new ExceptionTransformer(), Exception.class)
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

    public static <T> T decode(String json, T target) throws Exception {
        JSONDeserializer<T> deserializer = new JSONDeserializer<T>()
                .use(Date.class, new ObjectFactory() {
                    @Override
                    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
                        return Types.parse((String) value, DATE_TIME_FORMAT);
                    }
                })
                .use(StackTraceElement.class, new ObjectFactory() {
                    @Override
                    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
                        HashMap<String, ?> vals = (HashMap<String,?>)value;
                        String className = (String)vals.get("className");
                        String methodName = (String)vals.get("methodName");
                        String fileName = (String)vals.get("fileName");
                        int lineNumber = (Integer)vals.get("lineNumber");
                        return new StackTraceElement(className, methodName, fileName, lineNumber);
                    }
                });
        return deserializer.deserializeInto(json, target);
    }

	public static <T> T decode(String json, Class<T> targetClass) throws Exception {
        T newResult = null;
        newResult = targetClass.newInstance();
		return decode(json, newResult);
	}

}
