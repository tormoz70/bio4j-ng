package com.thoughtworks.xstream.exts;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.naming.NameCoder;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDomDriver;

import java.io.Writer;
import java.lang.reflect.Field;

public class XStreamFactory {

    private static Field findField(String name, Class clazz){
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            XStreamAlias xStreamAlias = field.getAnnotation(XStreamAlias.class);
            String alias = (xStreamAlias != null) ? xStreamAlias.value() : field.getName();
            if(name.equalsIgnoreCase(alias))
                return field;

        }
        return null;

    }

    public static XStream getXStream() {
        final NameCoder nameCoder = new NoNameCoder ();
        XStream xStream = new XStream(new XppDomDriver (nameCoder) {
            @Override
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new PrettyPrintWriter (out, nameCoder) {
                    boolean cdata = false;
                    Class<?> targetClass = null;

                    @Override
                    public void startNode(String name, Class clazz) {
                        super.startNode(name, clazz);
                        if(targetClass == null)
                            targetClass = clazz;
                        if(findField(name, targetClass) == null)
                            targetClass = clazz;
                        cdata = needCDATA(name, targetClass);
                    }

                    @Override
                    protected void writeText(QuickWriter writer, String text) {
                        if (cdata) {
                            writer.write("<![CDATA[");
                            writer.write(text);
                            writer.write("]]>");
                        } else {
                            writer.write(text);
                        }
                    }
                };
            }
        });
        return xStream;
    }

    private static boolean needCDATA(String fieldAlias, Class<?> targetClass){
        boolean cdata = false;
        cdata = existsCDATA(fieldAlias, targetClass);
        if(cdata) return cdata;
        Class<?> superClass = targetClass.getSuperclass();
        while(!superClass.equals(Object.class)){
            cdata = existsCDATA(fieldAlias, superClass);
            if(cdata) return cdata;
            superClass = superClass.getSuperclass();
        }
        return false;
    }

    private static boolean existsCDATA(String fieldAlias, Class<?> clazz){
        Field field = findField(fieldAlias, clazz);
        return field != null && field.getAnnotation(XStreamCDATA.class) != null;
    }
}
