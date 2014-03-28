package ru.bio4j.service;

public interface ServiceLifecycle {

	enum Status {
        STARTING,
		STARTED,
		ERROR,
        STOPPED,
        STOPPING;
	};

	String getName();

	Status getStatus();

	Integer getOrder();

	void startWork();

	void finishWork();

}
