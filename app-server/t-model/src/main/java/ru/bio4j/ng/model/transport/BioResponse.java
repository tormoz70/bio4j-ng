package ru.bio4j.ng.model.transport;

import java.util.List;

/**
 * Базовый класс для всех ответов сервера на запросы BioRequest
 * Примечание: Теперь все запросы на сервер подразумеваются как "долгие процессы"
 */
public class BioResponse extends AjaxResponse {

    /**
     * Код информационного объекта
     */
    private String bioCode;

    /**
     * параметры информационного объекта
     * Тут теже параметры что были переданы на сервер,
     * но с добавленными out и измененными in/out параметрами
     */
    private List<Param> bioParams;

    /**
     * Пакет соделжит состояние "долгой операции" - ответ запрос BioRequestLongOp
     */
    private RmtStatePack rmtStatePack;

    public List<Param> getBioParams() {
        return bioParams;
    }

    public void setBioParams(List<Param> bioParams) {
        this.bioParams = bioParams;
    }

    public RmtStatePack getRmtStatePack() {
        return rmtStatePack;
    }

    public void setRmtStatePack(RmtStatePack rmtStatePack) {
        this.rmtStatePack = rmtStatePack;
    }

    public String getBioCode() {
        return bioCode;
    }

    public void setBioCode(String bioCode) {
        this.bioCode = bioCode;
    }
}
