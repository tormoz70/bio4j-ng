function cleanCache(cacheName) {
	askServerData.action = "clean";
	askServer(pluginRoot + "/reload.json?pluginId=" + pluginLabel + "&cache=" + cacheName, askServerData, createHtml);
}

function reloadCache(cacheName) {
	askServerData.action = "reload";
	askServer(pluginRoot + "/reload.json?pluginId=" + pluginLabel + "&cache=" + cacheName, askServerData, createHtml);
}

function createHtml(serverResult) {
	var oCache = serverResult.cache;
    var conf = oCache.configuration;
    var stat = oCache.statistics;
	var liveStat = oCache.live_statistics;

	var mainDiv = $('#cacheResult' + oCache.name);

	var mainDivExists = (mainDiv.length > 0);
	
	if (mainDivExists) {
		mainDiv.empty();
	}
	
	if (!mainDivExists) {
		var cacheHtml = "<div class=\"ui-widget-header ui-corner-top ui-corner-bottom buttonGroup\">";
		cacheHtml += "<button id='cleanCacheButton" + oCache.name + "' onclick=\"cleanCache('" + oCache.name + "');\" type=\"button\">Clean</button>&nbsp;";
		cacheHtml += "<button id='reloadCacheButton" + oCache.name + "' onclick=\"reloadCache('" + oCache.name + "');\" type=\"button\">Reload</button>";
		cacheHtml += "</div><br/>";
		//cacheHtml += "<div id='cacheResult" + oCache.name + "'>";
	}	
	cacheHtml += "<dl>";
    cacheHtml += "<dt><b>Name</b>:&nbsp;" + oCache.name + "&nbsp;<b>Size</b>:&nbsp;" + oCache.size + "</dt>";
    //configuration
    cacheHtml += "<dd style=\"margin-left: 10px;\"><b>Configuration</b></dd>";
    cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Cache Loader Timeout (millis)</b>: " + conf.cache_loader_timeout_millis + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Max Elements In Memory</b>: " + conf.max_elements_in_memory + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Max Memory Off Heap In Bytes</b>: " + conf.max_memory_off_heap_in_bytes + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Time To Idle (seconds)</b>: " + conf.time_to_idle_seconds + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Time To Live (seconds)</b>: " + conf.time_to_live_seconds + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Max Memory Off Heap</b>: " + conf.max_memory_off_heap + "</dd>";
    //statistics
	cacheHtml += "<dd style=\"margin-left: 10px;\"><b>Statistics</b></dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Average Get Time</b>: " + stat.average_get_time + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Average Search Time</b>: " + stat.average_search_time + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Cache Hits</b>: " + stat.cache_hits + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Cache Misses</b>: " + stat.cache_misses + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Eviction Count</b>: " + stat.eviction_count + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Object Count</b>: " + stat.object_count + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Off Heap Hits</b>: " + stat.off_heap_hits + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Off Heap Misses</b>: " + stat.off_heap_misses + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Searches Per Second</b>: " + stat.searches_per_second + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Writer Queue Size</b>: " + stat.writer_queue_size + "</dd>";
	//live statistics
    cacheHtml += "<dd style=\"margin-left: 10px;\"><b>Live Statistics</b></dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Hit Count</b>: " + liveStat.hit_count + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Miss Count</b>: " + liveStat.miss_count + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Miss Count Expired</b>: " + liveStat.miss_count_expired + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Evicted Count</b>: " + liveStat.evicted_count + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Expired Count</b>: " + liveStat.expired_count + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>In Memory Hit Count</b>: " + liveStat.in_memory_hit_count + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>in Memory Miss Count</b>: " + liveStat.in_memory_miss_count + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>In Memory Size</b>: " + liveStat.in_memory_size + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Max Get Time Millis</b>: " + liveStat.max_get_time_millis + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Min Get Time Millis</b>: " + liveStat.min_get_time_millis + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Off Heap Hit Count</b>: " + liveStat.off_heap_hit_count + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Off Heap Miss Count</b>: " + liveStat.off_heap_miss_count + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Off Heap Size</b>: " + liveStat.off_heap_size + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Put Count</b>: " + liveStat.put_count + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Removed Count</b>: " + liveStat.removed_count + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Size</b>: " + liveStat.size + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Update Count</b>: " + liveStat.update_count + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Writer Queue Length</b>: " + liveStat.writer_queue_length + "</dd>";
	cacheHtml += "<dd style=\"margin-left: 20px;\"><b>Average Get Time Millis</b>: " + liveStat.average_get_time_millis + "</dd>";
	cacheHtml += "</dl>";
	if (!mainDivExists) {
		cacheHtml += "</div>";	
	} else {
		mainDiv.append(cacheHtml);
	}
	return 	!mainDivExists ? cacheHtml : "";
}

function loadChaches() {
	askServerData.action = "load";
	askServer(pluginRoot + "/reload.json?pluginId=" + pluginLabel, askServerData, createTabs);
}

function createTabs(serverResult) {
	var jscaches = serverResult;
	$('#chachesTabs').tabs({
		ajaxOptions: {
	        dataFilter: function (result, type) {
	            var serverResult = window.JSON.parse(result);
	            this.dataTypes=['html'];
	            return createHtml(serverResult);
	        }
	    }
	});
	for (var i = 0; i < jscaches.length; i++) {
		var jscache = jscaches[i];
		var cacheUrl = pluginRoot + "/cache.json?pluginId=" + pluginLabel + "&action=info&cache=" + jscache;
		$('#chachesTabs').tabs("add", cacheUrl, "Cache <b>" + jscache + "</b>", i);
	}
}

$(document).ready(function() {
	loadChaches();
});
