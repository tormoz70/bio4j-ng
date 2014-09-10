package ru.bio4j.ng.commons.utils;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.util.List;

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

    @Test
    public void testFindElems1() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("org-sroom-list.xml");
        Document document = Doms.loadDocument(inputStream);
        List<Element> elements = Doms.findElems(document.getDocumentElement(), "SQL/text");
        Assert.assertEquals(elements.size(), 3);
        String updExpected = "begin eorg_help_tst.edit_showrooms1($PRMLIST); end;";
        String updActual = elements.get(1).getTextContent();
        Assert.assertEquals(updActual, updExpected);
    }

    @Test
    public void testFindElems2() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("org-sroom-list.xml");
        Document document = Doms.loadDocument(inputStream);
        List<Element> elements = Doms.findElems(document.getDocumentElement(), "/cursor/SQL/text");
        Assert.assertEquals(elements.size(), 3);
        String updExpected = "begin eorg_help_tst.edit_showrooms1($PRMLIST); end;";
        String updActual = elements.get(1).getTextContent();
        Assert.assertEquals(updActual, updExpected);
    }

}
