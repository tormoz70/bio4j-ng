package ru.bio4j.service.monitor;

import org.apache.felix.webconsole.AbstractWebConsolePlugin;
import org.apache.felix.webconsole.DefaultVariableResolver;
import org.apache.felix.webconsole.WebConsoleConstants;
import org.apache.felix.webconsole.WebConsoleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.service.monitor.ServicePluginsHolder.Plugin;
import ru.bio4j.service.monitor.plugin.AbstractPlugin;
import ru.bio4j.service.monitor.plugin.PluginUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ServicesMonitorConsolePanel extends AbstractWebConsolePlugin {

    private static final String PLUGIN_ID = "pluginId";

    private final static Logger LOG = LoggerFactory.getLogger(ServicesMonitorConsolePanel.class);

    private class VariableResolverWriter extends PrintWriter {

        private DefaultVariableResolver variableResolver;

        public VariableResolverWriter(Writer out, DefaultVariableResolver variableResolver) {
            super(out);
            this.variableResolver = variableResolver;
        }

        @Override
        public void println(String x) {
            String passValue = x;
            if (x.contains("${")) {
                for (Object variableKey : variableResolver.keySet()) {
                    String key = (String) variableKey;
                    passValue = passValue.replace("${" + key + "}", variableResolver.resolve(key));
                }
            }
            super.println(passValue);
        }
    };

    private volatile ServicePluginsHolder openBackPluginHolder;

    public ServicesMonitorConsolePanel(
        ServicePluginsHolder pluginHolder) {
        super();
        this.openBackPluginHolder = pluginHolder;
    }

    @Override
    public String getLabel() {
        return "%bio4j.services.monitor.label";
    }

    @Override
    public String getTitle() {
        return "%bio4j.services.monitor.label";
    }

    @Override
    protected void renderContent(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        PrintWriter pw = res.getWriter();
        try {
            Set<Map.Entry<String, Plugin>> plugins = openBackPluginHolder.getPlugins();
            if (plugins.isEmpty()) {
                addVariable("nothing.to.display", "Nothing to display", req);
                pw.println(readTemplateFile("/templates/nothing-to-display.html"));
            } else {
                buildTabs(pw, plugins, req);
            }
        } finally {
            pw.flush();
        }
    }

    @SuppressWarnings("unchecked")
    private void addVariable(Object key, Object value, HttpServletRequest request) {
        DefaultVariableResolver variableResolver = (DefaultVariableResolver) WebConsoleUtil.getVariableResolver(request);
        variableResolver.put(key, value);
    }

    private void buildTabs(PrintWriter pw, Set<Map.Entry<String, Plugin>> plugins, HttpServletRequest request) {
        pw.println("<script type=\"text/javascript\" src=\"${pluginRoot}/res/ui/ui.tabs.paging.js\"></script>");
        pw.println("<script type=\"text/javascript\" src=\"${pluginRoot}/res/ui/servicesmonitor.js\"></script>");

        addVariable("services.amount", plugins.size() + " monitors in total", request);

        pw.println("<p class=\"statline\">${services.amount}</p>");

        pw.println("<div id='services-tabs'> <!-- tabs container -->");
        pw.println("<ul> <!-- tabs on top -->");

        String pluginRoot = request.getAttribute( WebConsoleConstants.ATTR_PLUGIN_ROOT ) + "/";
        for (Entry<String, Plugin> pluginEntry : plugins) {
            Plugin plugin = pluginEntry.getValue();
            AbstractPlugin openBackPlugin = plugin.getPlugin();
            if (openBackPlugin != null) {
                String label = openBackPlugin.getLabel();
                String title = openBackPlugin.getTitle();
                pw.println("<li><a href=" + pluginRoot + label + ".nfo>" + title + "</a></li>");
            }
        }
        pw.println("</ul> <!-- end tabs on top -->");
        pw.println("</div> <!-- end tabs container -->");

        pw.println("<div id=\"waitDlg\" title=\"${configStatus.wait}\" class=\"ui-helper-hidden\"><img src=\"${appRoot}/res/imgs/loading.gif\" alt=\"${configStatus.wait}\" />${configStatus.wait.msg}</div>");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String requestPathInfo = request.getPathInfo();
        if (requestPathInfo.endsWith("nfo")) {
            String pluginLabel = getRequestedPluginLabel(request);

            LOG.debug("Loading tab content for ruqest path " + requestPathInfo + " and plugin " + pluginLabel);

            DefaultVariableResolver variableResolver = (DefaultVariableResolver) WebConsoleUtil.getVariableResolver(request);
            try (VariableResolverWriter pw = new VariableResolverWriter(response.getWriter(), variableResolver)) {
                WebConsoleUtil.setNoCache(response);
                response.setContentType("text/html; charset=utf-8");

                pw.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"");
                pw.println("  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
                pw.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
                pw.println("<head><title>dummy</title></head><body><div>");
                Plugin plugin = openBackPluginHolder.getPlugin(pluginLabel);
                if (plugin != null) {
                    AbstractPlugin openBackPlugin = plugin.getPlugin();
                    if (openBackPlugin != null) {
                        LOG.debug("Resolving variables for plugin " + pluginLabel);
                        Map<String, String> variables = openBackPlugin.resolveVariables();
                        variableResolver.putAll(variables);
                        variableResolver.put("pluginLabel", pluginLabel);
                        pw.println(openBackPlugin.loadTemplate());
                        pw.println("</div></body></html>");
                    }
                    return;
                }
            }
            response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid monitor : " + pluginLabel);

        } else if (request.getPathInfo().endsWith("json")) {
            LOG.debug("Buidling tabs for UI");
            String pluginLabel = request.getParameter(PLUGIN_ID);
            Plugin plugin = openBackPluginHolder.getPlugin(pluginLabel);
            AbstractPlugin openBackPlugin = plugin.getPlugin();
            if (openBackPlugin != null) {
                openBackPlugin.processRequest(request, response.getWriter());
            }
            return;
        }
        super.doGet(request, response);
    }

    private String getRequestedPluginLabel(HttpServletRequest request) {
        String name = request.getPathInfo();
        int dotPos = name.lastIndexOf(".nfo");
        if ( dotPos != -1 ) {
            name = name.substring(0, dotPos);
        }
        name = name.substring( name.lastIndexOf('/') + 1);
        name = WebConsoleUtil.urlDecode( name );
        return name;
    }

    public URL getResource(String path) {
        if (path.contains("/res/ui/")) {
            String correctPath = path.substring(ServicePluginActivator.SERVLET_PATH.length() + 1);
            URL url = PluginUtils.loadResource(getClass(), correctPath);
            if (url == null) {
                for (Map.Entry<String, Plugin> pluginEntry : openBackPluginHolder.getPlugins()) {
                    AbstractPlugin plugin = pluginEntry.getValue().getPlugin();
                    if (plugin != null) {
                        url = plugin.getResource(correctPath);
                        if (url != null) {
                            break;
                        }
                    }
                }
            }
            return url;
        }
        return null;
    }
}
