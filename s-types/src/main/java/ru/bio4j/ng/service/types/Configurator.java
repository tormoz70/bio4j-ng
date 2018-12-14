package ru.bio4j.ng.service.types;

import org.osgi.service.cm.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.ApplyValuesToBeanException;
import ru.bio4j.ng.commons.utils.Utl;

import java.io.InputStream;
import java.util.Dictionary;
import java.util.Properties;

public class Configurator<T> {
    private static final Logger LOG = LoggerFactory.getLogger(Configurator.class);

    private Class<?> beanType;
    private T configBean;
    private boolean updated;

    public Configurator(Class<?> beanType) {
        this.beanType = beanType;
    }

    public void update(Dictionary conf) throws ConfigurationException {
        LOG.debug("About updating config to {}...", beanType);
        // Здесь получаем конфигурацию
        if(configBean == null) {
            try {
                LOG.debug("Bean instance is null creating new {}...", beanType);
                configBean = (T)beanType.newInstance();
                LOG.debug("New bean instance of {} is created.", beanType);
            } catch (Exception e) {
                throw new ConfigurationException("*", String.format("Error on create new instance of bean (%s)! Message: %s", beanType, e.getMessage()));
            }
        }
        try {
            updated = Utl.applyValuesToBeanFromDict(conf, configBean);
            LOG.debug("Config {} updated with:\n{}", beanType.getCanonicalName(),  Utl.buildBeanStateInfo(configBean, null, "\t"));
        } catch (ApplyValuesToBeanException e) {
            throw new ConfigurationException(e.getField(), e.getMessage());
        }
        LOG.debug("Appling config dictionary to {} done.", beanType);

    }

    public void load(InputStream inputStream) throws Exception {
        Properties prop = new Properties();
        prop.load(inputStream);
        update(prop);
    }

    public  T getConfig() {
        return this.configBean;
    }

    public boolean isUpdated() {
        return updated;
    }
}
