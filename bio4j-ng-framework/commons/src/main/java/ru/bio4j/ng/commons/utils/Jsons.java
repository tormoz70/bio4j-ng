package ru.bio4j.ng.commons.utils;

//import flexjson.*;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import sun.org.mozilla.javascript.internal.json.JsonParser;

import flexjson.*;
import flexjson.transformer.AbstractTransformer;
import flexjson.transformer.DateTransformer;
import ru.bio4j.ng.commons.converter.Types;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.FilterAndSorter;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.filter.And;
import ru.bio4j.ng.model.transport.jstore.filter.Expression;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;
import ru.bio4j.ng.model.transport.jstore.filter.FilterBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.*;

public class Jsons {

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
				.transform(new DateTimeBioTransformer(), Date.class)
				.transform(new ExceptionTransformer(), Exception.class)
                .transform(new MetaTypeTransformer(), MetaType.class)
				.deepSerialize(object);
	}

    private static <T> JSONDeserializer<T> createDeserializer(T target) {
        return new JSONDeserializer<T>()
                .use(Date.class, new ObjectFactory() {
                    @Override
                    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) throws Exception {
                        return DateTimeBioTransformer.parse((String) value);
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
                }).use(ABean.class, new ObjectFactory() {
                    @Override
                    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) throws Exception {
                        HashMap<String, ?> vals = (HashMap<String, ?>) value;
                        ABean rslt = new ABean();
                        for(String key : vals.keySet())
                            rslt.put(key, vals.get(key));
                        return rslt;
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

    public static HashMap<String, Object> decode(String json) throws Exception {
        HashMap<String, Object> rslt = (HashMap<String, Object>) new JSONDeserializer<>().deserialize(json);
        return rslt;
    }

    public static class ABeanWrapper{
        public ABean abean;
    }
    public static class ABeansWrapper{
        public List<ABean> abeans;
    }
    public static List<ABean> decodeABeans(String json) throws Exception {
        if(json.trim().startsWith("[")) {
            json = String.format("{\"abeans\":%s}", json);
            ABeansWrapper dummy1 = decode(json, ABeansWrapper.class);
            if (dummy1.abeans != null)
                return dummy1.abeans;
        }
        json = String.format("{\"abean\":%s}", json);
        ABeanWrapper dummy2 = decode(json, ABeanWrapper.class);
        if(dummy2.abean != null)
            return Arrays.asList(dummy2.abean);
        return new ArrayList<>();
    }

    public static ABean decodeABean(String json) throws Exception {
        json = String.format("{\"abean\":%s}", json);
        ABeanWrapper dummy2 = decode(json, ABeanWrapper.class);
        if(dummy2.abean != null)
            return dummy2.abean;
        return null;
    }

    private static List<Expression> parsExprationLevel(HashMap<String, Object> map) {
        List<Expression> expressions = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if(entry.getKey().equalsIgnoreCase("and")) {
                Expression e = FilterBuilder.and();
                //e.addAll(parsExprationLevel((HashMap<String, Object>)entry.getValue()));
                List<HashMap<String, Object>> itms = (List<HashMap<String, Object>>)entry.getValue();
                for(HashMap<String, Object> itm : itms)
                    e.addAll(parsExprationLevel(itm));
                expressions.add(e);
            }
            if(entry.getKey().equalsIgnoreCase("or")) {
                Expression e = FilterBuilder.or();
                List<HashMap<String, Object>> itms = (List<HashMap<String, Object>>)entry.getValue();
                for(HashMap<String, Object> itm : itms)
                    e.addAll(parsExprationLevel(itm));
                expressions.add(e);
            }
            if(entry.getKey().equalsIgnoreCase("eq")) {
                Map.Entry<String, Object> fldVal = ((HashMap<String, Object>)entry.getValue()).entrySet().iterator().next();
                Expression e = FilterBuilder.eq(fldVal.getKey(), fldVal.getValue());
                expressions.add(e);
            }
            if(entry.getKey().equalsIgnoreCase("eqi")) {
                Map.Entry<String, Object> fldVal = ((HashMap<String, Object>)entry.getValue()).entrySet().iterator().next();
                Expression e = FilterBuilder.eq(fldVal.getKey(), (String) fldVal.getValue(), true);
                expressions.add(e);
            }
            if(entry.getKey().equalsIgnoreCase("not")) {
                List<Expression> exps = parsExprationLevel((HashMap<String, Object>)entry.getValue());
                Expression e = FilterBuilder.not(exps.get(0));
                expressions.add(e);
            }
            if(entry.getKey().equalsIgnoreCase("isnull")) {
                Expression e = FilterBuilder.isNull((String)entry.getValue());
                expressions.add(e);
            }
            if(entry.getKey().equalsIgnoreCase("gt")) {
                Map.Entry<String, Object> fldVal = ((HashMap<String, Object>)entry.getValue()).entrySet().iterator().next();
                Expression e = FilterBuilder.gt(fldVal.getKey(), fldVal.getValue());
                expressions.add(e);
            }
            if(entry.getKey().equalsIgnoreCase("ge")) {
                Map.Entry<String, Object> fldVal = ((HashMap<String, Object>)entry.getValue()).entrySet().iterator().next();
                Expression e = FilterBuilder.ge(fldVal.getKey(), fldVal.getValue());
                expressions.add(e);
            }
            if(entry.getKey().equalsIgnoreCase("lt")) {
                Map.Entry<String, Object> fldVal = ((HashMap<String, Object>)entry.getValue()).entrySet().iterator().next();
                Expression e = FilterBuilder.lt(fldVal.getKey(), fldVal.getValue());
                expressions.add(e);
            }
            if(entry.getKey().equalsIgnoreCase("le")) {
                Map.Entry<String, Object> fldVal = ((HashMap<String, Object>)entry.getValue()).entrySet().iterator().next();
                Expression e = FilterBuilder.le(fldVal.getKey(), fldVal.getValue());
                expressions.add(e);
            }
            if(entry.getKey().equalsIgnoreCase("bgn")) {
                Map.Entry<String, Object> fldVal = ((HashMap<String, Object>)entry.getValue()).entrySet().iterator().next();
                String sval = (String)fldVal.getValue();
                if(!Strings.isNullOrEmpty(sval)) {
                    Expression e = FilterBuilder.bgn(fldVal.getKey(), sval, false);
                    expressions.add(e);
                }
            }
            if(entry.getKey().equalsIgnoreCase("bgni")) {
                Map.Entry<String, Object> fldVal = ((HashMap<String, Object>)entry.getValue()).entrySet().iterator().next();
                String sval = (String)fldVal.getValue();
                if(!Strings.isNullOrEmpty(sval)) {
                    Expression e = FilterBuilder.bgn(fldVal.getKey(), sval, true);
                    expressions.add(e);
                }
            }
            if(entry.getKey().equalsIgnoreCase("end")) {
                Map.Entry<String, Object> fldVal = ((HashMap<String, Object>)entry.getValue()).entrySet().iterator().next();
                String sval = (String)fldVal.getValue();
                if(!Strings.isNullOrEmpty(sval)) {
                    Expression e = FilterBuilder.end(fldVal.getKey(), sval, false);
                    expressions.add(e);
                }
            }
            if(entry.getKey().equalsIgnoreCase("endi")) {
                Map.Entry<String, Object> fldVal = ((HashMap<String, Object>)entry.getValue()).entrySet().iterator().next();
                String sval = (String)fldVal.getValue();
                if(!Strings.isNullOrEmpty(sval)) {
                    Expression e = FilterBuilder.end(fldVal.getKey(), sval, true);
                    expressions.add(e);
                }
            }
            if(entry.getKey().equalsIgnoreCase("contains")) {
                Map.Entry<String, Object> fldVal = ((HashMap<String, Object>)entry.getValue()).entrySet().iterator().next();
                String sval = (String)fldVal.getValue();
                if(!Strings.isNullOrEmpty(sval)) {
                    Expression e = FilterBuilder.contains(fldVal.getKey(), sval, false);
                    expressions.add(e);
                }
            }
            if(entry.getKey().equalsIgnoreCase("containsi")) {
                Map.Entry<String, Object> fldVal = ((HashMap<String, Object>)entry.getValue()).entrySet().iterator().next();
                String sval = (String)fldVal.getValue();
                if(!Strings.isNullOrEmpty(sval)) {
                    Expression e = FilterBuilder.contains(fldVal.getKey(), sval, true);
                    expressions.add(e);
                }
            }
        }
        return expressions;
    }

    private static List<Sort> parsSorter(HashMap<String, Object> map) {
        List<Sort> sorter = new ArrayList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Sort s = new Sort();
            s.setFieldName(entry.getKey());
            s.setDirection(Sort.Direction.valueOf(((String) entry.getValue()).toUpperCase()));
            sorter.add(s);
        }
        return sorter;
    }

//    public static Filter decodeFilter(String json) throws Exception {
//        HashMap<String, Object> filterJsonObj = (HashMap<String, Object>)new JSONDeserializer<>().deserialize(json);
//        Filter filter = new Filter();
//        filter.add(parsExprationLevel(filterJsonObj).get(0));
//        return filter;
//    }

    public static FilterAndSorter decodeFilterAndSorter(String json) throws Exception {
        HashMap<String, Object> filterAndSorterJsonObj = (HashMap<String, Object>)new JSONDeserializer<>().deserialize(json);
        FilterAndSorter filterAndSorter = new FilterAndSorter();
        filterAndSorter.setFilter(new Filter());

        for (Map.Entry<String, Object> entry : filterAndSorterJsonObj.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("filter")) {
                Expression e = parsExprationLevel((HashMap<String, Object>)entry.getValue()).get(0);
                filterAndSorter.getFilter().add(e);
            }
            if (entry.getKey().equalsIgnoreCase("sorter")) {
                filterAndSorter.setSorter(parsSorter((HashMap<String, Object>)entry.getValue()));
            }
        }
        return filterAndSorter;
    }
}
