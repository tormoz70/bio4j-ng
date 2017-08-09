package ru.bio4j.service.file.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

/*
* Используется для инвалидирования кеша при изменения файла .sql или удалении
* также может слушать добавление
*
* */
public class FileWatcher extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(FileWatcher.class);

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final List<FileListener> fileListeners = new ArrayList<>();
    private final boolean recursive;
    private boolean trace = false;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            processEvents();
        }
    }

    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                LOG.debug("register: {}", dir);
            } else {
                if (!dir.equals(prev)) {
                    LOG.debug("update: {} -> {}", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    private void registerAll(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public FileWatcher(Path dir, boolean recursive) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        this.recursive = recursive;

        if (recursive) {
            LOG.debug("Scanning {}", dir);
            registerAll(dir);
            LOG.debug("Done.");
        } else {
            register(dir);
        }

        this.trace = true;
    }

    public void processEvents() {

        WatchKey key;
        try {
            key = watcher.take();
        } catch (InterruptedException x) {
            return;
        }

        Path dir = keys.get(key);
        if (dir == null) {
            LOG.debug("WatchKey not recognized!!");
            return;
        }

        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind kind = event.kind();

            if (kind == OVERFLOW) {
                continue;
            }

            // Context for directory entry event is the file name of entry
            WatchEvent<Path> ev = cast(event);
            Path name = ev.context();
            Path child = dir.resolve(name);

            // print out event
            LOG.debug("{} {}", event.kind().name(), child);

            if (recursive && (kind == ENTRY_CREATE)) {
                try {
                    if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                        registerAll(child);
                    }
                } catch (IOException x) {
                }
            }
            if (kind == ENTRY_MODIFY || kind == ENTRY_DELETE) {
                for (FileListener fileListener : fileListeners) {
                    fileListener.onEvent(name, kind);
                }
            }
        }

        // reset key and remove from set if directory no longer accessible
        boolean valid = key.reset();
        if (!valid) {
            keys.remove(key);

            // all directories are inaccessible
            if (keys.isEmpty()) {
                return;
            }
        }
    }

    public void addListener(FileListener fileListener) {
         fileListeners.add(fileListener);
    }

    public void removeListener(FileListener fileListener) {
        fileListeners.remove(fileListener);
    }

}
