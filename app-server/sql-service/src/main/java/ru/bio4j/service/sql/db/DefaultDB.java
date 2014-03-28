package ru.bio4j.service.sql.db;

import ru.bio4j.func.Function;
import ru.bio4j.func.Functions;
import ru.bio4j.service.sql.types.*;
import ru.bio4j.service.sql.util.DBTools;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultDB implements DB {

    protected final TypeMapper typeMapper;
    protected final DatabaseMetaData dbmd;
    private Function<String, String> identifierConverter = Functions.directFunction();
    private final Object stLock = new Object();
    private volatile SqlTypes sqlTypes;
    protected WrapperInterpreter wrapperInterpreter;

    public DefaultDB(DatabaseMetaData dbmd) throws SQLException {
        this.dbmd = dbmd;
        this.typeMapper = new DefaultTypeMapper(this);
    }

    public DefaultDB(DatabaseMetaData dbmd, TypeMapper typeMapper) throws SQLException {
        this.dbmd = dbmd;
        this.typeMapper = typeMapper;
    }

    @Override
    public String convertIdentifier(String ident) {
        return identifierConverter.apply(ident);
    }

    protected Function<String, String> getIdentifierConverter() {
        return identifierConverter;
    }

    protected void setIdentifierConverter(Function<String, String> identifierConverter) {
        this.identifierConverter = identifierConverter;
    }

    /**
     * Имя объекта
     * @title Получение имени базы данных
     * @return Имя базы данных
     */
    @Override
    public String getName() {
        try {
            return DBTools.escapeProductName(dbmd.getDatabaseProductName());
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Schema getDefaultSchema() {
        return new SchemaReal(this, null);
    }

    /**
     * @title Получение схемы по имени
     * @param name
     * @return Схема
     */
    @Override
    public Schema getSchema(String name) {
        if(name == null) {
            return new SchemaReal(this, null);
        }
        ResultSet rs = null;
        try {
            rs = dbmd.getSchemas(null, convertIdentifier(name));
            if(rs.next()) {
                return new SchemaReal(this, rs.getString("TABLE_SCHEM"));
            }
            return null;
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            DBUtils.close(rs);
        }
    }

    /**
     * @title Получение списка схем
     * @return Список схем
     */
    @Override
    public List<Schema> getSchemas() {
        ResultSet rs = null;
        try {
            rs = dbmd.getSchemas();
            List<Schema> list = new ArrayList<>();
            while(rs.next()) {
                list.add(new SchemaReal(this, rs.getString("TABLE_SCHEM")));
            }
            return Collections.unmodifiableList(list);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            DBUtils.close(rs);
        }
    }

    /**
     * @title Закрытие базы данных и освобождение всех системных ресурсов, ассоциированных с ней
     */
    @Override
    public void close() {
        //TODO clean caches;
    }

    /**
     * @see java.sql.Connection#getMetaData()
     * @title Получение метаданных базы данных
     * @return Метаданные базы данных
     */
    @Override
    public DatabaseMetaData getDatabaseMetaData() {
        return dbmd;
    }

    /**
     * @title Получение маппера типов
     * @return Маппер типов
     */
    @Override
    public TypeMapper getTypeMapper() {
        return typeMapper;
    }

    /**
     * Можно перегрузить сей метод для произвольного механизма инициализации типов
     * @title Создание нового sql-типа
     * @return SQL-тип
     */
    protected SqlTypes createSqlTypes() {
        ResultSet rs = null;
        List<SqlType> types = new ArrayList<>();
        try {
            rs = dbmd.getTypeInfo();
            while(rs.next()) {
                //маппинг вручную, так как пока не загружены типы автоматический маппинг недоступен
                SqlTypeReal type = new SqlTypeReal();
                //String => Type name
                type.setTypeName(rs.getString("TYPE_NAME"));
                //int => SQL data type from java.sql.Types
                type.setSQLType(rs.getInt("DATA_TYPE"));
                // int => maximum precision
                type.setSize(rs.getInt("PRECISION"));
                // String => prefix used to quote a literal (may be null)
                type.setLiteralPrefix(rs.getString("LITERAL_PREFIX"));
                //String => suffix used to quote a literal (may be null)
                type.setLiteralSuffix(rs.getString("LITERAL_SUFFIX"));
                //String => parameters used in creating the type (may be null)
                type.setCreateParams(rs.getString("CREATE_PARAMS"));
                // short => can you use NULL for this type.
                short nullableShort = rs.getShort("NULLABLE");
                Boolean nullable = null;
                switch(nullableShort) {
                    //typeNoNulls - does not allow NULL values
                    case DatabaseMetaData.typeNoNulls:
                        nullable = false;
                        break;
                    //typeNullable - allows NULL values
                    case DatabaseMetaData.typeNullable:
                        nullable = true;
                        break;
                    //default - typeNullableUnknown - nullability unknown
                }
                type.setNullable(nullable);
                //boolean=> is it case sensitive.
                type.setCaseSensitive(rs.getBoolean("CASE_SENSITIVE"));
                // boolean => is it unsigned.
                type.setUnsigned(rs.getBoolean("UNSIGNED_ATTRIBUTE"));
                //boolean => can it be a money value.
                type.setFixedPrecisionScale(rs.getBoolean("FIXED_PREC_SCALE"));
                //boolean => can it be used for an auto-increment value.
                type.setAutoIncrement(rs.getBoolean("AUTO_INCREMENT"));
                //String => localized version of type name (may be null)
                type.setLocalTypeName(rs.getString("LOCAL_TYPE_NAME"));
                //short => minimum scale supported
                type.setMinimumScale(rs.getInt("MINIMUM_SCALE"));
                //short => maximum scale supported
                type.setMaximumScale(rs.getInt("MAXIMUM_SCALE"));
                // int => usually 2 or 10
                type.setNumberPrecisionRadix(rs.getInt("NUM_PREC_RADIX"));
                types.add(type);
            }
        } catch(SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtils.close(rs);
        }
        return new DefaultSqlTypes(types);
    }

    /**
     * @title Получение типов СУБД
     * @return Типы СУБД
     */
    @Override
    public final SqlTypes getTypes() {
        if(sqlTypes == null) {
            synchronized(stLock) {
                if(sqlTypes == null) {
                    sqlTypes = createSqlTypes();
                }
            }
        }
        return sqlTypes;
    }

    @Override
    public WrapperInterpreter getWrapperInterpreter() {
        return this.wrapperInterpreter;
    }

}
