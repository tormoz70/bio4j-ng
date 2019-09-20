package ru.bio4j.ng.commons.utils;

import ru.bio4j.ng.commons.types.DelegateCheck;

import java.util.ArrayList;
import java.util.List;

public class Lists {
    public static <T> List<T> select(List<T> list, DelegateCheck<T> check) {
        List<T> result = new ArrayList<>();
        if (list != null && check != null) {
            for (T item : list)
                try {
                    if (check.callback(item))
                        result.add(item);
                } catch (Exception e) {
                    throw Utl.wrapErrorAsRuntimeException(e);
                }
        }
        return result;
    }
    public static <T> T first(List<T> list, DelegateCheck<T> check) {
        if(check != null)
            list = select(list, check);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
    public static <T> T first(List<T> list) throws Exception {
        return first(list, null);
    }

    public static <T> T last(List<T> list, DelegateCheck<T> check) throws Exception {
        if(check != null)
            list = select(list, check);
        if (list != null && list.size() > 0) {
            return list.get(list.size()-1);
        }
        return null;
    }
    public static <T> T last(List<T> list) throws Exception {
        return last(list, null);
    }


}
