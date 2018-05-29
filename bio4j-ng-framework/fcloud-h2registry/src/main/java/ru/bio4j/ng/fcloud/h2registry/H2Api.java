package ru.bio4j.ng.fcloud.h2registry;

import org.h2.jdbcx.JdbcDataSource;
import ru.bio4j.ng.database.api.SQLNamedParametersStatement;
import ru.bio4j.ng.database.api.SQLParamGetter;
import ru.bio4j.ng.database.api.SQLParamSetter;
import ru.bio4j.ng.database.commons.DbCallableParamGetter;
import ru.bio4j.ng.database.commons.DbCallableParamSetter;
import ru.bio4j.ng.database.commons.DbNamedParametersStatement;
import ru.bio4j.ng.database.commons.DbSelectableParamSetter;
import ru.bio4j.ng.model.transport.Param;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public class H2Api {
    public static Connection getConnection(final String url, final String usrName, final String passwd) throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(url); //("jdbc:h2:˜/test");
        ds.setUser(usrName); //("sa");
        ds.setPassword(passwd); //("sa");
        return ds.getConnection();
    }

    public static ResultSet openSql(final Connection conn, final String sql, final List<Param> params) throws Exception {
        SQLNamedParametersStatement stmnt = DbNamedParametersStatement.prepareStatement(conn, sql);
        SQLParamSetter paramSetter = new DbSelectableParamSetter();
        paramSetter.setParamsToStatement(stmnt, params);
        ResultSet rs = stmnt.executeQuery();
        return rs;
    }

    public static void execSql(final Connection conn, final String sql, final List<Param> params) throws Exception {
        SQLNamedParametersStatement stmnt = DbNamedParametersStatement.prepareCall(conn, sql);
        SQLParamSetter paramSetter = new DbCallableParamSetter();
        SQLParamGetter paramGetter = new DbCallableParamGetter();
        paramSetter.setParamsToStatement(stmnt, params);
        stmnt.execute();
        paramGetter.getParamsFromStatement(stmnt, params);
    }

}
