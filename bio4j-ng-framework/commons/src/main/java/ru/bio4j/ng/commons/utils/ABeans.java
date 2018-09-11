package ru.bio4j.ng.commons.utils;

import ru.bio4j.ng.model.transport.ABean;

public class ABeans {
    public static <T> T extractAttrFromBean(final ABean bean, final String attrName, Class<T> clazz, T defauldValue) {
        if(bean != null) {
            if (bean.containsKey(attrName))
                return  (T) bean.get(attrName);
        }
        return defauldValue;
    }
}
