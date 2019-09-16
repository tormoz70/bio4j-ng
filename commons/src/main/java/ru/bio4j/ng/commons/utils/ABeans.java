package ru.bio4j.ng.commons.utils;

import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.model.transport.ABean;

import java.util.HashMap;
import java.util.List;

public class ABeans {

    public static HashMap<String, Object> extractBean(final HashMap<String, Object> bean, final String attrName) {
        if(bean != null) {
            for (String key : bean.keySet()) {
                if (Strings.compare(key, attrName, true)) {
                    if(bean.get(key) instanceof HashMap)
                    return (HashMap<String, Object>) bean.get(key);
                }
            }
        }
        return null;
    }

    public static String getNextPathItem(String path) {
        if(!Strings.isNullOrEmpty(path))
            return path.indexOf("/") >= 0 ? path.substring(0, path.indexOf("/")) : path;
        return null;
    }

    public static String cutNextPathItem(String path) {
        if(!Strings.isNullOrEmpty(path) && path.indexOf("/") >= 0)
            return path.substring(path.indexOf("/")+1);
        return null;
    }

    public static <T> T extractAttrFromBean(final HashMap<String, Object> bean, final String path, Class<T> clazz, T defauldValue) {
        if(bean != null) {
            if(Strings.isNullOrEmpty(path))
                return defauldValue;
            if(path.indexOf("/") >= 0){
                String nextLevelAttr = getNextPathItem(path);
                HashMap<String, Object> hextBean = extractBean(bean, nextLevelAttr);
                return extractAttrFromBean(hextBean, cutNextPathItem(path), clazz, defauldValue);
            } else {
                for (String key : bean.keySet()) {
                    if (Strings.compare(key, path, true))
                        return Converter.toType(bean.get(key), clazz);
                }
            }
        }
        return defauldValue;
    }

    public static HashMap<String, Object> extractBeanFromBean(final HashMap<String, Object> bean, final String path) throws Exception {
        if(bean != null) {
            if(Strings.isNullOrEmpty(path))
                return null;
            if(path.indexOf("/") >= 0){
                String nextLevelAttr = getNextPathItem(path);
                HashMap<String, Object> hextBean = extractBean(bean, nextLevelAttr);
                return extractBeanFromBean(hextBean, cutNextPathItem(path));
            } else {
                for (String key : bean.keySet()) {
                    if (Strings.compare(key, path, true))
                        return (HashMap<String, Object>)bean.get(key);
                }
            }
        }
        return null;
    }

    public static List<HashMap<String, Object>> extractBeansFromBean(final HashMap<String, Object> bean, final String path) throws Exception {
        if(bean != null) {
            if(Strings.isNullOrEmpty(path))
                return null;
            if(path.indexOf("/") >= 0){
                String nextLevelAttr = getNextPathItem(path);
                HashMap<String, Object> hextBean = extractBean(bean, nextLevelAttr);
                return extractBeansFromBean(hextBean, cutNextPathItem(path));
            } else {
                for (String key : bean.keySet()) {
                    if (Strings.compare(key, path, true))
                        return (List<HashMap<String, Object>>)bean.get(key);
                }
            }
        }
        return null;
    }

    public static void renameAttr(final HashMap<String, Object> bean, final String oldAttrName, final String newAttrName) throws Exception {
        Object val = null;
        String foundKey = null;
        for (String key : bean.keySet()) {
            if (Strings.compare(key, oldAttrName, true)){
                val = bean.get(key);
                foundKey = key;
            }
        }
        if(foundKey != null){
            bean.remove(foundKey);
            bean.put(newAttrName, val);
        }
    }
}
