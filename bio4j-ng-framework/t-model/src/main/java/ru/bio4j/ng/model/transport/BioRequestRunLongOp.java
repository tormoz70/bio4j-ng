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
    private String sessionUid;

    public RmtCommand getCmd() {
        return cmd;
    }

    public void setCmd(RmtCommand cmd) {
        this.cmd = cmd;
    }

    public String getSessionUid() {
        return sessionUid;
    }

    public void setSessionUid(String sessionUid) {
        this.sessionUid = sessionUid;
    }
}
