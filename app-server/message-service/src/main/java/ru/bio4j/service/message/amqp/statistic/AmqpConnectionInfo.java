package ru.bio4j.service.message.amqp.statistic;

import java.net.InetAddress;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmqpConnectionInfo {

	private static Logger LOG = LoggerFactory.getLogger(AmqpConnectionInfo.class);
	
	private boolean open = false;
	
	private Map<String, Object> serverProperties;
	
	private InetAddress inetAddress;
	
	private int port;

	public AmqpConnectionInfo() {
		super();
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public void setServerProperties(Map<String, Object> serverProperties) {
		this.serverProperties = serverProperties;
	}

	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("hostname", inetAddress.getHostName());
			jsonObject.put("address", inetAddress.getHostAddress());
			jsonObject.put("port", port);
			JSONArray serverProps = new JSONArray();
			for (Map.Entry<String, Object> propEntry : serverProperties.entrySet()) {
				JSONObject serverProperty = new JSONObject();
				serverProperty.put("name", propEntry.getKey());
				serverProperty.put("value", propEntry.getValue());
				serverProps.put(serverProperty);
			}
			jsonObject.put("serverProperties", serverProps);
			jsonObject.put("status", open);
		} catch (Exception e) {
			LOG.error("Could not build JSON", e);
		}
		return jsonObject;
	}
	
}
