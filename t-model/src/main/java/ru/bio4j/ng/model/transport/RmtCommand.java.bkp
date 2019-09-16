package ru.bio4j.ng.model.transport;

/**
 * Команды для управления процессом на сервере
 */
public enum RmtCommand {
	RUN, BREAK, GETSTATE, GETRESULT;

	public int getCode() {
		return this.ordinal();
	}

    public static RmtCommand decode(String str) {
        for (RmtCommand cmd : RmtCommand.values()) {
            if (cmd.toString().equalsIgnoreCase(str)) {
                return cmd;
            }
        }
        return null;
    }

}
