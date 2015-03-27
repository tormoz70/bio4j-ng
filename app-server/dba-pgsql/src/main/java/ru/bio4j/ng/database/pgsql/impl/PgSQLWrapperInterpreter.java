package ru.bio4j.ng.database.pgsql.impl;


import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.WrapperInterpreter;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.filter.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PgSQLWrapperInterpreter implements WrapperInterpreter {

    private static final Map<Class<?>, String> compareTemplates = new HashMap<Class<?>, String>() {{
        put(Eq.class, "%s = %s");
        put(Gt.class, "%s > %s");
        put(Ge.class, "%s >= %s");
        put(Lt.class, "%s < %s");
        put(Le.class, "%s <= %s");
        put(Bgn.class, "%s like %s");
        put(End.class, "%s like %s");
        put(Contains.class, "%s like %s");
    }};

    private static String decodeCompare(String alias, Expression e) {
        String column = appendAlias(alias, e.getColumn());
        Object value = e.getValue();
        String templ = compareTemplates.get(e.getClass());
        if(e instanceof Bgn)
            value = value+"%";
        if(e instanceof End)
            value = "%"+value;
        if(e instanceof Contains)
            value = "%"+value+"%";
        if (Strings.isString(value))
            value = "'"+value+"'";
        if (e.ignoreCase()) {
            value = "upper("+value+")";
            column = "upper("+column+")";
        }

        if (value != null && value instanceof Date)
            value = "to_date('YYYYMMDD', '" + new SimpleDateFormat("YYYYMMdd").format(value) + "')";
        return "("+String.format(templ, column, value)+")";
    }

    private static String appendAlias(String alias, String column){
        return (Strings.isNullOrEmpty(alias) ? ""  : alias+".")+column;
    }

    @Override
    public String filterToSQL(String alias, Expression e) {
        if(e instanceof Logical){
            String logicalOp = (e instanceof And) ? " and " : (
                    (e instanceof Or) ? " or " : " unknown-logical "
            );
            StringBuilder rslt = new StringBuilder();
            for(Object chld : e.getChildrens()){
                rslt.append(((rslt.length() == 0) ? "" : logicalOp) + this.filterToSQL(alias, (Expression) chld));
            }
            return "("+rslt.toString()+")";
        }

        if(e instanceof Compare){
            return decodeCompare(alias, e);
        }
        if(e instanceof IsNull){
            return "("+appendAlias(alias, e.getColumn()) + " is null)";
        }
        if(e instanceof Not){
            return "not "+this.filterToSQL(alias, (Expression) e.getChildrens().get(0))+"";
        }
        return null;
    }

    @Override
    public String sortToSQL(String alias, List<Sort> sort) {
        if(sort != null) {
            StringBuilder result = new StringBuilder();
            char comma;
            for (Sort s : sort){
                comma = (result.length() == 0) ? ' ' : ',';
                result.append(String.format("%s %s.%s %s", comma, alias, s.getFieldName().toUpperCase(), s.getDirection().toString()));
            }
            return result.toString();
        }
        return null;
    }
}
