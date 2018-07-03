package ru.bio4j.ng.security.module.impl;

import ru.bio4j.ng.service.api.Prop;
import ru.bio4j.ng.service.api.SQLContextConfig;

public class SecurityConfig extends SQLContextConfig {

    @Prop(name = "bio.screenplays.path")
    private String screenplaysPath = null;

    public String getScreenplaysPath() {
        return screenplaysPath;
    }

    public void setScreenplaysPath(String screenplaysPath) {
        this.screenplaysPath = screenplaysPath;
    }

}
