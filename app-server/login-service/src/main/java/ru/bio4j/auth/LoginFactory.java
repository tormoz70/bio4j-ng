package ru.bio4j.auth;

import org.apache.felix.jaas.LoginModuleFactory;
import org.osgi.framework.ServiceRegistration;

import javax.security.auth.spi.LoginModule;

public class LoginFactory implements LoginModuleFactory {

    private ServiceRegistration loginModuleFactoryReg;

    @Override
    public LoginModule createLoginModule() {
/*        Dictionary<String,Object> lmProps = new Hashtable<>();
        lmProps.put(LoginModuleFactory.JAAS_CONTROL_FLAG, controlFlag);
        lmProps.put(LoginModuleFactory.JAAS_REALM_NAME,
                PropertiesUtil.toString(config.get(PROP_REALM), null));
        lmProps.put(Constants.SERVICE_RANKING,
                PropertiesUtil.toInteger(config.get(JAAS_RANKING), 0));

        loginModuleFactoryReg = context.registerService(
                LoginModuleFactory.class.getName(), this, lmProps);
                */
        return null;
    }
}
