package ru.bio4j.ng.model.transport;

/**
 * Команды для управления процессом на сервере
 */
public enum RmtCommand {
	RUN, BREAK, GET_STATE, GET_RESULT;

	public int getCode() {
		return this.ordinal();
	}
	
}
