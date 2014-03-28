package ru.bio4j.service.sql.query.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.collections.Parameter;
import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.QueryContext;
import ru.bio4j.service.sql.db.DB;
import ru.bio4j.service.sql.db.Procedure;
import ru.bio4j.service.sql.query.parser.*;
import ru.bio4j.service.sql.result.handlers.ResultHandler;
import ru.bio4j.service.sql.types.MetaTypeResolver;
import ru.bio4j.service.sql.types.SqlTypes;
import ru.bio4j.service.sql.types.TypeHandler;
import ru.bio4j.service.sql.types.TypeMapper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.bio4j.service.sql.db.Parameter.ParameterType;


/**
 * Обработчик вызова процедур
 *
 * @title Обработчик вызова процедур
 */
public class CallProcedure implements QueryHandler<Object> {

    private static final Logger LOG = LoggerFactory.getLogger(CallProcedure.class);

    /**
     * Паттерн разбора вызова процедуры,
     * 1 - группа - имя процедуры
     * 2 - группа - аргументы процедуры,
     * TODO требующие дальнейшего парсинга (пока не реализованно)
     */
    public static final Pattern CALL_PATTERN = Pattern.compile("\\{(?:\\s*#\\{[^}]+\\}\\s*=\\s*)?\\s*call\\s+([\\w\\#$.]+)\\s*(\\([^)]*\\))?\\s*\\}",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
    private final QueryParser parser = new QueryParser();

    /**
     * Обрабатывает запрос
     *
     * @param sql
     * @return
     * @title Обработка вызова процедуры
     */
    @Override
    public <T> T handle(Query sql, QueryContext context, ResultReceiver<Object, T> handler) throws Exception {
        //получаем имя процедуры
        String s = sql.getSql();
        Matcher m = CALL_PATTERN.matcher(s);
        if (!m.matches()) {
            throw new SQLException("Can not parse sql: " + s);
        }
        String name = m.group(1).toUpperCase();//имя процедуры
        String argList = m.group(2);//список указанных аругментов
        CallableStatement statement = null;
        try {
            ParsedQuery call;
            if (argList == null || argList.isEmpty()) {
                call = buildCall(context, name, sql);
            } else {
                call = parser.parse(sql, new StatementFunction(sql));
            }
            LOG.debug("ParsedQuery = {}", call);
            //выполняем
            Connection con = context.getConnection();
            statement = con.prepareCall(call.getQuery());
            LOG.debug("SQL: {}", call.getQuery());
            List<ParsedParameter> outParams = new ArrayList<>();
            List<ParsedParameter> params = call.getParameters();
            int size = params.size();
            //конверсия типов
            DB db = context.getDB();
            TypeMapper typeMapper = db.getTypeMapper();
            SqlTypes sqlTypes = db.getTypes();
            MetaTypeResolver mtr = context.get(MetaTypeResolver.class);
            //регистрируем входные параметры
            for (int i = 0; i < size; ++i) {
                ParsedParameter p = params.get(i);
                Parameter param = p.getParameter();
                LOG.debug("PARAM [" + (p.isInput() ? "IN" : "") + (p.isOutput() ? "OUT" : "") + "] PARAM " +
                        p.getName() + " (" + p.getPosition() + ")",
                        param == null ? null : param.getValue() + " of Type " +
                                param == null ? null : param.getType());
                //регистрируем выходной параметр
                if (p.isOutput()) {
                    Integer sqlType = p.getSqlType();
                    if (sqlType == null) {
                        String sqlTypeName = p.getSqlTypeName();
                        sqlType = sqlTypes.toInt(sqlTypeName);
                    }
                    if (sqlType == null) {
                        throw new NullPointerException("SqlType of " + p + " is null.");
                    }
                    statement.registerOutParameter(p.getPosition(), sqlType);
                    outParams.add(p);
                }
                //TODO дать возможность работы с  inout параметрами
                if (p.isInput() && !p.isOutput()) {
                    try {
                        Class<Object> type = null;
                        String sqlType = null;
                        String metaTypeName = p.getMetaType();
                        Parameter parameter = p.getParameter();
                        Object value = parameter.getValue();
                        if (metaTypeName != null && mtr != null) {
                            type = (Class<Object>) mtr.toJavaType(metaTypeName);
                            sqlType = mtr.toSqlType(metaTypeName);
                        } else if (value != null) {
                            type = (Class<Object>) value.getClass();
                            sqlType = p.getSqlTypeName();
                            if (sqlType == null) {
                                sqlType = typeMapper.getSqlTypeForClass(type);
                            }
                        }
                        TypeHandler<Object> typeHandler = typeMapper.findHandler(TypeMapper.Mode.WRITE, sqlType, type, metaTypeName);
                        typeHandler.write(statement, value, p.getPosition(), type, sqlType);
                    } catch (Exception ex) {
                        throw new SQLException("Can not set param '" + p.getName() + "'.", ex);
                    }
                }
            }
            statement.execute();
            if (!outParams.isEmpty() && handler != null) {
                if (handler instanceof ResultHandler) {//для обратной совместимости
                    T t = null;
                    if (outParams.size() > 1) {
                        throw new UnsupportedOperationException(
                                "Multiple return params with ResultHandler not supported yet. Use ResultReceiver.");
                    }
                    ParsedParameter p = outParams.get(0);
                    Object result = statement.getObject(p.getPosition());
                    if (result instanceof ResultSet) {
                        ResultSet rs = (ResultSet) result;
                        t = handler.handle(rs, sql, context);
                    } else {
                        LOG.warn("Handling of non ResultSet values is not supported, at ' {} {}", call.getQuery(), "'");
                    }
                    return t;
                } else {
                    //Вызываем обычный ResultReceiver
                    return handler.handle(statement, sql, context);
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return null;
    }

    /*
    * получение из базы сигнатуры
    */
    @SuppressWarnings("unchecked")
    static ParsedQuery buildCall(QueryContext context, String procedureName, Query sql) throws SQLException {
        Map<String, Parameter> paramMap = sql.getParams();
        //получаем список параметров и строим вызов
        Procedure proc = context.getDB().getDefaultSchema().getProcedure(procedureName);
        //запрашиваем из базы список параметров процедуры и их типы
        List<ru.bio4j.service.sql.db.Parameter> args = proc.getColumns();
        //это будет код вызова процедуры
        StringBuilder sb = new StringBuilder("call ");
        sb.append(procedureName).append(" (");
        ParsedQueryImpl.Builder pqb = ParsedQueryImpl.builder();
        boolean hasReturned = false;
        int i = 0;
        boolean writeParam = false;
        for(ru.bio4j.service.sql.db.Parameter arg: args) {
            String parameterName = arg.getName().toUpperCase();
            ParsedParameterImpl.Builder ppb = ParsedParameterImpl.builder()
                    .position(i + 1)
                    .name(parameterName)
                    .sqlType(arg.getSQLType())
                    .sqlTypeName(arg.getTypeName());
            ParameterType type = arg.getParameterType();
            if(type == ParameterType.IN_OUT || type == ParameterType.OUT) {//находим выходной параметр
                ppb.output(true);
                if(arg.getPosition() == 0){
                    ppb.position(0).input(false);
                    hasReturned = true;
                } else {
                    writeParam = true;
                }
            } else if (type == ParameterType.IN) {
                ppb.input(true).output(false);
            }
            //параметры имеющие значение по умолчанию подставлять не надо если их нет

            Parameter parameter = paramMap.get(parameterName);
            if(parameter != null || !arg.isHasDefaultValue()){
                //определяем возвращаемые параметры
                ppb.parameter(parameter);
                pqb.addParameter(ppb.build());
                writeParam = true;
            }
            if(writeParam) {
                sb.append(" ?, ");
                ++i;
            }
            writeParam = false;
        }
        int len = sb.length();
        sb.replace(len - 2, len, ")}");// `2` - потому что надо удалить последний пробел и запятую
        if(hasReturned) {
            sb.insert(0, "{ ? =");
        } else {
            sb.insert(0, "{");
        }
        pqb.query(sb.toString());
        return pqb.build();
    }
}