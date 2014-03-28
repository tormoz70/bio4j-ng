package ru.bio4j.service.message.amqp.plugin;

import ru.bio4j.service.message.amqp.statistic.AmqpChannelsStatistic;
import ru.bio4j.service.message.amqp.statistic.AmqpConnectionInfo;
import ru.bio4j.service.monitor.Monitor;

public interface AmqpServiceManager extends Monitor {

	AmqpChannelsStatistic getChannelsStatistic();

	AmqpConnectionInfo getConnectionInfo();
	
}
