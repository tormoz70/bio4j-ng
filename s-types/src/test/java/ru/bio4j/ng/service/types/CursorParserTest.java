package ru.bio4j.ng.service.types;

import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.SQLDefinition;
import ru.bio4j.ng.model.transport.BioConfig;


import java.io.InputStream;

public class CursorParserTest {
    private static final Logger LOG = LoggerFactory.getLogger(CursorParser.class);

    @Test
    public void toStringTest() throws Exception {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("rcard.xml");
        Document document = Utl.loadXmlDocument(inputStream);
        SQLDefinition cursor = CursorParser.pars(null, document, "eve.rcard");
        String out = cursor.toString();
        System.out.println(out);
    }

    @Test
    public void configuratorTest() throws Exception {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("bio4j.config.cfg");
        Configurator<BioConfig> cfg = new Configurator<>(BioConfig.class);
        cfg.load(inputStream);
        System.out.println(Utl.buildBeanStateInfo(cfg.getConfig(), null, "\t"));
    }

}
