package ru.bio4j.ng.model.transport;

/**
 * Состояние процесса на сервере
 */
public enum RmtState {
    UNDEFINED(-1),
    /**
     * Готов к запуску
     */
    REDY(0),
    /**
     * Запускается
     */
    STARTING(1),
    /**
     * Выполняется...
     */
    RUNNING(2),
    /**
     * Выполнен
     */
    DONE(3),
    /**
     * Останов...
     */
    BREAKING(4),
    /**
     * Остановлен
     */
    BREAKED(5),
    /**
     * Ошибка
     */
    ERROR(6),
    /**
     * В очереди...
     */
    WAITING(7),
    /**
     * Удален
     */
    DISPOSED(8);

    private final int code;
    RmtState(int code){
        this.code = code;
    }
    public int getCode() {
        return this.code;
    }

    public static RmtState decode(int code){
        for (RmtState type : values()) {
            if (type.getCode() == code)
                return type;
        }
        return UNDEFINED;
    }
}
