package com.thoughtworks.xstream.exts;

import java.io.*;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.XStream;

public final class XStreamTranslator {
    private XStream xstream = null;
    private XStreamTranslator(){
        xstream = new XStream();
        xstream.ignoreUnknownElements();
    }

    public String toXMLString(Object object) {
        return xstream.toXML(object);
    }

    public Object toObject(String xml) {
        return (Object) xstream.fromXML(xml);
    }

    public static XStreamTranslator getInstance(){
        return new XStreamTranslator();
    }

    public <T> T toObject(File xmlFile) throws IOException {
        return (T)xstream.fromXML(new FileReader(xmlFile));
    }

    public void toXMLStream(Object objTobeXMLTranslated, OutputStream stream) throws IOException {
        if(objTobeXMLTranslated != null) {
            xstream.processAnnotations(objTobeXMLTranslated.getClass());
            xstream.toXML(objTobeXMLTranslated, stream);
        }
    }

    public void toXMLFile(Object objTobeXMLTranslated, String fileName) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        xstream.toXML(objTobeXMLTranslated, writer);
        writer.close();
    }

    public void toXMLFile(Object objTobeXMLTranslated, String fileName, List omitFieldsRegXList) throws IOException {
        xstreamInitializeSettings(objTobeXMLTranslated, omitFieldsRegXList);
        toXMLFile(objTobeXMLTranslated, fileName);
    }

    public void xstreamInitializeSettings(Object objTobeXMLTranslated, List omitFieldsRegXList) {
        if(omitFieldsRegXList != null && omitFieldsRegXList.size() > 0){
            Iterator itr = omitFieldsRegXList.iterator();
            while(itr.hasNext()){
                String omitEx = itr.next().toString();
                xstream.omitField(objTobeXMLTranslated.getClass(), omitEx);
            }
        }
    }

    public void toXMLFile(Object objTobeXMLTranslated) throws IOException {
        toXMLFile(objTobeXMLTranslated,objTobeXMLTranslated.getClass().getName()+".xml");
    }
}