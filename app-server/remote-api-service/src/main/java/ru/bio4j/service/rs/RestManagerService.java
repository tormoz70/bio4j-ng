package ru.bio4j.service.rs;

import org.apache.felix.ipojo.annotations.*;
import org.glassfish.jersey.servlet.ServletContainer;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.service.processing.QueryProcessor;

import java.util.Dictionary;
import java.util.Hashtable;

@Component
@Provides
@Instantiate
public class RestManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(RestManagerService.class);
    private static final String SERVLET_SERVICE_PATH = "/bio4j-spi";

    private HttpService httpService;
    private QueryProcessor queryProcessor;

    @Validate
    public void start() throws Exception {
        if (httpService != null) {
            LOG.debug("Registering Jersey servlet");

            ClassLoader current = getClass().getClassLoader();
            ClassLoader origin = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(current);
                ServletContainer servletContainer = new ServletContainer();
                httpService.registerServlet(SERVLET_SERVICE_PATH, servletContainer, getJerseyParams(), httpService.createDefaultHttpContext());
                servletContainer.getServletContext().setAttribute(QueryProcessor.class.getName(), queryProcessor);
            } finally {
                Thread.currentThread().setContextClassLoader(origin);
            }
            LOG.info("Jersey servlet Registered");
        }
    }

    private Dictionary getJerseyParams() {
        Dictionary<String, String> jerseyServletParams = new Hashtable<String, String>();
        jerseyServletParams.put("javax.ws.rs.Application", BioJerseyApplication.class.getName());
        return jerseyServletParams;
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
    public void setQueryProcessor(QueryProcessor queryProcessor) {
        this.queryProcessor = queryProcessor;
    }

    @Bind
    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }
}
