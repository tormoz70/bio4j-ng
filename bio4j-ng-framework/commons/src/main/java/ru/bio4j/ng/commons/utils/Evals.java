package ru.bio4j.ng.commons.utils;


import ru.bio4j.ng.model.transport.Param;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;

public class Evals {

    private ScriptEngine engine;
    private static Evals instance = new Evals();
    private Evals() {
        engine = new ScriptEngineManager().getEngineByExtension("js");
    }

    public static Evals getInstance(){
        return instance;
    }

    public boolean runCondition(String condition, List<Param> prms) throws Exception {
        String CS_JSVAR = "%s = '%s'";
        for (Param p : prms)
            engine.eval(String.format(CS_JSVAR, p.getName(), (""+p.getValue()).toLowerCase()));
        return (boolean)engine.eval(condition);
    }
}
