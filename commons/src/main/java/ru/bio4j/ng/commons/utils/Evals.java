package ru.bio4j.ng.commons.utils;


import ru.bio4j.ng.model.transport.Param;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.List;

public class Evals {

    private ScriptEngine engine;
    private static Evals instance = new Evals();
    private Evals() {
        engine = new ScriptEngineManager().getEngineByExtension("js");
        if (engine == null)
            engine = new ScriptEngineManager().getEngineByExtension("JavaScript");
        if (engine == null)
            engine = new ScriptEngineManager().getEngineByExtension("nashorn");

    }

    public static Evals getInstance(){
        return instance;
    }

    private Object _eval(String js) throws Exception {
        if(engine == null)
            throw new Exception("Error on exec javascript. Message: JavaScript engine not loaded!");
        try{
            return engine.eval(js);
        } catch (Exception e) {
            throw new Exception(String.format("Error on exec javascript {%s}, Message: %s", js, e.getMessage()));
        }
    }

    public boolean runCondition(String condition, List<Param> prms) throws Exception {
        String CS_JSVAR = "var %s = %s;";
        if(prms != null) {
            for (Param p : prms) {
                Object val = p.getValue();
                String valStr = "null";
//                String valStr = Jsons.encode(val);
                if(val != null){
                    if (val instanceof String)
                        valStr = String.format("'%s'", val);
                    else
                        valStr = String.format("%s", ""+val);
                } else
                    valStr = "null";

                _eval(String.format(CS_JSVAR, p.getName(), valStr));
            }
        }
        return (boolean)_eval(condition);
    }
}
