package ru.bio4j.ng.service.types;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.api.BioModule;

public class BioModuleHelper {
    private static final Logger LOG = LoggerFactory.getLogger(BioModuleHelper.class);

    public static BioModule lookupService(BundleContext context, String key) throws Exception {
        LOG.debug("Looking for module of type:{} by key:{}", BioModule.class.getName(), key);
        ServiceReference[] references;
        references = context.getAllServiceReferences(BioModule.class.getName(), "(bioModuleKey=" + key + ")");

        if(references != null) {
            BioModule module = (BioModule) context.getService(references[0]);
            module.setKey(key);
            LOG.debug("Module {} found!!!", module.getDescription());
            return module;
        }

        throw new IllegalArgumentException(String.format("Module with key \"%s\" not registred in system!", key));
    }

}
