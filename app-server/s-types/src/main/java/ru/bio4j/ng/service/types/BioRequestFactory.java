package ru.bio4j.ng.service.types;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

import java.lang.reflect.Type;

public abstract class BioRequestFactory implements ObjectFactory {

    public static class Ping extends BioRequestFactory {
        @Override
        public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) throws Exception {
            return null;
        }

    }

    public static class GetDataSet extends BioRequestFactory {
        @Override
        public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) throws Exception {
            return null;
        }

    }

    public static class GetRecord extends BioRequestFactory {
        @Override
        public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) throws Exception {
            return null;
        }

    }


}
