package ru.bio4j.service;

public interface ServiceController {

	String getServiceName();

	void start() throws Exception;

	void stop() throws Exception;
	
}
