package ru.bio4j.ng.commons.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ru.bio4j.ng.commons.converter.ConvertValueException;
import ru.bio4j.ng.commons.converter.Converter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ayrat on 20.05.14.
 */
public class Doms {

    public static Document loadDocument(InputStream inputStream) {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setValidating(false);
        try {
            DocumentBuilder builder = f.newDocumentBuilder();
            return builder.parse(inputStream);
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static Element findElem(Element from, String path) {
        if(Strings.isNullOrEmpty(path))
            return from;
        if(Strings.compare(path, "/", true))
            return from.getOwnerDocument().getDocumentElement();
        NodeList children = from.getChildNodes();
        if(path.startsWith("/")) {
            children = from.getOwnerDocument().getChildNodes();
            path = path.substring(1);
        }
        String elemName = Strings.getFirstItem(path, "/");
        path = Strings.cutFirstItem(path, "/");
        for(int i=0; i<children.getLength(); i++) {
            if(children.item(i) instanceof Element && children.item(i).getNodeName().equals(elemName)){
                if(Strings.isNullOrEmpty(path))
                    return (Element)children.item(i);
                return findElem((Element)children.item(i), path);
            }
        }
        return null;
    }

    private static void _findElems(Element from, String path, List<Element> rslt) {
        NodeList children = from.getChildNodes();
        if(path.startsWith("/")) {
            children = from.getOwnerDocument().getChildNodes();
            path = path.substring(1);
        }
        String elemName = Strings.getFirstItem(path, "/");
        path = Strings.cutFirstItem(path, "/");
        for(int i=0; i<children.getLength(); i++) {
            if(children.item(i) instanceof Element && children.item(i).getNodeName().equals(elemName)){
                if(Strings.isNullOrEmpty(path))
                    rslt.add((Element) children.item(i));
                else
                    _findElems((Element)children.item(i), path, rslt);
            }
        }
    }

    public static List<Element> findElems(Element from, String path) {
        if(Strings.isNullOrEmpty(path))
            return null;
        if(Strings.compare(path, "/", true))
            return Arrays.asList(from.getOwnerDocument().getDocumentElement());
        List<Element> rslt = new ArrayList<>();
        _findElems(from, path, rslt);
        return rslt;
    }

    public static Element findElem(Document from, String path) {
        return findElem(from.getDocumentElement(), path);
    }

    public static <T> T getAttribute(Element elem, String attrName, T defaultVal, Class<T> type) {
        if (elem.hasAttribute(attrName)) {
            String strVal = elem.getAttribute(attrName);
            return Converter.toType(strVal, type);
        } else
            return defaultVal;
    }

}
