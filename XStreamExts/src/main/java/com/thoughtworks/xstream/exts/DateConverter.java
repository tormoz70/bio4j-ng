package com.thoughtworks.xstream.exts;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateConverter implements Converter {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT);

    public boolean canConvert(Class clazz) {
        // This converter is only for Calendar fields.
        return Date.class.isAssignableFrom(clazz);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer,
                        MarshallingContext context) {
//        Calendar calendar = (Calendar) value;
//        Date date = calendar.getTime();
//        writer.setValue(formatter.format(date));

        writer.setValue(formatter.format(value));
    }

    public Object unmarshal(HierarchicalStreamReader reader,
                            UnmarshallingContext context) {
//        GregorianCalendar calendar = new GregorianCalendar();
//        try {
//            calendar.setTime(formatter.parse(reader.getValue()));
//        } catch (ParseException e) {
//            throw new ConversionException(e.getMessage(), e);
//        }
//        return calendar;

        try {
            return formatter.parse( reader.getValue().toString().substring(0, 19) );
        } catch (ParseException e) {
            throw new ConversionException(String.format( "Failed to parse %s with %s pattern.", reader.getValue().toString(), formatter.toPattern() ), e );
        }

    }
}