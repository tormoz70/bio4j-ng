package ru.bio4j.ng.commons.utils;

import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.model.transport.ABean;

public class ABeans {
    public static <T> T extractAttrFromBean(final ABean bean, final String attrName, Class<T> clazz, T defauldValue) throws Exception {
        if(bean != null) {
            for(String key : bean.keySet()){
                if(Strings.compare(key, attrName, true))
                    return Converter.toType(bean.get(key), clazz);
            }
        }
        return defauldValue;
    }
}
