package ru.bio4j.ng.module.ekb.impl;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.api.BioModule;
import ru.bio4j.ng.module.commons.BioModuleBase;

@Component
@Instantiate
@Provides(specifications = BioModule.class,
        properties = {@StaticServiceProperty(
                name = "bioModuleKey",
                value = "ekbp",
                type = "java.lang.String"
        )})
public class EkbApplicationImpl extends BioModuleBase {
    private static final Logger LOG = LoggerFactory.getLogger(EkbApplicationImpl.class);

    @Context
    private BundleContext bundleContext;

    @Override
    protected BundleContext bundleContext() {
        return bundleContext;
    }

    @Override
    public String getDescription() {
        return "E-Kinobilet application";
    }

}
