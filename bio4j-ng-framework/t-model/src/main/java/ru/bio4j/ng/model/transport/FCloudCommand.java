package ru.bio4j.ng.model.transport;

/**
 * Команды для управления процессом на сервере
 */
public enum FCloudCommand {
	REMOVE(1), UPLOAD(2), DOWNLOAD(3), FILESPEC(4), METADATA(5), RUNIMPORT(6);

	private int code;
	FCloudCommand(int code){
	    this.code = code;
    }
	public int getCode() {
		return this.code;
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
