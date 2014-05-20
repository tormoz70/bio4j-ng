package ru.bio4j.ng.commons.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.bio4j.ng.commons.converter.ConvertValueException;
import ru.bio4j.ng.commons.converter.Converter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * Created by ayrat on 20.05.14.
 */
public class Doms {

    public static Document loadDocument(InputStream inputStream) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setValidating(false);
        DocumentBuilder builder = f.newDocumentBuilder();
        return builder.parse(inputStream);
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

    public static Element findElem(Document from, String path) {
        return findElem(from.getDocumentElement(), path);
    }

    public static <T> T getAttribute(Element elem, String attrName, T defaultVal, Class<T> type) throws ConvertValueException {
        if (elem.hasAttribute(attrName)) {
            String strVal = elem.getAttribute(attrName);
            return Converter.toType(strVal, type);
        } else
            return defaultVal;
    }

}
