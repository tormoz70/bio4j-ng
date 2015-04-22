package ru.bio4j.ng.model.transport;

import java.util.Date;
import java.util.List;

/**
 * Содержит данные о состоянии процесса на сервере
 */
public class RmtStatePack {

    /**
     * Дата/Время запуска
     */
    private Date started;

    /**
     * Прошло миллисекунд
     */
    private long duration;

    /**
     * Состояние
     */
    private RmtState state;

    /**
     * Ошибки
     */
    private List<Exception> exceptions;

    /**
     * По завершении имеется результирующий файл
     */
    private boolean hasResultFile;

    /**
     * UID сессии (для чтения прогресса из DBMS_PIPE)
     */
    private String sessionUID;

    /**
     * UID пользователя, запустившего процесс
     */
    private String ownerUID;

    /**
     * Имя пользователя, запустившего процесс
     */
    private String ownerName;

    /**
     * Последнее сообщение считанное PipeReader'ом
     */
    private String[] lastPipedLines;

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public RmtState getState() {
        return state;
    }

    public void setState(RmtState state) {
        this.state = state;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<Exception> exceptions) {
        this.exceptions = exceptions;
    }

    public boolean isHasResultFile() {
        return hasResultFile;
    }

    public void setHasResultFile(boolean hasResultFile) {
        this.hasResultFile = hasResultFile;
    }

    public String getSessionUID() {
        return sessionUID;
    }

    public void setSessionUID(String sessionUID) {
        this.sessionUID = sessionUID;
    }

    public String getOwnerUID() {
        return ownerUID;
    }

    public void setOwnerUID(String ownerUID) {
        this.ownerUID = ownerUID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String[] getLastPipedLines() {
        return lastPipedLines;
    }

    public void setLastPipedLines(String[] lastPipedLines) {
        this.lastPipedLines = lastPipedLines;
    }
}
