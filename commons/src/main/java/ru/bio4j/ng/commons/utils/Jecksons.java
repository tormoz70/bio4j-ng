package ru.bio4j.ng.commons.utils;

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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
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
            if (!Strings.isNullOrEmpty(defaultDateTimeFormat)) {
                DateFormat df = new SimpleDateFormat(defaultDateTimeFormat);
                objectMapper.setDateFormat(df);
                objectMapper.getDeserializationConfig().with(df);
            }
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addDeserializer(Param.class, new ParamJsonDateDeserializer());
            simpleModule.addDeserializer(MetaType.class, new MetaTypeJsonDateDeserializer());
            objectMapper.registerModule(simpleModule);
        }
        return objectMapper;
    }

    public String encode(Object object) throws JsonProcessingException {
        return getObjectMapper().writeValueAsString(object);
    }


    public <T> T decode(String json, Class<T> targetType) throws Exception {
        return getObjectMapper().readValue(json, targetType);
    }

    public <T> T decode(String json, TypeReference<T> typeReference) throws Exception {
        return getObjectMapper().readValue(json, typeReference);
    }

    public ABean decodeABean(final String json) throws Exception {
        return getObjectMapper().readValue(json, new TypeReference<ABean>() {
        });
    }

    public List<ABean> decodeABeans(String json) throws Exception {
        if (json.trim().startsWith("[")) {
            return getObjectMapper().readValue(json, new TypeReference<List<ABean>>() {
            });
        } else {
            return Arrays.asList(decodeABean(json));
        }
    }

    public static class ParamJsonDateDeserializer extends JsonDeserializer<Param> {

        public static class ParamPOJO extends Param {
        }

        @Override
        public Param deserialize(JsonParser jsonParser,
                                 DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

            Param deserializedParam = jsonParser.readValuesAs(ParamPOJO.class).next();
            if (Arrays.asList(MetaType.DATE, MetaType.UNDEFINED).contains(Utl.nvl(deserializedParam.getType(), MetaType.UNDEFINED)) && deserializedParam.getValue() != null && deserializedParam.getValue() instanceof String) {
                boolean isValueDateAsString = Strings.compare(DateTimeParser.getInstance().detectFormat((String) deserializedParam.getValue()), Jecksons.getInstance().defaultDateTimeFormat, true);
                if (isValueDateAsString) {
                    try {
                        Date val = DateTimeParser.getInstance().pars((String) deserializedParam.getValue(), Jecksons.getInstance().defaultDateTimeFormat);
                        deserializedParam.setValue(val);
                    } catch (DateParseException e) {
                        throw new RuntimeException(e);
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
