package ru.bio4j.ng.rapi.rmi;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.rapi.BioRemote;
import ru.bio4j.ng.service.api.BioRouter;
import ru.bio4j.ng.service.api.DataProvider;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

@Component
//@Provides
@Instantiate
public class RemoteAPIService {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteAPIService.class);

    @Requires
    private HttpService httpService;
    @Requires
    private BioRouter router;

    @Validate
    public void doStart() throws Exception {
        LOG.debug("Starting...");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "bio4j.remote.rmi";
            BioRemote bioRemote = new BioRemoteImpl(this);
            BioRemote stub =
                    (BioRemote) UnicastRemoteObject.exportObject(bioRemote, 37541);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            LOG.debug("BioRemote bound.");
        } catch (Exception e) {
            LOG.error("Error while BioRemote registration!", e);
        }
        LOG.debug("Started.");
    }

    @Invalidate
    protected void doStop() throws Exception {
        LOG.debug("Stoping...");
        LOG.debug("Stoped.");
    }

    public BioRouter getRouter() {
        return router;
    }
}
