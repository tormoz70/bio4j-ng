package ru.bio4j.ng.service.api;

import org.osgi.service.cm.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.ApplyValuesToBeanException;
import ru.bio4j.ng.commons.utils.Utl;

import java.lang.reflect.Type;
import java.util.Dictionary;

/**
 * Created by ayrat on 04.05.14.
 */
public class Configurator<T> {
    private static final Logger LOG = LoggerFactory.getLogger(Configurator.class);

    private Class<?> beanType;
    private T configBean;
    private boolean updated;

    public Configurator(Class<?> beanType) {
        this.beanType = beanType;
    }



    public void update(Dictionary conf) throws ConfigurationException {
//        if(this.beanType == null) {
//            LOG.debug("About detecting beanType ...");
//            Type[] typeParams = Utl.getTypeParams(getClass());
//            if(typeParams.length == 0)
//                throw new ConfigurationException("*", "Cannot detect type parameter!");
//            beanType = (Class)typeParams[0];
//            beanType = Utl.getTypeParams(getClass());
//            LOG.debug("BeanType detected: {}...", beanType);
//        }

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

        if(!conf.isEmpty()) {
            LOG.debug("Config dictionary is not empty...");
            try {
                updated = Utl.applyValuesToBean(conf, configBean);
            } catch (ApplyValuesToBeanException e) {
                throw new ConfigurationException(e.getField(), e.getMessage());
            }
            LOG.debug("Apling config dictionary to {} done.", beanType);
        }

    }

    public  T getConfig() {
        return this.configBean;
    }

    public boolean isUpdated() {
        return updated;
    }
}
