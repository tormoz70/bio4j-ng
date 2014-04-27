package ru.bio4j.ng.model.transport;


import java.util.List;

/**
 * Базовый класс для всех запросов у которых есть ссылка на код информационного объекта (bioCode)
 */
public class BioRequest {

    /**
     * Код запрашиваемого инф. объекта. Фактически - это путь к файлу описания метаданных запроса
     */
    private String bioCode;

    /**
     * параметры информационного объекта
     */
    private List<Param> bioParams;

    /** Команда которую надо передать на сервер
     * для управления удаленным процессом
     */
    private RmtCommand cmd;

    public String getBioCode() {
        return bioCode;
    }

    public void setBioCode(String bioCode) {
        this.bioCode = bioCode;
    }

    public List<Param> getBioParams() {
        return bioParams;
    }

    public void setBioParams(List<Param> bioParams) {
        this.bioParams = bioParams;
    }

}
