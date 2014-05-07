package ru.bio4j.ng.rapi;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.api.DataProvider;

@Component//(immediate = true)
@Provides
@Instantiate
public class RemoteAPIService {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteAPIService.class);
    private static final String SERVLET_SERVICE_PATH = "/hello";

    @Requires
    private DataProvider dataProvider;
    @Requires
    private HttpService httpService;
    @Requires
    private EventAdmin eventAdmin;

    @Validate
    public void start() throws Exception {
        if (httpService != null) {
            LOG.debug("Registering \"hello\"-servlet...");
            httpService.registerServlet(SERVLET_SERVICE_PATH, new HelloWorld(this), null, null);
            LOG.info("Servlet registered");
        }
    }

    @Invalidate
    protected void stop() throws Exception {
        if (httpService != null) {
            LOG.debug("Unregistering servlet");
            httpService.unregister(SERVLET_SERVICE_PATH);
            LOG.info("Servlet unregistered");
        }
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public EventAdmin getEventAdmin() {
        return eventAdmin;
    }
}
