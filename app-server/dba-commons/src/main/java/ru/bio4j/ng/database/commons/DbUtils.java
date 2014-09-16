package ru.bio4j.ng.database.commons;

import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.RDBMSUtils;
import ru.bio4j.ng.database.api.SqlTypeConverter;
import ru.bio4j.ng.database.api.StoredProgMetadata;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Утилиты для работы с метаданными СУБД
 */
public class DbUtils {

    private SqlTypeConverter converter;
    private RDBMSUtils rdbmsUtils;

    private DbUtils() {
    }

    private static final DbUtils instance = new DbUtils();
    private static final Map<Integer, String> jdbcMappings = getAllJdbcTypeNames();

    public static DbUtils getInstance() {return instance;}

    private static final String INIT_ERRORS_TEMPL = "Instance of \"%s\" is not initiated!";
    public void init(SqlTypeConverter converter, RDBMSUtils rdbmsUtils) {
        this.converter = converter;
        this.rdbmsUtils = rdbmsUtils;
    }

    private static Map<Integer, String> getAllJdbcTypeNames() {

        Map<Integer, String> result = new HashMap<Integer, String>();

        for (Field field : Types.class.getFields()) {
            try {
                result.put((Integer) field.get(null), field.getName());
            } catch (IllegalAccessException ex) {}
        }

        return result;
    }

    public String getSqlTypeName(int type) {
        return jdbcMappings.get(type);
    }

    public int paramSqlType(Param param) {
        if(converter == null)
            throw new IllegalArgumentException(String.format(INIT_ERRORS_TEMPL, SqlTypeConverter.class.getSimpleName()));
        int stringSize = 0;
        if(param.getType() == MetaType.STRING){
            if(((param.getDirection() == Param.Direction.INOUT) || (param.getDirection() == Param.Direction.IN)) && (stringSize == 0))
                stringSize = Strings.isNullOrEmpty(Paramus.paramValueAsString(param)) ? 0 : Paramus.paramValueAsString(param).length();
        }
        boolean isCallable = (param.getDirection() == Param.Direction.INOUT) || (param.getDirection() == Param.Direction.OUT);
        return converter.read(param.getType(), stringSize, isCallable);
    }

    public StoredProgMetadata detectStoredProcParamsAuto(String storedProcName, Connection conn) throws SQLException {
        if(rdbmsUtils == null)
            throw new IllegalArgumentException(String.format(INIT_ERRORS_TEMPL, RDBMSUtils.class.getSimpleName()));
        return rdbmsUtils.detectStoredProcParamsAuto(storedProcName, conn);
    }

}
