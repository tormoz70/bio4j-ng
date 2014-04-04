package org.example;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ayrat on 03.04.14.
 */
public class Activator implements BundleActivator {
    private static Logger LOG = LoggerFactory.getLogger(Activator.class);

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        LOG.debug("start!");
        System.out.println("start!");
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        LOG.debug("stop!");
        System.out.println("stop!");
    }
}
