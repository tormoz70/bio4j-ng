package ru.bio4j.service.file;

import org.apache.felix.ipojo.annotations.*;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.collections.Pair;
import ru.bio4j.service.ehcache.CacheName;
import ru.bio4j.service.ehcache.CacheService;
import ru.bio4j.service.file.config.FileContentResolverConfig;
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
import static org.osgi.framework.Constants.SERVICE_RANKING;
import static ru.bio4j.service.ServiceConstants.PROCESSING_FILE_RANK_IPOJO;
import static ru.bio4j.service.file.QueryExtractor.extractName;
import static ru.bio4j.service.file.QueryExtractor.loadQueries;
import static ru.bio4j.service.file.io.FileLoader.buildCode;
import static ru.bio4j.util.Strings.empty;

@Component(managedservice="content.service.config")
@Instantiate
@Provides(properties =
        {@StaticServiceProperty(name = SERVICE_RANKING,
                value = PROCESSING_FILE_RANK_IPOJO, type = "java.lang.Integer")})
public class FileContentResolverImpl implements FileContentResolver, ManagedService, FileListener {

    private static final Logger LOG = LoggerFactory.getLogger(FileContentResolverImpl.class);

    private final FileContentResolverConfig fileConf = new FileContentResolverConfig();
    private volatile FileWatcher fileWatcher;

    private CacheService cacheService;

    @Updated
    public synchronized void updated(Dictionary conf) {
        fileConf.config(conf);
        if (fileConf.isFilled()) {
            LOG.info("service updated new values are {}", fileConf);
            cacheService.clear(CacheName.QUERY);
            stop();
            start(fileConf.getPath());
        }
    }

    public void start(String path) {
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
            final Map<String, String> qMap = loadQueries(fileName.getRight(), fileConf.getPath());
            if (qMap != null) {
                cacheService.put(CacheName.QUERY, fileName.getLeft(), (Serializable)Collections.unmodifiableMap(qMap));
                return qMap.get(fileName.getLeft());
            }
        }
        return content.get(fileName.getLeft());
    }


    public String getContent(String bioCode) throws IOException {
        String content = cacheService.get(CacheName.CONTENT, bioCode);
        if (empty(content)) {
            content = FileLoader.loadFile(bioCode, fileConf.getPath());
            cacheService.put(CacheName.CONTENT, bioCode, content);
        }
        return content;
    }

    @Override
    public void onEvent(Path name, WatchEvent.Kind<Path> kind) {
        final String code = buildCode(name, fileConf.getPath());
        LOG.info("changed code = {} {}", code, kind);
        cacheService.remove(CacheName.QUERY, extractName(code).getRight());
    }

    @Bind
    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }
}
