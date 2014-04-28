package ru.bio4j.ng.rapi;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.api.DataProvider;

import java.util.Dictionary;
import java.util.Hashtable;

@Component
@Provides
@Instantiate
public class RestManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(RestManagerService.class);
    private static final String SERVLET_SERVICE_PATH = "/bio4j-spi";

    private DataProvider dataProvider;
    private HttpService httpService;

    @Validate
    public void start() throws Exception {
        if (httpService != null) {
            LOG.debug("Registering \"hello\"-servlet...");
            httpService.registerServlet("/hello", new HelloWorld(), null, null);
            LOG.info("Servlet Registered");
        }
    }

    @Invalidate
    protected void stop() throws Exception {
        if (httpService != null) {
            LOG.debug("Unregistering Jersey servlet");
            httpService.unregister(SERVLET_SERVICE_PATH);
            LOG.info("Jersey servlet Unregistered");
        }
    }

    @Bind
    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    @Bind
    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }
}
