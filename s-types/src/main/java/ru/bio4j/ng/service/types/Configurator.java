package ru.bio4j.ng.service.types;

import org.osgi.service.cm.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.utils.ApplyValuesToBeanException;
import ru.bio4j.ng.commons.utils.Regexs;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Configurator<T> {
    private static final Logger LOG = LoggerFactory.getLogger(Configurator.class);

    private Class<?> beanType;
    private T configBean;
    private volatile boolean updated;

    public Configurator(Class<?> beanType) {
        this.beanType = beanType;
    }

    public void update(Dictionary conf) throws ConfigurationException {
        try {

            List<String> keys = Collections.list(conf.keys());
            Map<String, Object> confMap = keys.stream()
                    .collect(Collectors.toMap(Function.identity(), conf::get));

            if (LOG.isDebugEnabled())
                LOG.debug("About updating config to {}...", beanType);
            // Здесь получаем конфигурацию
            if (configBean == null) {
                try {
                    if (LOG.isDebugEnabled())
                        LOG.debug("Bean instance is null creating new {}...", beanType);
                    configBean = (T) beanType.newInstance();
                    if (LOG.isDebugEnabled())
                        LOG.debug("New bean instance of {} is created: \n{}", beanType, Utl.buildBeanStateInfo(configBean, null, "\t"));
                } catch (Exception e) {
                    throw new ConfigurationException("*", String.format("Error on create new instance of bean (%s)! Message: %s", beanType, e.getMessage()));
                }
            }

            if (LOG.isDebugEnabled())
                LOG.debug("Try to set default values to config...");
            // Вытаскиваем в конфик значения по умолчанию
            Collections.synchronizedMap(confMap).forEach((key, v) -> {
                String val = (String) v;
                val = Regexs.replace(val, "\\$\\{.+\\}", "", Pattern.CASE_INSENSITIVE);
                String defaultVal = Utl.fieldValue(configBean, key, String.class);
                if (Strings.isNullOrEmpty(val))
                    confMap.put(key, Utl.nvl(defaultVal, ""));
            });
            if (LOG.isDebugEnabled())
                LOG.debug("Default values setted to config.");

            if (LOG.isDebugEnabled())
                LOG.debug("Try to apply values to config bean...");
            updated = Utl.applyValuesToBeanFromMap(confMap, configBean);
            if (LOG.isDebugEnabled())
                LOG.debug("Config {} updated with:\n{}", beanType.getCanonicalName(), Utl.buildBeanStateInfo(configBean, null, "\t"));

            if (LOG.isDebugEnabled())
                LOG.debug("Appling config dictionary to {} done.", beanType);

        } catch (Exception e) {
            LOG.error(String.format("Unexpected error while prepare config %s!", this.beanType), e);
        }
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
