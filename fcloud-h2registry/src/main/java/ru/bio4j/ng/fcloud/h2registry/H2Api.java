package ru.bio4j.ng.fcloud.h2registry;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.SQLNamedParametersStatement;
import ru.bio4j.ng.database.api.SQLParamGetter;
import ru.bio4j.ng.database.api.SQLParamSetter;
import ru.bio4j.ng.database.commons.*;
import ru.bio4j.ng.model.transport.Param;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class H2Api {
    private static final Logger LOG = LoggerFactory.getLogger(H2Api.class);

    private static H2Api instance = new H2Api();
    private H2Api(){
        DbUtils.getInstance().init(new H2SQLTypeConverterImpl(), new H2SQLUtilsImpl());
    }

    private String actualTcpPort = null;
    private String tcpPort = null;
    private String databasePath = null;
    private String usrName = null;
    private String passwd = null;
    private Server server;
    private String actualUrl = null;

    public synchronized void startServer(String port, String databasePath, String usrName, String passwd) throws SQLException {
        if(server == null) {
            if (!Strings.isNullOrEmpty(port)) {
                tcpPort = port;
                final String[] args = new String[] {
                        "-tcp",
                        "-tcpPort", tcpPort,
                        "-tcpAllowOthers",
                };
                server = Server.createTcpServer(args).start();
                actualTcpPort = "" + server.getPort();
                this.databasePath = databasePath;
                this.usrName = usrName;
                this.passwd = passwd;
            }
        }
    }

    public String getActualTcpPort() {
        return actualTcpPort;
    }

    public String getActualUrl() {
        return actualUrl;
    }

    public synchronized void shutdownServer() throws SQLException {
        if(server != null)
            Server.shutdownTcpServer(String.format("tcp://localhost:%s", actualTcpPort), "", true, true);
    }

    public Connection getLocalConnection() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        actualUrl = String.format("jdbc:h2:tcp://localhost:%s/%s", tcpPort, databasePath);
        ds.setURL(actualUrl); //("jdbc:h2:˜/test");
        ds.setUser(usrName); //("sa");
        ds.setPassword(passwd); //("sa");
        return ds.getConnection();
    }

    private static String getSQL2Execute(String sql, List<Param> params) {
        StringBuilder sb = new StringBuilder();
        if(params != null) {
            sb.append("{DbCommand.Params(before exec): ");
            sb.append(Paramus.paramsAsString(params));
            sb.append("}");
        }
        return String.format("preparedSQL: %s;\n - %s", sql, sb.toString());
    }

    public ResultSet openSql(final Connection conn, final String sql, final List<Param> params) throws Exception {
        if(LOG.isDebugEnabled())LOG.debug("Try open: {}", getSQL2Execute(sql, params));
        SQLNamedParametersStatement stmnt = DbNamedParametersStatement.prepareStatement(conn, sql);
        SQLParamSetter paramSetter = new DbSelectableParamSetter();
        paramSetter.setParamsToStatement(stmnt, params);
        ResultSet rs = stmnt.executeQuery();
        return rs;
    }

    public boolean execSql(final Connection conn, final String sql, final List<Param> params) throws Exception {
        if(LOG.isDebugEnabled())LOG.debug("Try exec: {}", getSQL2Execute(sql, params));
        SQLNamedParametersStatement stmnt = DbNamedParametersStatement.prepareCall(conn, sql);
        SQLParamSetter paramSetter = new DbCallableParamSetter();
        SQLParamGetter paramGetter = new DbCallableParamGetter();
        paramSetter.setParamsToStatement(stmnt, params);
        boolean rslt = stmnt.execute();
        paramGetter.getParamsFromStatement(stmnt, params);
        return rslt;
    }

    public static H2Api getInstance() {
        return instance;
    }
}
