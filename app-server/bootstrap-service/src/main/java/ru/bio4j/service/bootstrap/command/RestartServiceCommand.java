package ru.bio4j.service.bootstrap.command;

import java.io.PrintStream;

import org.apache.felix.shell.Command;

import ru.bio4j.service.bootstrap.BootstrapService;

public class RestartServiceCommand implements Command {

	private final BootstrapService bootstrapService;
	
	public RestartServiceCommand(BootstrapService bootstrapService) {
		super();
		this.bootstrapService = bootstrapService;
	}

	@Override
	public String getName() {
		return "bootstrap:restart";
	}

	@Override
	public String getUsage() {
		return "bootstrap:restart";
	}

	@Override
	public String getShortDescription() {
		return "Restart all services.";
	}

	@Override
	public void execute(String line, PrintStream out, PrintStream err) {
		bootstrapService.restart(out);
	}

}
