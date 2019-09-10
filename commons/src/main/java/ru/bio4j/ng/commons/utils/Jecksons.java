package ru.bio4j.ng.commons.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.FilterAndSorter;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.filter.Expression;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;
import ru.bio4j.ng.model.transport.jstore.filter.FilterBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
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

        private String defaultDateTimeFormat = null;

    public void setDefaultDateTimeFormat(String defaultDateTimeFormat) {
        this.defaultDateTimeFormat = defaultDateTimeFormat;
    }

    private volatile ObjectMapper objectMapper;
        private synchronized ObjectMapper getObjectMapper() {
            if(objectMapper == null) {
                objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                if(!Strings.isNullOrEmpty(defaultDateTimeFormat)) {
                    DateFormat df = new SimpleDateFormat(defaultDateTimeFormat);
                    objectMapper.setDateFormat(df);
                }
            }
            return objectMapper;
        }

        public String encode(Object object) throws JsonProcessingException {
            return getObjectMapper().writeValueAsString(object);
        }


        public <T> T decode(String json, Class<T> targetType) throws Exception {
            return getObjectMapper().readValue(json, targetType);
        }

}
