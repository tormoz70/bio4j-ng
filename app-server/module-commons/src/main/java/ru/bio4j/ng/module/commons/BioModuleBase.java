package ru.bio4j.ng.module.commons;

import org.osgi.framework.BundleContext;
import org.w3c.dom.Document;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.api.SQLContextConfig;
import ru.bio4j.ng.database.doa.SQLContextFactory;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGet;
import ru.bio4j.ng.module.api.BioModule;
import ru.bio4j.ng.service.api.BioConfig;
import ru.bio4j.ng.service.api.BioCursor;
import ru.bio4j.ng.service.api.Configurator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public abstract class BioModuleBase implements BioModule {

    private static Document loadDocument(InputStream inputStream) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setValidating(false);
        DocumentBuilder builder = f.newDocumentBuilder();
        return builder.parse(inputStream);
    }


    private static BioCursor loadCursor(BundleContext context, String bioCode) throws Exception {
        BioCursor cursor = null;
        String path = "/"+bioCode.replace(".", "/");
        InputStream inputStream = context.getBundle().getResource(path + ".xml").openStream();
        if(inputStream != null) {
            Document document = loadDocument(inputStream);
            cursor = CursorParser.pars(bioCode, document);
        } else {
            inputStream = context.getBundle().getResource(path + ".sql").openStream();
            if(inputStream != null) {
                String sql = Utl.readStream(inputStream, "WINDOWS-1251");
                cursor = CursorParser.pars(bioCode, sql);
            } else
                throw new Exception(String.format("Resource %s not found in module!", path));
        }
        return cursor;
    }

    protected abstract BundleContext bundleContext();


    @Override
    public BioCursor getCursor(BioRequest request) throws Exception {
        String bioCode = request.getBioCode();
        BioCursor cursor = loadCursor(bundleContext(), bioCode);
        if(request instanceof BioRequestJStoreGet) {
            final BioRequestJStoreGet r = (BioRequestJStoreGet)request;
            cursor.setOffset(r.getOffset());
            cursor.setPageSize(r.getPagesize());
            cursor.setFilter(r.getFilter());
            cursor.setSort(r.getSort());
            cursor.setParams(r.getBioParams());
        }
        return cursor;
    }

    private static final String SQL_CONTEXT_CONFIG_FILE_NAME = "sql-context.config";
    private Configurator<SQLContextConfig> configurator = new Configurator<>(SQLContextConfig.class);
    private SQLContext sqlContext = null;
    private boolean localSQLContextIsInited = false;
    public SQLContext getSQLContext() throws Exception {
        if(sqlContext == null && !localSQLContextIsInited) {
            localSQLContextIsInited = true;
            BundleContext bundleContext = bundleContext();
            InputStream inputStream = bundleContext.getBundle().getResource(SQL_CONTEXT_CONFIG_FILE_NAME).openStream();
            if(inputStream != null) {
                configurator.load(inputStream);
                sqlContext = SQLContextFactory.create(configurator.getConfig());
            }
        }
        return sqlContext;
    }
}
