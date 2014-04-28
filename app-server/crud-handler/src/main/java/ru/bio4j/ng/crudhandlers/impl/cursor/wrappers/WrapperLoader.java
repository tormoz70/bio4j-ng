package ru.bio4j.ng.crudhandlers.impl.cursor.wrappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

public class WrapperLoader {
    private static final Logger LOG = LoggerFactory.getLogger(WrapperLoader.class);

    public static Map<WrapQueryType, String> loadQueries(String dbmsName) throws Exception {
        final Map<WrapQueryType, String> map = new EnumMap<>(WrapQueryType.class);
        //загрузка запрсоов из XML
        final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final String templFileName = "/cursor/wrapper/templates/" + dbmsName + ".xml";
        final InputStream is = AbstractWrapper.class.getResourceAsStream(templFileName);
        if(is == null)
            throw new IllegalArgumentException(String.format("Resource %s not found!", templFileName));
        final Document doc = db.parse(is);
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
        LOG.debug("loaded {} queries for cursor.wrapper", map.size());
        return unmodifiableMap(map);
    }
}
