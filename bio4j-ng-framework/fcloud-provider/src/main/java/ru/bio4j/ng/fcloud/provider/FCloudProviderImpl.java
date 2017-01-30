package ru.bio4j.ng.fcloud.provider;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioServiceBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.util.Collection;
import java.util.Dictionary;

@Component(managedservice="fcloud.config")
@Instantiate
@Provides(specifications = ConfigProvider.class)
public class FCloudProviderImpl extends BioServiceBase<FCloudConfig> implements FCloudProvider {
    private static final Logger LOG = LoggerFactory.getLogger(FCloudProviderImpl.class);

    @Requires
    private EventAdmin eventAdmin;

    @Override
    protected EventAdmin getEventAdmin(){
        return eventAdmin;
    }


    public void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        Collection<Part> parts = null;
        try {
            parts = request.getParts();
        } catch (Exception e) {}
        if(parts != null) {
            LOG.debug("Parts recived: {}", parts.size());
            for (Part p : parts) {
                LOG.debug(" - contentType: {}; partName: {}; size: {}; fileName: {}", p.getContentType(), p.getName(), p.getSize(), p.getSubmittedFileName());
            }
        }

    }

    @Updated
    public synchronized void updated(Dictionary conf) throws Exception {
        doOnUpdated(conf, "fcloud-config-updated");
    }

    @Validate
    public void start() throws Exception {
        LOG.debug("Starting...");
        this.ready = true;
        LOG.debug("Started");
    }

    @Invalidate
    public void stop() throws Exception {
        LOG.debug("Stoping...");
        this.ready = false;
        LOG.debug("Stoped.");
    }

}
