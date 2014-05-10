package ru.bio4j.ng.model.transport;

import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.StoreData;
import ru.bio4j.ng.model.transport.jstore.filter.Expression;

import java.util.List;

/**
 * Базовый класс для всех ответов сервера на запросы BioRequest
 * Примечание: Теперь все запросы на сервер подразумеваются как "долгие процессы"
 */
public class BioResponse {

    /**
     * Содержит true если все при обработке запроса не произошло ни каких ошибок
     */
    private boolean success;

    /**
     * Если на сервере произошди ошибки при обработке запроса, то здесь возвращается коллекция объектов содержащий ошибки
     * Их может быть много в общем случае
     */
    private List<Exception> exceptions;

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


    /**
     * Пакет данных. В ответ на BioRequestJStoreGet
     */
    private StoreData packet;

    /**
     * Сортировка, которая использовалясь при запросе к БД
     */
    private Sort sort;

    /**
     * Фильтрация, которая использовалясь при запросе к БД
     */
    private Expression filter;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<Exception> exceptions) {
        this.exceptions = exceptions;
    }

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

    public StoreData getPacket() {
        return packet;
    }

    public void setPacket(StoreData packet) {
        this.packet = packet;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public Expression getFilter() {
        return filter;
    }

    public void setFilter(Expression filter) {
        this.filter = filter;
    }

}
