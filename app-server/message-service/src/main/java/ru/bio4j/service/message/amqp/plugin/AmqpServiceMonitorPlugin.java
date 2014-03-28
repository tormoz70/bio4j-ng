package ru.bio4j.service.message.amqp.plugin;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bio4j.service.message.amqp.statistic.AmqpChannelsStatistic;
import ru.bio4j.service.message.amqp.statistic.AmqpConnectionInfo;
import ru.bio4j.service.monitor.Monitor;
import ru.bio4j.service.monitor.plugin.AbstractPlugin;

public class AmqpServiceMonitorPlugin extends AbstractPlugin {

	private static Logger LOG = LoggerFactory.getLogger(AmqpServiceMonitorPlugin.class);
	
	public AmqpServiceMonitorPlugin(Monitor monitor) {
		super(monitor);
	}

	@Override
	public String getTitle() {
		return "RabbitMQ Service Monitor";
	}

	@Override
	public String getTemplate() {
		return "/templates/rabbitmq-service-monitor.html";
	}

	@Override
	public void processRequest(HttpServletRequest request, PrintWriter writer) {
		String action = request.getParameter(JSON_ACTION_FIELD);
		if (JSON_LOAD_ACTION.equals(action)) {
			AmqpServiceManager rabbitMQServiceManager = (AmqpServiceManager) getService();
			AmqpConnectionInfo connectionInfo = rabbitMQServiceManager.getConnectionInfo();
			AmqpChannelsStatistic statistic = rabbitMQServiceManager.getChannelsStatistic();
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("connection", connectionInfo.toJSON());
				jsonObject.put("queues", statistic.toJSON());
			} catch (JSONException e) {
				LOG.error("Problem with JSON", e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("RabbitMQ JSON " + jsonObject.toString());
			}
			writer.print(jsonObject.toString());
		}
	}

}
