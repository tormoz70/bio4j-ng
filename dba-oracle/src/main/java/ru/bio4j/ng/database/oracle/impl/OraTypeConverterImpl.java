package ru.bio4j.ng.database.oracle.impl;

import oracle.jdbc.OracleTypes;
import ru.bio4j.ng.commons.converter.Types;
import ru.bio4j.ng.database.commons.SqlTypeConverterImpl;

import java.sql.ResultSet;

public class OraTypeConverterImpl extends SqlTypeConverterImpl {

    @Override
    public int read (Class<?> type, int stringSize, boolean isCallableStatment) {
        if (type == ResultSet.class)
            return OracleTypes.CURSOR;
        else if (Types.typeIsReal(type))
            return java.sql.Types.DECIMAL;
        else
            return super.read(type, stringSize, isCallableStatment);
    }

}
