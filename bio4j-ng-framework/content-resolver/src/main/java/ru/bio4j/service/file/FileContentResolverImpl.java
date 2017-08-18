package ru.bio4j.service.file;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.collections.Pair;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.service.api.BioContentResolver;
import ru.bio4j.ng.service.api.CacheName;
import ru.bio4j.ng.service.api.CacheService;
import ru.bio4j.ng.service.api.ConfigProvider;
import ru.bio4j.ng.service.types.BioServiceBase;
import ru.bio4j.service.file.io.FileListener;
import ru.bio4j.service.file.io.FileLoader;
import ru.bio4j.service.file.io.FileWatcher;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.Paths.get;
import static ru.bio4j.service.file.QueryExtractor.extractName;
import static ru.bio4j.service.file.QueryExtractor.loadQueries;
import static ru.bio4j.service.file.io.FileLoader.buildCode;

@Component
@Instantiate
@Provides(specifications = BioContentResolver.class)
public class FileContentResolverImpl extends BioServiceBase implements BioContentResolver, FileListener {
    private static final Logger LOG = LoggerFactory.getLogger(FileContentResolverImpl.class);
    private volatile FileWatcher fileWatcher;

    @Requires
    private ConfigProvider configProvider;
    @Requires
    private CacheService cacheService;

    private String contentPath;

    public void startWatcher(String path) {
        try {
            readVersion();
            final Path contentPath = get(path);
            LOG.info("Watching path is = {}", path);
            fileWatcher = new FileWatcher(contentPath, true);
            fileWatcher.addListener(this);
            fileWatcher.start();
        } catch (IOException e) {
            LOG.error("Can't watch dirs", e);
        }
    }

    @Validate
    public void doStart() throws Exception {
        LOG.debug("Starting...");

        if(!configProvider.configIsReady()) {
            LOG.info("Config is not ready! Waiting...");
            return;
        }
        ready = true;
        contentPath = configProvider.getConfig().getLiveBioContentPath();

        LOG.info("Service starting on path: {}", contentPath);
        cacheService.clear(CacheName.QUERY);
        stop();
        startWatcher(contentPath);
        LOG.debug("Started");
    }

    @Invalidate
    public void stop() {
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
    }

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
            final Map<String, String> qMap = loadQueries(fileName.getRight(), contentPath);
            if (qMap != null) {
                cacheService.put(CacheName.QUERY, fileName.getLeft(), (Serializable)Collections.unmodifiableMap(qMap));
                return qMap.get(fileName.getLeft());
            }
        }
        return content.get(fileName.getLeft());
    }


    public String getContent(String bioCode) throws IOException {
        String content = cacheService.get(CacheName.CONTENT, bioCode);
        if (Strings.isNullOrEmpty(content)) {
            content = FileLoader.loadFile(bioCode, contentPath);
            cacheService.put(CacheName.CONTENT, bioCode, content);
        }
        return content;
    }

    @Override
    public void onEvent(Path name, WatchEvent.Kind<Path> kind) {
        final String code = buildCode(name, contentPath);
        LOG.info("changed code = {} {}", code, kind);
        cacheService.remove(CacheName.QUERY, extractName(code).getRight());
    }

}
