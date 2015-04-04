package ru.bio4j.ng.rapi.resful.ims;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ayrat on 04.04.2015.
 */
public class RestApplication extends Application {

    private final RESTFulService myRestResource;

    public RestApplication( RESTFulService myRestResource ) {
        this.myRestResource = myRestResource;
    }

    @Override
    public Set<Object> getSingletons() {
        HashSet<Object> set = new HashSet<Object>();
        set.add( this.myRestResource );
        return set;
    }
}
