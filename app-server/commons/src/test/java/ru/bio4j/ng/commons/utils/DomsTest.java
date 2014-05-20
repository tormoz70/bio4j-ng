package ru.bio4j.ng.commons.utils;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.InputStream;

/**
 * Created by ayrat on 20.05.14.
 */
public class DomsTest {
    @Test
    public void testFindElem() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("film-registry.xml");
        Document document = Doms.loadDocument(inputStream);
        Element element = Doms.findElem(document.getDocumentElement(), "SQL/text");
        Assert.assertTrue(element != null && element.getNodeName().equals("text"));
        element = Doms.findElem(document.getDocumentElement(), "/cursor/SQL/text");
        Assert.assertTrue(element != null && element.getNodeName().equals("text"));
        element = Doms.findElem(document, "/cursor/SQL/text");
        Assert.assertTrue(element != null && element.getNodeName().equals("text"));
        element = Doms.findElem(document, "/cursor");
        Boolean m = Doms.getAttribute(element, "multiselection", null, Boolean.class);
        Assert.assertTrue(m);
    }
}
