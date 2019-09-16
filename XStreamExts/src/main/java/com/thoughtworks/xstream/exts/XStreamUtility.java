package com.thoughtworks.xstream.exts;

import com.thoughtworks.xstream.XStream;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class XStreamUtility {

    private final XStream xstream = XStreamFactory.getXStream();

    private static XStreamUtility instance;
    public static synchronized XStreamUtility getInstance(){
        if (instance == null) {
            instance = new XStreamUtility();
        }
        return instance;
    }

    public <T> void toXml(T obj, OutputStream stream, String encoding) {
        try {
            String headLine = String.format("<?xml version=\"1.0\" encoding=\"%s\"?>", encoding);
            if (obj != null) {
                xstream.processAnnotations(obj.getClass());
                final PrintStream printStream = new PrintStream(stream, true, encoding);
                printStream.print(headLine);
                xstream.toXML(obj, stream);
            }
        }catch(UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void toXml(T obj, OutputStream stream) {
        toXml(obj, stream, "utf-8");
    }

    public String toXml(Object obj, String encoding) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        toXml(obj, output, encoding);
        return output.toString();
    }

    public <T> T toJavaBean(String xmlStr){
        return (T)xstream.fromXML(xmlStr);
    }
}