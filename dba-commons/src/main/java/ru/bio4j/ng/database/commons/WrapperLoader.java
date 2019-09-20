package ru.bio4j.ng.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.WrapQueryType;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

public class WrapperLoader {
    private static final Logger LOG = LoggerFactory.getLogger(WrapperLoader.class);

    private static DocumentBuilder createDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    private static Document loadDocument(final InputStream is) {
        try {
            final DocumentBuilder db = createDocumentBuilder();
            return db.parse(is);
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static Map<WrapQueryType, String> loadQueries(final InputStream is, final String dbmsName) {
        final Map<WrapQueryType, String> map = new EnumMap<>(WrapQueryType.class);
        //загрузка запрсоов из XML
        final Document doc = loadDocument(is);
        final NodeList nl = doc.getElementsByTagName("template");
        int len = nl.getLength();
        for (int i = 0; i < len; ++i) {
            Node n = nl.item(i);
            String name = null;
            Node nameNode = n.getAttributes().getNamedItem("type");
            if (nameNode != null) {
                name = nameNode.getTextContent();
                map.put(WrapQueryType.valueOf(name.toUpperCase()), n.getTextContent());
            }
        }
        if(LOG.isDebugEnabled())
            LOG.debug("loaded {} queries for cursor.wrapper", map.size());
        return unmodifiableMap(map);
    }
}
