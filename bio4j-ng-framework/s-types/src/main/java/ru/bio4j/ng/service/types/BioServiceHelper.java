package ru.bio4j.ng.service.types;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.api.BioService;

public class BioServiceHelper {
    private static final Logger LOG = LoggerFactory.getLogger(BioServiceHelper.class);

    public static <T extends BioService> T lookupService(BundleContext context, Class<T> clazz) throws Exception {
        LOG.debug("Looking for service of type:{}", clazz.getName());
        ServiceReference[] references;
        references = context.getAllServiceReferences(clazz.getName(), null);

        if(references != null) {
            T module = (T) context.getService(references[0]);
            LOG.debug("Service {} found!!!", module.getClass().getName());
            return module;
        }

        throw new IllegalArgumentException(String.format("Service \"%s\" not found in system!", clazz.getName()));
    }

}
