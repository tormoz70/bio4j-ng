package ru.bio4j.service.sql.query;

import ru.bio4j.func.UnsafeFunction;
import ru.bio4j.service.sql.QueryContext;
import ru.bio4j.service.sql.db.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Базовое задание для SqlContext
 * @title Базовое задание для SqlContext
 */
public class UpdateFunction implements UnsafeFunction<QueryContext, Integer, SQLException> {

    private final String sql;
    private final List<Object> parameters;

    public UpdateFunction(String sql,
                          List<Object> parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }

    public UpdateFunction(String sql) {
        this.sql = sql;
        this.parameters = null;
    }

    /**
     * @title Выполнение задания
     * @param key
     * @return Результат выполнения задания
     * @throws T
     */
    @Override
    public Integer apply(QueryContext qc) throws SQLException {
        Connection c = qc.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = c.prepareStatement(sql);
            if(parameters != null) {
                for(int i = parameters.size(); i > 0; --i) {
                    preparedStatement.setObject(i, parameters.get(i - 1));
                }
            }
            return preparedStatement.executeUpdate();
        } catch(SQLException e) {
            throw new SQLException("Can not execute update: " + sql, e);
        } finally {
            DBUtils.close(preparedStatement);
        }
    }

}