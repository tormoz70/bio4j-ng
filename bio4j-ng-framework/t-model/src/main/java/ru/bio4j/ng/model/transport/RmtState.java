package ru.bio4j.ng.model.transport;

/**
 * Состояние процесса на сервере
 */
public enum RmtState {
    /**
     * Готов к запуску
     */
    REDY,
    /**
     * Запускается
     */
    STARTING,
    /**
     * Выполняется...
     */
    RUNNING,
    /**
     * Выполнен
     */
    DONE,
    /**
     * Останов...
     */
    BREAKING,
    /**
     * Остановлен
     */
    BREAKED,
    /**
     * Ошибка
     */
    ERROR,
    /**
     * В очереди...
     */
    WAITING,
    /**
     * Удален
     */
    DISPOSED;

    public int getCode() {
        return this.ordinal();
    }

}
