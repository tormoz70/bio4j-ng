package ru.bio4j.service.sql.db;

import ru.bio4j.func.Function;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Реализация интерфейса процедуры
 * @title Процедура
 * @author rad
 */
public class ProcedureReal implements Procedure {


    private final Function<ResultSet, Parameter> columnCreator = new Function<ResultSet, Parameter>() {

        @Override
        public Parameter apply(ResultSet key) throws RuntimeException {
            final ParameterReal parameterReal = new ParameterReal();
            try {
                parameterReal.setSQLType(key.getInt("DATA_TYPE"));
                parameterReal.setName(key.getString("COLUMN_NAME"));
                parameterReal.setTypeName(key.getString("TYPE_NAME"));
                parameterReal.setSize(key.getInt("LENGTH"));
                parameterReal.setComment(key.getString("REMARKS"));
                parameterReal.setDefaultValue(key.getObject("COLUMN_DEF"));
                parameterReal.setPosition(key.getInt("ORDINAL_POSITION"));
                parameterReal.setParameterType(Parameter.ParameterType.typeForInt(key.getInt("COLUMN_TYPE")));
                return parameterReal;
            } catch (SQLException e) {
                throw new RuntimeException("Can't get parameter info", e);
            }
        }
    };
    private final DatabaseMetaData dbmd;
    private final Schema schema;
    private String name;

    public ProcedureReal(Schema schema) {
        this.schema = schema;
        this.dbmd = this.schema.getDB().getDatabaseMetaData();
    }

    /**
     * @title Получение схемы процедуры
     * @return Схема процедуры
     */
    @Override
    public Schema getSchema() {
        return schema;
    }

    /**
     * @title Получение списка параметров (колонок) процедуры
     * @return Список параметров (колонок) процедуры
     */
    @Override
    public List<Parameter> getColumns() {
        ResultSet rs = null;
        try {
            rs = dbmd.getProcedureColumns(null, schema.getName(), name, null);
            return DBUtils.listFromResultSet(
                    rs,
                    columnCreator);
        } catch(SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtils.close(rs);
        }
    }

    /**
     * Имя объекта
     * @title Получение имени процедуры
     * @return Имя процедуры
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @title Устновка имени процедуры
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
}