package ru.bio4j.ng.module.api;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ayrat on 19.05.14.
 */
public class BioModuleHelper {
    private static final Logger LOG = LoggerFactory.getLogger(BioModuleHelper.class);

    public static BioModule lookupService(BundleContext context, String key) throws Exception {
        LOG.debug("Looking for module of type:{} by key:{}", BioModule.class.getName(), key);
        ServiceReference[] references;
        references = context.getAllServiceReferences(BioModule.class.getName(), "(bioModuleKey=" + key + ")");

        if(references != null) {
            BioModule module = (BioModule) context.getService(references[0]);
            LOG.debug("Module {} found!!!", module.getDescription());
            return module;
        }

        throw new IllegalArgumentException(String.format("Module with key \"%s\" not registred in system!", key));
    }

}
