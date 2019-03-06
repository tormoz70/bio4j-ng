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
        String CS_JSVAR = "%s = %s";
        if(prms != null) {
            for (Param p : prms) {
                Object val = p.getValue();
                String valStr = "null";
                //String valStr = Jsons.encode(val);
                if(val != null){
                    if (val instanceof String)
                        valStr = String.format("'%s'", val);
                    else
                        valStr = String.format("%s", ""+val);
                }

                engine.eval(String.format(CS_JSVAR, p.getName(), valStr));
            }
        }
        return (boolean)engine.eval(condition);
    }
}
