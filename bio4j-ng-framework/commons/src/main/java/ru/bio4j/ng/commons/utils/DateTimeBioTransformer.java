package ru.bio4j.ng.commons.utils;

import flexjson.JSONException;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import flexjson.transformer.AbstractTransformer;
import ru.bio4j.ng.commons.converter.DateTimeParser;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: brandongoodin
 * Date: Dec 12, 2007
 * Time: 11:20:39 PM
 */
public class DateTimeBioTransformer extends AbstractTransformer implements ObjectFactory {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final SimpleDateFormat simpleDateFormatter = new SimpleDateFormat(DATE_TIME_FORMAT);

    public static Date parse(String value) {
        try {
            String v = value.toString();
            v = v.length() > 19 ? v.substring(0, 19) : v;
            return DateTimeParser.getInstance().pars(v);
        } catch (Exception e){
            throw new JSONException(String.format("Failed to parse %s with %s pattern.", value, simpleDateFormatter.toPattern()), e);
        }
    }

    public void transform(Object value) {
        getContext().writeQuoted(simpleDateFormatter.format(value));
    }

    public Object instantiate(ObjectBinder context, Object value, Type targetType, Class targetClass) {
        return parse(value.toString());
    }
}
