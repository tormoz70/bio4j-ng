package ru.bio4j.ng.rapi.resful.ims;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.api.BioRouter;
import ru.bio4j.ng.service.api.ConfigProvider;
import ru.bio4j.ng.service.api.DataProvider;
import ru.bio4j.ng.service.api.SecurityHandler;

@Component
//@Provides
@Instantiate
public class RemoteAPIService {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteAPIService.class);
    private static final String SERVLET_PATH = "/jersey-service";

    @Requires
    private HttpService httpService;
    @Requires
    private DataProvider dataProvider;
    @Requires
    private BioRouter router;
    @Requires
    private SecurityHandler securityHandler;
    @Requires
    private ConfigProvider configProvider;

    @Validate
    public void doStart() throws Exception {
        if (httpService != null) {
            LOG.debug("Registering \"{}\"-servlet...", SERVLET_PATH);
            RESTFulService res = new RESTFulService();
            RestApplication app = new RestApplication( res );
            ServletContainer jerseyServlet = new ServletContainer( app );

            this.httpService.registerServlet( SERVLET_PATH, jerseyServlet, null, null );
            LOG.info("Servlet \"{}\" registered.", SERVLET_PATH);
        }
    }

    @Invalidate
    protected void doStop() throws Exception {
        if (httpService != null) {
            LOG.debug("Unregistering \"{}\"-servlet...", SERVLET_PATH);
            this.httpService.unregister( SERVLET_PATH);
            LOG.info("Servlet \"{}\" unregistered.", SERVLET_PATH);
        }
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

}
