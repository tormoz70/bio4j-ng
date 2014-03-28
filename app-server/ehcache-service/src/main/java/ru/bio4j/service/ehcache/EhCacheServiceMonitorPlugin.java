package ru.bio4j.service.ehcache;

import net.sf.ehcache.Cache;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.service.ehcache.util.JSONUtil;
import ru.bio4j.service.monitor.Monitor;
import ru.bio4j.service.monitor.plugin.AbstractPlugin;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;

public class EhCacheServiceMonitorPlugin extends AbstractPlugin {

	private static Logger logger = LoggerFactory.getLogger(EhCacheServiceMonitorPlugin.class);
	
	private static final String JSON_INFO_FIELD = "info";
	private static final String JSON_CACHE_FIELD = "cache";
	private static final String JSON_CLEAN_FIELD = "clean";
	
	public EhCacheServiceMonitorPlugin(Monitor service) {
		super(service);
	}

	@Override
	public String getTitle() {
		return "EhCache Monitor";
	}

	@Override
	public String getTemplate() {
		return "/templates/ehcache-service-monitor.html";
	}

	@Override
	public void processRequest(HttpServletRequest request, PrintWriter writer) {
		String cacheName = request.getParameter(JSON_CACHE_FIELD);
		String action = request.getParameter(JSON_ACTION_FIELD);
		CacheServiceManagement cacheServiceManagement = ((CacheServiceManagement) getService());
        switch (action) {
            case JSON_LOAD_ACTION:
                JSONArray caches = JSONUtil.cacheNames(cacheServiceManagement.getCacheNames());
                logger.debug("JSON for caches {} {}", cacheName, caches);
                writer.print(caches.toString());
                break;
            case JSON_INFO_FIELD:
            case JSON_RELOAD_FIELD: {
                Cache cache = cacheServiceManagement.getCache(cacheName);
                JSONObject cacheObject = JSONUtil.cacheToJSON(cache);
                logger.debug("JSON for cache {} {}", cacheObject);
                writer.print(cacheObject.toString());
                break;
            }
            case JSON_CLEAN_FIELD: {
                cacheServiceManagement.clean(cacheName);
                Cache cache = cacheServiceManagement.getCache(cacheName);
                JSONObject cacheObject = JSONUtil.cacheToJSON(cache);
                logger.debug("After clean command JSON for cache {} {}", cacheName, cacheObject);
                writer.print(cacheObject.toString());
                break;
            }
        }
	}

}
