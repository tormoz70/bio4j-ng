package ru.bio4j.service.bootstrap.command;

import java.io.PrintStream;

import org.apache.felix.shell.Command;

import ru.bio4j.service.bootstrap.BootstrapService;

public class StartServiceCommand implements Command {

	private final BootstrapService bootstrapService;
	
	public StartServiceCommand(BootstrapService bootstrapService) {
		super();
		this.bootstrapService = bootstrapService;
	}

	@Override
	public String getName() {
		return "bootstrap:start";
	}

	@Override
	public String getUsage() {
		return "bootstrap:start";
	}

	@Override
	public String getShortDescription() {
		return "Start all services.";
	}

	@Override
	public void execute(String line, PrintStream out, PrintStream err) {
		bootstrapService.startServices(out);
	}
	
}
