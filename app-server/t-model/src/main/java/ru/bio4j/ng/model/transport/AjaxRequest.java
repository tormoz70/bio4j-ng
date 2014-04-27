package ru.bio4j.ng.model.transport;

import java.util.ArrayList;
import java.util.List;


/** 
 * Базовый класс для всех запросов к серверу приложений
 */
public class AjaxRequest {

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
     * Может быть передано несколько запросов Bio
     * При этом если это запросы типа BioRequestJStorePost,
     * то выходные параметры каждого из запросов
     * должны добавляться во входные парамеры последующего
     */
	private final List<BioRequest> bioRequests = new ArrayList<>();

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

    public List<BioRequest> getBioRequest() {
        return this.bioRequests;
    }

}
