package ru.bio4j.service.bootstrap.command;

import java.io.PrintStream;

import org.apache.felix.shell.Command;

public class ErrorsQueueListenerCommand implements Command {

	@Override
	public String getName() {
		return "bootstrap:errorsqueue";
	}

	@Override
	public String getUsage() {
		return "bootstrap:errorsqueue <command>. where <command> is one of the 'enable' or 'disable'";
	}

	@Override
	public String getShortDescription() {
		return "Enable or disable errors queue listener";
	}

	@Override
	public void execute(String line, PrintStream out, PrintStream err) {
	}

}
