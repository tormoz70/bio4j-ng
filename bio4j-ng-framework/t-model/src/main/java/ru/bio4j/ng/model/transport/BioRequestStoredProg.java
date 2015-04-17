package ru.bio4j.ng.model.transport;

/**
 * Запуск хранимых процедур
 * С возможностью отслеживать прогресс с помощью DBMS_PIPE
 */

public class BioRequestStoredProg extends BioRequest {

	/**
     * UID сессии которая идентифицирует запущенный процесс
     */
    private String sessionUID;

    public String getSessionUID() {
        return sessionUID;
    }

    public void setSessionUID(String sessionUID) {
        this.sessionUID = sessionUID;
    }

}
