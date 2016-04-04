package ru.bio4j.ng.security.module.impl;

import ru.bio4j.ng.commons.types.Prop;
import ru.bio4j.ng.database.api.SQLContextConfig;

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
