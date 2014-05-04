package ru.bio4j.ng.content.impl;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.collections.Pair;
import ru.bio4j.ng.service.api.CacheName;
import ru.bio4j.ng.service.api.CacheService;
import ru.bio4j.ng.content.io.FileListener;
import ru.bio4j.ng.content.io.FileLoader;
import ru.bio4j.ng.content.io.FileWatcher;
import ru.bio4j.ng.service.api.Configurator;
import ru.bio4j.ng.service.api.FileContentResolver;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import java.nio.file.Paths;
import static ru.bio4j.ng.content.impl.QueryExtractor.extractName;
import static ru.bio4j.ng.content.impl.QueryExtractor.loadQueries;
import static ru.bio4j.ng.content.io.FileLoader.buildCode;
import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;
import static org.osgi.framework.Constants.SERVICE_RANKING;
import static ru.bio4j.ng.service.api.ServiceConstants.PROCESSING_SERVICE_RANK_IPOJO;

@Component(managedservice="bio4j.content.resolver.config")
@Instantiate
@Provides(specifications = FileContentResolver.class)
//        properties = {@StaticServiceProperty(name = SERVICE_RANKING, value = PROCESSING_SERVICE_RANK_IPOJO, type = "java.lang.Integer")})
public class FileContentResolverImpl implements FileContentResolver, FileListener, ManagedService {

    private static final Logger LOG = LoggerFactory.getLogger(FileContentResolverImpl.class);

    private Configurator<FileContentResolverConfig> configurator = new Configurator<>(FileContentResolverConfig.class);
    private volatile FileWatcher fileWatcher;

    private CacheService cacheService;

//    public synchronized void updated(Dictionary conf) {
//        fileConf.config(conf);
//        if (fileConf.isFilled()) {
//            LOG.info("service updated new values are {}", fileConf);
//            cacheService.clear(CacheName.QUERY);
//            stop();
//            start(fileConf.getPath());
//        }
//    }

    private void readVersion() {
        LOG.info("try to find version");
        try {
            final String version = getContent("version");
            LOG.info("the version is = {}", version);
        } catch (IOException e) {
            LOG.error("failed to load version", e);
        }
    }

    @Override
    public String getQueryContent(String bioCode) throws IOException {
        final Pair<String,String> fileName = extractName(bioCode);
        final HashMap<String, String> content = cacheService.get(CacheName.QUERY, fileName.getRight());
        if (content == null) {
            final Map<String, String> qMap = loadQueries(fileName.getRight(), configurator.getConfig().getPath());
            if (qMap != null) {
                cacheService.put(CacheName.QUERY, fileName.getLeft(), (Serializable)Collections.unmodifiableMap(qMap));
                return qMap.get(fileName.getLeft());
            }
        }
        return content.get(fileName.getLeft());
    }


    public String getContent(String bioCode) throws IOException {
        String content = cacheService.get(CacheName.CONTENT, bioCode);
        if (isNullOrEmpty(content)) {
            content = FileLoader.loadFile(bioCode, configurator.getConfig().getPath());
            cacheService.put(CacheName.CONTENT, bioCode, content);
        }
        return content;
    }

    @Override
    public void onEvent(Path name, WatchEvent.Kind<Path> kind) {
        final String code = buildCode(name, configurator.getConfig().getPath());
        LOG.info("changed code = {} {}", code, kind);
        cacheService.remove(CacheName.QUERY, extractName(code).getRight());
    }

//    @Bind
    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Validate
    public void doStart() throws Exception {
        LOG.debug("Starting...");
        try {
            readVersion();
            final String path = configurator.getConfig().getPath();
            LOG.info("Watching path is = {}", path);
            final Path contentPath = Paths.get(path);
            fileWatcher = new FileWatcher(contentPath, true);
            fileWatcher.addListener(this);
            fileWatcher.start();
        } catch (IOException e) {
            LOG.error("Can't watch dirs", e);
        }
        LOG.debug("Started.");
    }

    @Invalidate
    public void doStop() throws Exception {
        LOG.debug("Stoping...");
        if (fileWatcher != null) {
            try {
                fileWatcher.removeListener(this);
                fileWatcher.interrupt();
                fileWatcher.join();
            } catch (InterruptedException e) {
                LOG.error("thread is interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
        LOG.debug("Stoped.");
    }

    @Updated
    public synchronized void updated(Dictionary conf) throws ConfigurationException {
        LOG.debug("Updating config...");
//        if(configurator == null)
//            configurator = new Configurator<>();
//        LOG.debug("About appling config to sqlContextConfig...");
//        // Здесь получаем конфигурацию
//        if(fileConf == null) {
//            fileConf = new FileContentResolverConfig();
//        }
//
//        if(!conf.isEmpty()) {
//            LOG.debug("Config is not empty...");
//            try {
//                Utl.applyValuesToBean(conf, fileConf);
//            } catch (ApplyValuesToBeanException e) {
//                throw new ConfigurationException(e.getField(), e.getMessage());
//            }
//            LOG.debug("Apling config to sqlContextConfig done.");
//        }

        configurator.update(conf);

        if(isNullOrEmpty(configurator.getConfig().getPath())){
            LOG.debug("SQLContextConfig bp empty. Wating...");
            return;
        }

        cacheService.clear(CacheName.QUERY);
        try {
            doStop();
            doStart();
        } catch (Exception e) {
            LOG.error("Error on restarting service!", e);
        }

    }

}
