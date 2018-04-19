package ru.bio4j.ng.content.impl;

import org.apache.felix.ipojo.annotations.*;
import org.apache.felix.ipojo.handlers.event.Subscriber;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.content.io.FileListener;
import ru.bio4j.ng.content.io.FileWatcher;
import ru.bio4j.ng.database.api.BioCursorDeclaration;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.commons.CursorParser;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.*;
import ru.bio4j.ng.service.types.BioServiceBase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import static ru.bio4j.ng.content.io.FileLoader.buildCode;

@Component
@Instantiate
@Provides(specifications = ContentResolver.class)
public class ContentResolverImpl extends BioServiceBase implements ContentResolver, FileListener {
    private static final Logger LOG = LoggerFactory.getLogger(ContentResolverImpl.class);

    private volatile FileWatcher fileWatcher;

    @Requires
    private BundleContext bundleContext;
    @Requires
    private ConfigProvider configProvider;
    @Requires
    private CacheService cacheService;
    @Requires
    private ModuleProvider moduleProvider;


    private BioCursorDeclaration getCursorFromFileSystem(String bioCode, User usr) throws Exception {
        BioCursorDeclaration cursor = cacheService.get(CacheName.CURSOR, bioCode.toLowerCase());
        if (cursor == null) {
            cursor = CursorParser.pars(configProvider.getConfig().getContentResolverPath(), bioCode);
            cacheService.put(CacheName.CURSOR, bioCode.toLowerCase(), cursor);
        }
        return cursor;
    }

    private BioCursorDeclaration getCursorFromModule(String moduleKey, String bioCode, User usr) throws Exception {
        BioAppModule module = moduleProvider.getAppModule(moduleKey);
        if(module == null)
            throw new Exception(String.format("Модуле \"%s\" not found in system!", moduleKey));

        BioCursorDeclaration cursor = module.getCursor(bioCode);
        return cursor;
    }

    @Override
    public BioCursorDeclaration getCursor(String moduleKey, String bioCode, User usr) throws Exception {
        BioCursorDeclaration cursor = getCursorFromFileSystem(bioCode, usr);
        if(cursor == null)
            cursor = getCursorFromModule(moduleKey, bioCode, usr);

        if(cursor == null)
            throw new Exception(String.format("Cursor \"%s\" not found in file system and module \"%s\"!", bioCode, moduleKey));

        return cursor;
    }

    @Override
    public BioCursorDeclaration getCursor(String moduleKey, String bioCode) throws Exception {
        return getCursor(moduleKey, bioCode, null);
    }

    @Override
    public BioCursorDeclaration getCursor(String moduleKey, BioRequest bioRequest) throws Exception {
        String bioCode = bioRequest.getBioCode();
        BioCursorDeclaration cursor = getCursor(moduleKey, bioCode, bioRequest.getUser());

//        if(cursor != null)
//            applyBioParams(bioRequest.getBioParams(), cursor.sqlDefs());

        return cursor;
    }

    @Override
    public SQLContext getSQLContext(String moduleKey) throws Exception {
        BioAppModule module = moduleProvider.getAppModule(moduleKey);
        if(module == null)
            throw new Exception(String.format("Модуле \"%s\" not found in system!", moduleKey));
        return module.getSQLContext();
    }

    @Override
    public void onEvent(Path name, WatchEvent.Kind<Path> kind) {
        final String bioCode = buildCode(name, configProvider.getConfig().getContentResolverPath());
        if(cacheService.isKeyInCache(CacheName.CURSOR, bioCode.toLowerCase())) {
            LOG.info("Changed object bioCode: \"{}\" - \"{}\", removing from cache...", bioCode, kind);
            cacheService.remove(CacheName.CURSOR, bioCode.toLowerCase());
            LOG.info("Object bioCode: \"{}\" removed from cache.", bioCode, kind);
        }
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
            Files.createDirectories(contentPath);
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
