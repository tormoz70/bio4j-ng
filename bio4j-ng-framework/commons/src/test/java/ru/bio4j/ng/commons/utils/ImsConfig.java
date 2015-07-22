package ru.bio4j.ng.commons.utils;

import ru.bio4j.ng.commons.types.Prop;
import ru.bio4j.ng.commons.utils.Utl;

public class ImsConfig extends SQLContextConfig {

    @Prop(name = "ims.screenplays.path")
    private String screenplaysPath = null;

    public String getScreenplaysPath() {
        return screenplaysPath;
    }

    public void setScreenplaysPath(String screenplaysPath) {
        this.screenplaysPath = screenplaysPath;
    }

}
