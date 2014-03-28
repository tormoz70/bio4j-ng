package ru.bio4j.service.sql.query.wrappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.bio4j.func.Function;
import ru.bio4j.func.UnsafeFunction;
import ru.bio4j.service.sql.QueryContext;
import ru.bio4j.service.sql.util.DBTools;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.sql.DatabaseMetaData;
import java.util.EnumMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;
import static ru.bio4j.collections.Sugar.func;
import static ru.bio4j.service.sql.query.wrappers.WrapQueryType.forName;

public class WrapperLoader {
    private static final Logger LOG = LoggerFactory.getLogger(WrapperLoader.class);

    public static Function<WrapQueryType, String> loadQueries() {
        return QueryContext.call(new UnsafeFunction<QueryContext, Function<WrapQueryType, String>, Exception>() {

            @Override
            public Function<WrapQueryType, String> apply(QueryContext context) throws Exception {
                final DatabaseMetaData metaData = context.getDB().getDatabaseMetaData();
                final String dbmsName = DBTools.escapeProductName(metaData.getDatabaseProductName());
                final Map<WrapQueryType, String> map = new EnumMap<>(WrapQueryType.class);
                //загрузка запрсоов из XML
                final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                final InputStream is = AbstractWrapper.class.getResourceAsStream(dbmsName + ".xml");
                final Document doc = db.parse(is);
                final NodeList nl = doc.getElementsByTagName("query");
                int len = nl.getLength();
                for(int i = 0; i < len; ++i){
                    Node n = nl.item(i);
                    String name = null;
                    Node nameNode = n.getAttributes().getNamedItem("type");
                    if(nameNode != null){
                        name = nameNode.getTextContent();
                    }
                    map.put(forName(name), n.getTextContent());
                }
                LOG.debug("loaded {} queries for wrappers", map.size());
                return func(unmodifiableMap(map));
            }
        });
    }
}
