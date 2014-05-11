package ru.bio4j.ng.rapi.http;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.api.BioRouter;
import ru.bio4j.ng.service.api.DataProvider;

@Component
//@Provides
@Instantiate
public class RemoteAPIService {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteAPIService.class);
    private static final String HELLO_SERVLET_PATH = "/hello";
    private static final String BIO_SERVLET_PATH = "/biosrv";

    @Requires
    private HttpService httpService;
    @Requires
    private DataProvider dataProvider;
    @Requires
    private BioRouter router;

    @Validate
    public void doStart() throws Exception {
        if (httpService != null) {
            LOG.debug("Registering \"{}\"-servlet...", HELLO_SERVLET_PATH);
            httpService.registerServlet(HELLO_SERVLET_PATH, new HelloWorld(this), null, null);
            LOG.info("Servlet \"{}\" registered.", HELLO_SERVLET_PATH);

            LOG.debug("Registering \"{}\"-servlet...", BIO_SERVLET_PATH);
            httpService.registerServlet(BIO_SERVLET_PATH, new BioServlet(this), null, null);
            LOG.info("Servlet \"{}\" registered.", BIO_SERVLET_PATH);
        }
    }

    @Invalidate
    protected void doStop() throws Exception {
        if (httpService != null) {
            LOG.debug("Unregistering \"{}\"-servlet...", HELLO_SERVLET_PATH);
            httpService.unregister(HELLO_SERVLET_PATH);
            LOG.info("Servlet \"{}\" unregistered.", HELLO_SERVLET_PATH);

            LOG.debug("Unregistering \"{}\"-servlet...", BIO_SERVLET_PATH);
            httpService.unregister(BIO_SERVLET_PATH);
            LOG.info("Servlet \"{}\" unregistered.", BIO_SERVLET_PATH);
        }
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public BioRouter getRouter() {
        return router;
    }
}
