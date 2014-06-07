package ru.bio4j.ng.client.extjs.filter;

import com.sun.security.auth.UserPrincipal;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.security.Principal;
import java.util.Map;

public class HttpLoginModule implements LoginModule {

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {

    }

    @Override
    public boolean login() throws LoginException {
        return false;
    }

    private void assignPrincipal(Principal p)
    {
//        // Make sure we dont add duplicate principals
//        if (!subject.getPrincipals().contains(p)) {
//            subject.getPrincipals().add(p);
//        }
//
//        if(debug) System.out.println("Assigned principal "+p.getName()+" of type "+ p.getClass().getName() +" to user "+userName);
    }
    @Override
    public boolean commit() throws LoginException {
//        if (debug)
//            System.err.println("HttpLoginModule: Commit");
//
//        if (!succeeded) {
//            // We didn't authenticate the user, but someone else did.
//            // Clean up our state, but don't add our principal to
//            // the subject
//            userName = null;
//            return false;
//        }
//
//        assignPrincipal(new UserPrincipal(userName));
//
//        //Based on the username, we can assign principals here
//        //Some examples for test....
//        assignPrincipal(new RolePrincipal("authenticateduser"));
//        assignPrincipal(new RolePrincipal("administrator"));
//        assignPrincipal(new CustomPrincipal("company1"));
//
//        // Clean up our internal state
//        userName = null;
//        commitSucceeded = true;
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        return false;
    }

    @Override
    public boolean logout() throws LoginException {
        return false;
    }
}
