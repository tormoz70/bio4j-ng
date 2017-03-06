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
     * UID запущенного процесса
     */
    private String sessionuid;

    public RmtCommand getCmd() {
        return cmd;
    }

    public void setCmd(RmtCommand cmd) {
        this.cmd = cmd;
    }

    public String getSessionuid() {
        return sessionuid;
    }

    public void setSessionuid(String sessionuid) {
        this.sessionuid = sessionuid;
    }
}
