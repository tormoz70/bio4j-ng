package ru.bio4j.ng.model.transport;


import java.util.ArrayList;
import java.util.List;

/**
 * Базовый класс для всех запросов
 */
public class BioRequest {

    /** Параметры запроса.
     * Данные параметры передаются на сервер в виде параметров Http-запроса
     * При этом параметры, которые являются служебными параметрами
     * протокола сюда не попадают (при дисереализации),
     * только параметры, которые добавлены к запросу вручную
     */
    private List<Param> params;

    /** Логин в виде username/password */
    private String login;

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

    public List<Param> getParams() {
        return params;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

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
