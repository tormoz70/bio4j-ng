package ru.bio4j.ng.commons.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import ru.bio4j.ng.commons.converter.DateParseException;
import ru.bio4j.ng.commons.converter.DateTimeParser;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.filter.Expression;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;
import ru.bio4j.ng.model.transport.jstore.filter.FilterBuilder;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Jecksons {

    private Jecksons() { /* hidden constructor */ }

    public static Jecksons getInstance() {
        return SingletonContainer.INSTANCE;
    }

    private static class SingletonContainer {
        public static final Jecksons INSTANCE;

        static {
            INSTANCE = new Jecksons();
        }
    }

    //private String defaultDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private String defaultDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss";

    public void setDefaultDateTimeFormat(String defaultDateTimeFormat) {
        this.defaultDateTimeFormat = defaultDateTimeFormat;
    }

    public String getDefaultDateTimeFormat() {
        return defaultDateTimeFormat;
    }

    private volatile ObjectMapper objectMapper;

    private synchronized ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(JsonParser.Feature.ALLOW_TRAILING_COMMA, true);
            if (!Strings.isNullOrEmpty(defaultDateTimeFormat)) {
                DateFormat df = new SimpleDateFormat(defaultDateTimeFormat);
                objectMapper.setDateFormat(df);
                objectMapper.getDeserializationConfig().with(df);
            }
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addDeserializer(Param.class, new ParamJsonDateDeserializer());
            simpleModule.addDeserializer(MetaType.class, new MetaTypeJsonDateDeserializer());
            simpleModule.setMixInAnnotation(Exception.class, MixInException.class);
            objectMapper.registerModule(simpleModule);
        }
        return objectMapper;
    }

    public String encode(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }


    public <T> T decode(String json, Class<T> targetType) {
        try {
            return getObjectMapper().readValue(json, targetType);
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public <T> T decode(String json, TypeReference<T> typeReference) {
        try {
            return getObjectMapper().readValue(json, typeReference);
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public ABean decodeABean(final String json) {
        try {
            return getObjectMapper().readValue(json, new TypeReference<ABean>() {
            });
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public List<ABean> decodeABeans(String json) {
        try {
            if (json.trim().startsWith("[")) {
                return getObjectMapper().readValue(json, new TypeReference<List<ABean>>() {
                });
            } else {
                return Arrays.asList(decodeABean(json));
            }
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
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

    public Expression decodeFilter(String json) {
        HashMap<String, Object> filterJsonObj = decodeABean(json);
        return parsExprationLevel(filterJsonObj).get(0);
    }

    public FilterAndSorter decodeFilterAndSorter(String json) {
        HashMap<String, Object> filterAndSorterJsonObj = decodeABean(json);
        FilterAndSorter filterAndSorter = new FilterAndSorter();
        filterAndSorter.setFilter(new Filter());
        if(filterAndSorterJsonObj.containsKey("filter") || filterAndSorterJsonObj.containsKey("sorter"))
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
    // ******************** MixIn ***************************************************************************************************************
    abstract class MixInException {
        @JsonIgnore
        abstract StackTraceElement[] getStackTrace();
        @JsonIgnore
        abstract Throwable getCause();
        @JsonIgnore
        abstract String getLocalizedMessage();
        @JsonIgnore
        abstract Throwable[] getSuppressed();

        @JsonIgnore
        abstract Throwable getRootCause();
    }
    // ******************** Deserializers ***************************************************************************************************************
    public static class ParamJsonDateDeserializer extends JsonDeserializer<Param> {

        public static class ParamPOJO extends Param {
        }

        @Override
        public Param deserialize(JsonParser jsonParser,
                                 DeserializationContext deserializationContext) throws IOException {

            Param deserializedParam = jsonParser.readValuesAs(ParamPOJO.class).next();
            if (Arrays.asList(MetaType.DATE, MetaType.UNDEFINED).contains(Utl.nvl(deserializedParam.getType(), MetaType.UNDEFINED)) && deserializedParam.getValue() != null && deserializedParam.getValue() instanceof String) {
                boolean isValueDateAsString = Strings.compare(DateTimeParser.getInstance().detectFormat((String) deserializedParam.getValue()), Jecksons.getInstance().defaultDateTimeFormat, true);
                if (isValueDateAsString) {
                    try {
                        Date val = DateTimeParser.getInstance().pars((String) deserializedParam.getValue(), Jecksons.getInstance().defaultDateTimeFormat);
                        deserializedParam.setValue(val);
                    } catch (DateParseException e) {
                        throw Utl.wrapErrorAsRuntimeException(e);
                    }
                }
            }
            return deserializedParam;
        }

    }

    public static class MetaTypeJsonDateDeserializer extends JsonDeserializer<MetaType> {

        public static class ParamPOJO extends Param {
        }

        @Override
        public MetaType deserialize(JsonParser jsonParser,
                                 DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

            MetaType deserializedMetaType = MetaType.decode(jsonParser.getText());
            return deserializedMetaType;
        }

    }


}
