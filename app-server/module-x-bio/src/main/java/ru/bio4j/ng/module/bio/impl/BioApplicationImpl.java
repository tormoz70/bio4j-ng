package ru.bio4j.ng.module.bio.impl;

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
                value = "bio",
                type = "java.lang.String"
        )})
public class BioApplicationImpl extends BioModuleBase {
    private static final Logger LOG = LoggerFactory.getLogger(BioApplicationImpl.class);

    @Context
    private BundleContext bundleContext;

    @Override
    protected BundleContext bundleContext() {
        return bundleContext;
    }

    @Override
    public String getDescription() {
        return "Bio inner application module";
    }

}