package ru.bio4j.ng.content.impl;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.collections.Pair;
import ru.bio4j.ng.content.io.FileListener;
import ru.bio4j.ng.content.io.FileLoader;
import ru.bio4j.ng.content.io.FileWatcher;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioServiceBase;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import java.nio.file.Paths;
import static ru.bio4j.ng.content.impl.QueryExtractor.extractName;
import static ru.bio4j.ng.content.impl.QueryExtractor.loadQueries;
import static ru.bio4j.ng.content.io.FileLoader.buildCode;
import static ru.bio4j.ng.commons.utils.Strings.isNullOrEmpty;

@Component
@Instantiate
@Provides(specifications = FileContentResolver.class)
public class FileContentResolverImpl extends BioServiceBase implements FileContentResolver, FileListener {

    private static final Logger LOG = LoggerFactory.getLogger(FileContentResolverImpl.class);

    private volatile FileWatcher fileWatcher;

    @Requires
    private ConfigProvider configProvider;
    @Requires
    private CacheService cacheService;

    @Override
    public String getQueryContent(String bioCode) throws IOException {
        final Pair<String,String> fileName = extractName(bioCode);
        final HashMap<String, String> content = cacheService.get(CacheName.QUERY, fileName.getRight());
        if (content == null) {
            final Map<String, String> qMap = loadQueries(fileName.getRight(), configProvider.getConfig().getContentResolverPath());
            if (qMap != null) {
                cacheService.put(CacheName.QUERY, fileName.getLeft(), (Serializable)Collections.unmodifiableMap(qMap));
                return qMap.get(fileName.getLeft());
            }
        }
        return content.get(fileName.getLeft());
    }


    public String getContent(String bioCode) throws IOException {
        if(!cacheService.isReady()){
            LOG.error("CacheService is not redy!");
            return null;
        }
        String content = cacheService.get(CacheName.CONTENT, bioCode);
        if (isNullOrEmpty(content)) {
            content = FileLoader.loadFile(bioCode, configProvider.getConfig().getContentResolverPath());
            cacheService.put(CacheName.CONTENT, bioCode, content);
        }
        return content;
    }

    @Override
    public void onEvent(Path name, WatchEvent.Kind<Path> kind) {
        final String code = buildCode(name, configProvider.getConfig().getContentResolverPath());
        LOG.info("changed code = {} {}", code, kind);
        cacheService.remove(CacheName.QUERY, extractName(code).getRight());
    }

    @Validate
    public void doStart() throws Exception {
        LOG.debug("Starting...");
        if(!configProvider.configIsReady()) {
            LOG.info("Config is not redy! Waiting...");
            return;
        }
        try {
            final String path = configProvider.getConfig().getContentResolverPath();
            LOG.info("Watching path is = {}", path);
            final Path contentPath = Paths.get(path);
            fileWatcher = new FileWatcher(contentPath, true);
            fileWatcher.addListener(this);
            fileWatcher.start();
            this.ready = true;
        } catch (IOException e) {
            LOG.error("Can't watch dirs", e);
        }
        LOG.debug("Started.");
    }

    @Invalidate
    public void doStop() throws Exception {
        LOG.debug("Stoping...");
        this.ready = false;
        if (fileWatcher != null) {
            try {
                fileWatcher.removeListener(this);
                fileWatcher.interrupt();
                fileWatcher.join();
            } catch (InterruptedException e) {
                LOG.debug("thread is interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
        LOG.debug("Stoped.");
    }


    @Subscriber(
            name="content.resolver.subscriber",
            topics="bio-config-updated")
    public void receive(Event e) throws Exception {
        LOG.debug("Config updated event recived!!!");
        doStop();
        doStart();
    }

}
