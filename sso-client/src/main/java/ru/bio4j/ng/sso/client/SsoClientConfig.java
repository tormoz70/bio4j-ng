package ru.bio4j.ng.sso.client;

import ru.bio4j.ng.model.transport.AnConfig;
import ru.bio4j.ng.model.transport.Prop;

public class SsoClientConfig extends AnConfig {
    @Prop(name = "sso.service.url")
    private String ssoServiceUrl;

    public String getSsoServiceUrl() {
        return ssoServiceUrl;
    }

    public void setSsoServiceUrl(String ssoServiceUrl) {
        this.ssoServiceUrl = ssoServiceUrl;
    }
}
