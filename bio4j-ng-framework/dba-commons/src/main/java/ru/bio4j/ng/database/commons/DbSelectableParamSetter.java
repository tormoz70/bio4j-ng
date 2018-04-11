package ru.bio4j.ng.database.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.converter.MetaTypeConverter;
import ru.bio4j.ng.commons.converter.Types;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.database.api.SQLNamedParametersStatement;
import ru.bio4j.ng.database.api.SQLCommand;
import ru.bio4j.ng.database.api.SQLParamSetter;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;

import java.util.List;

public class DbSelectableParamSetter implements SQLParamSetter {
    private static final Logger LOG = LoggerFactory.getLogger(DbSelectableParamSetter.class);

    public DbSelectableParamSetter() {
    }

    @Override
    public void setParamsToStatement(SQLCommand command, List<Param> params) throws Exception {
        SQLNamedParametersStatement selectable = command.getStatement();
        final String sql = command.getPreparedSQL();
//        final List<String> paramsNames = Sqls.extractParamNamesFromSQL(sql);
        final List<String> paramsNames = selectable.getParamNames();
        for (int i = 0; i < paramsNames.size(); i++) {
            String paramName = paramsNames.get(i);
            Param param = Paramus.set(params).getParam(paramName);
            Paramus.instance().pop();
            if (param != null) {
                param.setId(i + 1);
                Object origValue = param.getValue();
                Object value = origValue == null ? null : Converter.toType(origValue, MetaTypeConverter.write(param.getType()));
                int targetType = DbUtils.getInstance().paramSqlType(param);
                if(value != null) {
                    //if(targetType == java.sql.Types.CHAR && param.getType() == MetaType.BOOLEAN && (value.getClass() == boolean.class || value.getClass() == Boolean.class))
                    //    selectable.setObjectAtName(paramName, ((boolean)value) ? "1" : "0", java.sql.Types.CHAR);
                    if(targetType == java.sql.Types.CHAR && param.getType() == MetaType.BOOLEAN) {
                        boolean boolVal = Converter.toType(value, Boolean.class);
                        selectable.setObjectAtName(paramName, boolVal ? "1" : "0", java.sql.Types.CHAR);
                    } else if(targetType == 0)
                        selectable.setObjectAtName(paramName, value);
                    else
                        selectable.setObjectAtName(paramName, value, targetType);
                } else {
                    if(targetType == 0)
                        selectable.setNullAtName(paramName);
                    else
                        selectable.setObjectAtName(paramName, null, targetType);
                }
            } else
                selectable.setNullAtName(paramName);
        }
    }


}
