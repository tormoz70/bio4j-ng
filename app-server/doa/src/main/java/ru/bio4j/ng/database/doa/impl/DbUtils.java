package ru.bio4j.ng.database.doa.impl;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;

/**
 * Утилиты для работы с метаданными СУБД
 */
public class DbUtils {

    public static int paramSqlType(Param param) {
        int stringSize = 0;
        if(param.getType() == MetaType.STRING){
            if(((param.getDirection() == Param.Direction.INOUT) || (param.getDirection() == Param.Direction.IN)) && (stringSize == 0))
                stringSize = Strings.isNullOrEmpty(Paramus.paramValueAsString(param)) ? 0 : Paramus.paramValueAsString(param).length();
        }
        boolean isCallable = (param.getDirection() == Param.Direction.INOUT) || (param.getDirection() == Param.Direction.OUT);
        return SqlTypeConverter.read(param.getType(), stringSize, isCallable);
    }

}
