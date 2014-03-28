package ru.bio4j.service.bootstrap;

import ru.bio4j.service.ServiceLifecycle.Status;

import java.io.PrintStream;

public interface Bootstrap {
	
	void stopServices();
	
	void startServices();
	
	void restart(PrintStream out);
	
	Status getStatus();
	
}
