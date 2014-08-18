package ru.bio4j.ng.service.types;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

import java.lang.reflect.Type;

/**
 * Created by ayrat on 18.08.2014.
 */
public abstract class BioRequestFactory implements ObjectFactory {

    public static class Ping extends BioRequestFactory {
        @Override
        public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) throws Exception {
            return null;
        }

    }

    public static class GetData extends BioRequestFactory {
        @Override
        public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) throws Exception {
            return null;
        }

    }


}
