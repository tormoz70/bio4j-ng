package ru.bio4j.ng.model.transport;

/**
 * Запуск хранимых процедур
 */

public class BioRequestRunLongOp extends BioRequest {

    /** Команда которую надо передать на сервер
     * для управления удаленным процессом
     */
    private RmtCommand cmd;

    /**
     * UID сессии которая идентифицирует запущенный процесс
     */
    private String sessionUID;

    public RmtCommand getCmd() {
        return cmd;
    }

    public void setCmd(RmtCommand cmd) {
        this.cmd = cmd;
    }

    public String getSessionUID() {
        return sessionUID;
    }

    public void setSessionUID(String sessionUID) {
        this.sessionUID = sessionUID;
    }
}
