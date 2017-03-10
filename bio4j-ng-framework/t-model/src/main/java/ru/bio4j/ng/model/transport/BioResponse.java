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
     * Содержит данные пользователя, в ответ на запрос аутентификации
     */
    private User user;

    /**
     * Содержит true если все при обработке запроса не произошло ни каких ошибок
     */
    private boolean success;

    /**
     * Если на сервере произошла ошибка при обработке запроса, то здесь возвращается объект содержащий ошибку
     */
    private BioError exception;

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
     * Пакет данных. В ответ на BioRequestJStoreGet, BioRequestJStorePost
     */
    private StoreData packet;

    /**
     * Сортировка, которая использовалясь при запросе к БД
     */
    private List<Sort> sort;

    /**
     * Фильтрация, которая использовалясь при запросе к БД
     */
    private Expression filter;

    /**
     * Значение первичного ключа, которое использовалось для локации
     */
    private Object location;

    /**
     * Значение первичного ключа, которое использовалось для получения единичной записи
     */
    private Object id;

    /**
     * Ответы от дочерних post-запросов
     */
    private List<BioResponse> slaveResponses;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public BioError getException() {
        return exception;
    }

    public void setException(BioError exception) {
        this.exception = exception;
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

    public List<Sort> getSort() {
        return sort;
    }

    public void setSort(List<Sort> sort) {
        this.sort = sort;
    }

    public Expression getFilter() {
        return filter;
    }

    public void setFilter(Expression filter) {
        this.filter = filter;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<BioResponse> getSlaveResponses() {
        return slaveResponses;
    }

    public void setSlaveResponses(List<BioResponse> slaveResponses) {
        this.slaveResponses = slaveResponses;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }
}
