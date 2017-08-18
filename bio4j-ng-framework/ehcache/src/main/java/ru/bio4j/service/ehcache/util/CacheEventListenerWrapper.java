package ru.bio4j.service.ehcache.util;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import static ru.bio4j.service.ehcache.util.CacheUtil.getCacheByCode;

public class CacheEventListenerWrapper implements CacheEventListener {
	
	ru.bio4j.service.ehcache.util.CacheEventListener listener;
	
	public CacheEventListenerWrapper(ru.bio4j.service.ehcache.util.CacheEventListener listener) {
		this.listener = listener;
	}

	@Override
	public void notifyElementRemoved(Ehcache cache, Element element)
			throws CacheException {
		listener.notifyElementRemoved(getCacheByCode(cache.getName()), element.getObjectValue());
	}

	@Override
	public void notifyElementPut(Ehcache cache, Element element)
			throws CacheException {
		listener.notifyElementPut(getCacheByCode(cache.getName()), element.getObjectValue());
	}

	@Override
	public void notifyElementUpdated(Ehcache cache, Element element)
			throws CacheException {
		listener.notifyElementUpdated(getCacheByCode(cache.getName()), element.getObjectValue());
	}

	@Override
	public void notifyElementExpired(Ehcache cache, Element element) {
		listener.notifyElementExpired(getCacheByCode(cache.getName()), element.getObjectValue());
	}

	@Override
	public void notifyElementEvicted(Ehcache cache, Element element) {
		listener.notifyElementEvicted(getCacheByCode(cache.getName()), element.getObjectValue());
	}

	@Override
	public void notifyRemoveAll(Ehcache cache) {
		listener.notifyRemoveAll(getCacheByCode(cache.getName()));
	}

	@Override
	public void dispose() {
		
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
