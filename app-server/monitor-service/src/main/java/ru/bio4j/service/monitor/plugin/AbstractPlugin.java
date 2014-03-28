package ru.bio4j.service.monitor.plugin;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.service.monitor.Monitor;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPlugin {

	private final static Logger LOG = LoggerFactory.getLogger(AbstractPlugin.class);
	
	protected static final String JSON_ACTION_FIELD = "action";
	protected static final String JSON_LOAD_ACTION = "load";
	protected static final String JSON_STATUS_FIELD = "status";
	protected static final String JSON_RESPONSE_FIELD = "response";
	protected static final String JSON_RESULT_FIELD = "result";
	protected static final String JSON_RELOAD_FIELD = "reload";

	private volatile Monitor service;
	
	public AbstractPlugin(Monitor service) {
		super();
		this.service = service;
	}

	public String getLabel() {
		return getService().pluginLabel();
	}

	public String getTitle() {
		return getService().pluginTitle();
	}
	
	public URL getResource(String path) {
		LOG.debug("Attempting to get resource using path " + path);
		return PluginUtils.loadResource(getClass(), path);
	}
	
	public final Map<String, String> resolveVariables() {
		HashMap<String, String> map = new HashMap<>();
		resolveVariables(map);
		return map;
	}

	protected void resolveVariables(Map<String, String> map) {}
	
	public String loadTemplate() {
		LOG.debug("Attempting to load template " + getTemplate());
		return readTemplateFile(getClass(), getTemplate());
	}

	protected String readTemplateFile(String templateFile) {
		return readTemplateFile(getClass(), templateFile);
	}
	
	private String readTemplateFile(Class<?> clazz, String templateFile) {
		InputStream templateStream = clazz.getResourceAsStream(templateFile);
		if (templateStream != null) {
			try {
				String str = IOUtils.toString(templateStream, "UTF-8"); //$NON-NLS-1$
				switch (str.charAt(0)) { // skip BOM
				case 0xFEFF: // UTF-16/UTF-32, big-endian
				case 0xFFFE: // UTF-16, little-endian
				case 0xEFBB: // UTF-8
					return str.substring(1);
				}
				return str;
			} catch (IOException e) {
				LOG.error("readTemplateFile: Error loading " + templateFile, e);
				throw new RuntimeException(
						"readTemplateFile: Error loading " + templateFile + ": " + e);
			} finally {
				IOUtils.closeQuietly(templateStream);
			}
		}
		LOG.debug("readTemplateFile: File '" + templateFile + "' not found through class " + clazz);
		return "";
	}
	
	public Monitor getService() {
		return service;
	}
	
	public void dispose() {
		service = null;
	}
	
	public abstract String getTemplate();

	public abstract void processRequest(HttpServletRequest request, PrintWriter writer);
}
