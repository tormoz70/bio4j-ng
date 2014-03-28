package ru.bio4j.service.bootstrap.command;

import java.io.PrintStream;

import org.apache.felix.shell.Command;

import ru.bio4j.service.bootstrap.BootstrapService;

public class StopServiceCommand implements Command {

	private final BootstrapService bootstrapService;
	
	public StopServiceCommand(BootstrapService bootstrapService) {
		super();
		this.bootstrapService = bootstrapService;
	}

	@Override
	public String getName() {
		return "bootstrap:stop";
	}

	@Override
	public String getUsage() {
		return "bootstrap:stop";
	}

	@Override
	public String getShortDescription() {
		return "Stop all services.";
	}

	@Override
	public void execute(String line, PrintStream out, PrintStream err) {
		bootstrapService.stopServices(out);
	}

}
