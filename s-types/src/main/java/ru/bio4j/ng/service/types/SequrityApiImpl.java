package ru.bio4j.ng.service.types;

//import ru.bio4j.ng.commons.utils.Jsons;

import ru.bio4j.ng.commons.utils.Jecksons;
import ru.bio4j.ng.commons.utils.SrvcUtils;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.ABean;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.BioQueryParams;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.service.api.SecurityApi;
import ru.bio4j.ng.service.api.SecurityService;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SequrityApiImpl implements SecurityApi {

    private SecurityService securityService;

    public void init(SecurityService securityService) {
        this.securityService = securityService;
    }

    /***
     * обрабатывает "стандарный" запрос "/curusr"
     * @param request
     * @return User
     */
    @Override
    public User doGetUser(final HttpServletRequest request) {
        if(securityService == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        final WrappedRequest req = (WrappedRequest)request;
        final BioQueryParams qprms = req.getBioQueryParams();
        return securityService.getUser(qprms);
    }

    /***
     * обрабатывает "стандарный" запрос "/login"
     * @param request
     * @return User
     */
    @Override
    public User doLogin(final HttpServletRequest request) {
        if(securityService == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        final WrappedRequest req = (WrappedRequest)request;
        final BioQueryParams qprms = req.getBioQueryParams();
        User user = securityService.login(qprms);
        req.setUser(user);
        return user;
    }

    /***
     * обрабатывает "стандарный" запрос "/logoff"
     * @param request
     */
    @Override
    public void doLogoff(final HttpServletRequest request) {
        if(securityService == null)
            throw new IllegalArgumentException("SecurityHandler not defined!");
        final WrappedRequest req = (WrappedRequest)request;
        final BioQueryParams qprms = req.getBioQueryParams();
        securityService.logoff(qprms);
    }


}
