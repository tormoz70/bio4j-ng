package ru.bio4j.ng.commons.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.exts.XStreamFactory;

import java.io.OutputStream;
import java.io.PrintStream;

public class XStreamUtility {

    private final XStream xstream = XStreamFactory.getXStream();

    private static XStreamUtility instance;
    public static synchronized XStreamUtility getInstance(){
        if (instance == null) {
            instance = new XStreamUtility();
        }
        return instance;
    }

    public <T> void toXml(T obj, OutputStream stream, String encoding) throws Exception {
        String headLine = String.format("<?xml version=\"1.0\" encoding=\"%s\"?>", encoding);
        if(obj != null) {
            xstream.processAnnotations(obj.getClass());
            final PrintStream printStream = new PrintStream(stream);
            printStream.print(headLine);
            xstream.toXML(obj, stream);
        }
    }

    public <T> void toXml(T obj, OutputStream stream) throws Exception {
        toXml(obj, stream, "utf-8");
    }

    public <T> T toJavaBean(String xmlStr){
        return (T)xstream.fromXML(xmlStr);
    }
}