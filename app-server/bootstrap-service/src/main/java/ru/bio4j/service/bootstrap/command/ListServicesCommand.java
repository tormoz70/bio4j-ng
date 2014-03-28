package ru.bio4j.service.bootstrap.command;

import java.io.PrintStream;

import org.apache.felix.shell.Command;

import ru.bio4j.service.bootstrap.BootstrapService;
import ru.bio4j.service.ServiceLifecycle;

public class ListServicesCommand implements Command {

	private final BootstrapService bootstrapService;
	
	public ListServicesCommand(BootstrapService bootstrapService) {
		super();
		this.bootstrapService = bootstrapService;
	}

	@Override
	public String getName() {
		return "bootstrap:list";
	}

	@Override
	public String getUsage() {
		return "bootstrap:list";
	}

	@Override
	public String getShortDescription() {
		return "List of all all services.";
	}

	@Override
	public void execute(String line, PrintStream out, PrintStream err) {
		for (ServiceLifecycle service : bootstrapService.getServices()) {
			out.println("Service " + service.getName());
		}
	}

}
