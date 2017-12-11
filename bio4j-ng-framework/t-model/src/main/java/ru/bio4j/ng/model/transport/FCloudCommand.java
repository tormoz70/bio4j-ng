package ru.bio4j.ng.model.transport;

/**
 * Команды для управления процессом на сервере
 */
public enum FCloudCommand {
	REMOVE, UPLOAD, DOWNLOAD, FILESPEC, RUNIMPORT;

	public int getCode() {
		return this.ordinal();
	}

    public static FCloudCommand decode(String str) {
        for (FCloudCommand cmd : FCloudCommand.values()) {
            if (cmd.toString().equalsIgnoreCase(str)) {
                return cmd;
            }
        }
        return null;
    }
}
