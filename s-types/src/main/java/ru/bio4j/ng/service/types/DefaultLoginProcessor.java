package ru.bio4j.ng.service.types;

import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.SrvcUtils;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.BioQueryParams;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultLoginProcessor implements LoginProcessor {

    private SecurityService securityService;

    public void init(SecurityService securityService) {
        this.securityService = securityService;
    }

    /***
     * обрабатывает "стандарный" запрос "/curusr"
     * @param request
     * @param response
     * @return true - продолжить обработку запроса, false - прекратить
     * @throws Exception
     */
    public boolean doGetUser(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if(securityService == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        final WrappedRequest req = (WrappedRequest)request;
        final BioQueryParams qprms = req.getBioQueryParams();
        User user = securityService.getUser(qprms);
        ABean result = SrvcUtils.buildSuccess(user);
        response.getWriter().append(Jsons.encode(result));
        return false;
    }

    /***
     * обрабатывает "стандарный" запрос "/login"
     * @param request
     * @param response
     * @return true - продолжить обработку запроса, false - прекратить
     * @throws Exception
     */
    public boolean doLogin(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if(securityService == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        final WrappedRequest req = (WrappedRequest)request;
        final BioQueryParams qprms = req.getBioQueryParams();
        User user = securityService.login(qprms);
        ABean result = SrvcUtils.buildSuccess(user);
        response.getWriter().append(Jsons.encode(result));
        return false;
    }

    /***
     * обрабатывает "стандарный" запрос "/logoff"
     * @param request
     * @param response
     * @return true - продолжить обработку запроса, false - прекратить
     * @throws Exception
     */
    public boolean doLogoff(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if(securityService == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        final WrappedRequest req = (WrappedRequest)request;
        final BioQueryParams qprms = req.getBioQueryParams();
        securityService.logoff(qprms);
        ABean result = SrvcUtils.buildSuccess(null);
        response.getWriter().append(Jsons.encode(result));
        return false;
    }

    /***
     * обрабатывает все остальные запросы
     * @param request
     * @param response
     * @return true - продолжить обработку запроса, false - прекратить
     * @throws Exception
     */
    public boolean doOthers(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if(securityService == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        final WrappedRequest req = (WrappedRequest)request;
        final BioQueryParams qprms = req.getBioQueryParams();
        User user = null;
        if (!Strings.isNullOrEmpty(qprms.login))
            user = securityService.login(qprms);
        else
            user = securityService.getUser(qprms);
        req.setUser(user);
        return true;
    }

}
