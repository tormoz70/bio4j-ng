package ru.bio4j.ng.commons.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TimedCache<T> {

    private long maxItems;
    private long timeToLive;
    private HashMap<String, TimedCacheObject> cacheMap;

    protected class TimedCacheObject {
        public long lastAccessed = System.currentTimeMillis();
        private final T value;

        protected TimedCacheObject(T value) {
            this.value = value;
        }
        protected T getValue() {
            try {
                return value;
            } finally {
                synchronized (this) {
                    lastAccessed = System.currentTimeMillis();
                }
            }
        }
    }

    /**
     * Constructor
     * @param timeToLive - seconds for life cache item
     * @param timeInterval - milliseconds for cleanup cache
     * @param maxItems - max cache size
     */
    public TimedCache(final long timeToLive, final long timeInterval, final int maxItems) {
        this.maxItems = maxItems;
        this.timeToLive = timeToLive * 1000;

        cacheMap = new HashMap<>();

        if (timeToLive > 0 && timeInterval > 0) {

            Thread t = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            cleanup();
                            Thread.sleep(timeInterval);
                        } catch (InterruptedException ex) {
                        }

                    }
                }
            });

            t.setDaemon(true);
            t.start();
        }
    }

    /**
     * Constructor
     * @param timeToLive - seconds for life cache item
     * @param timeInterval - milliseconds for cleanup cache
     */
    public TimedCache(final long timeToLive, final long timeInterval) {
        this(timeToLive, timeInterval, 1000);
    }

    /**
     * Constructor
     * @param timeToLive - seconds for life cache item
     */
    public TimedCache(final long timeToLive) {
        this(timeToLive, 1000L);
    }

    // PUT method
    public void put(String key, T value) {
        synchronized (cacheMap) {
            if(!cacheMap.containsKey(key))
                cacheMap.put(key, new TimedCacheObject(value));
        }
    }

    // GET method
    public T get(String key) {
        synchronized (cacheMap) {
            TimedCacheObject c = cacheMap.get(key);

            if (c != null)
                return c.getValue();
            return null;
        }
    }

    // REMOVE method
    public void remove(String key) {
        synchronized (cacheMap) {
            cacheMap.remove(key);
        }
    }

    // Get Cache Objects Size()
    public int size() {
        synchronized (cacheMap) {
            return cacheMap.size();
        }
    }

    // CLEANUP method
    public void cleanup() {

        long now = System.currentTimeMillis();
        ArrayList<String> deleteKey = null;

        synchronized (cacheMap) {
            Iterator<?> itr = cacheMap.entrySet().iterator();

            deleteKey = new ArrayList<>((cacheMap.size() / 2) + 1);
            TimedCacheObject c;

            while (itr.hasNext()) {
                Map.Entry<String, TimedCacheObject> entry = (Map.Entry<String, TimedCacheObject>)itr.next();
                if (entry.getValue() != null && (now > (timeToLive + entry.getValue().lastAccessed))) {
                    deleteKey.add(entry.getKey());
                }
            }
        }

        for (String key : deleteKey) {
            synchronized (cacheMap) {
                cacheMap.remove(key);
            }

            Thread.yield();
        }
    }
}
