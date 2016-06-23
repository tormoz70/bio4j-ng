package ru.bio4j.ng.database.pgsql;

import ru.bio4j.ng.commons.converter.ConvertValueException;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import ru.bio4j.ng.database.commons.DbContextAbstract;
import ru.bio4j.ng.database.commons.DbUtils;
import ru.bio4j.ng.database.commons.SQLExceptionExt;
import ru.bio4j.ng.database.pgsql.impl.PgSQLContext;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLFactoryTest {
    private static final Logger LOG = LoggerFactory.getLogger(SQLFactoryTest.class);
//    private static final String testDBDriverName = "oracle.jdbc.driver.OracleDriver";
//    private static final String testDBUrl = "jdbc:oracle:thin:@stat4-ora-dev:1521:MICEXDB";
    private static final String testDBDriverName = "org.postgresql.Driver";
    private static final String testDBUrl = "jdbc:postgresql://192.168.50.47:5432/postgres";

//    private static final String testDBUrl = "jdbc:oracle:thin:@cmon-ora-dev:1521:MICEXDB";
    //private static final String testDBUrl = "jdbc:oracle:oci:@GIVCDB_EKBS03";
    //private static final String testDBUrl = "jdbc:oracle:thin:@https://databasetrial0901-rugivcmkrftrial07058.db.em1.oraclecloudapps.com/apex:1521:databasetrial0901";
    private static final String testDBUsr = "scott";
    private static final String testDBPwd = "tiger";

    private static SQLContext context;

    @BeforeTest
    public static void setUpClass() throws Exception {
        context = DbContextAbstract.create(
                SQLConnectionPoolConfig.builder()
                        .poolName("TEST-CONN-POOL")
                        .dbDriverName(testDBDriverName)
                        .dbConnectionUrl(testDBUrl)
                        .dbConnectionUsr(testDBUsr)
                        .dbConnectionPwd(testDBPwd)
                        .build(),
                PgSQLContext.class);
        //if(true) return;
        try {
            context.execBatch(new SQLActionScalar<Object>() {
                @Override
                public Object exec(SQLContext context, Connection conn) throws Exception {
                    String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_test_table.sql"));
                    Statement cs = conn.createStatement();
                    cs.execute(sql);
                    sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_simple.sql"));
                    cs.execute(sql);
                    sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_error.sql"));
                    cs.execute(sql);
                    sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_ret_cursor.sql"));
                    cs.execute(sql);
                    sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_with_inout.sql"));
                    cs.execute(sql);
                    return null;
                }
            }, null);
        } catch (SQLException ex) {
            LOG.error("Error!", ex);
        }
    }

    @AfterTest
    public static void finClass() throws Exception {
        //if(true) return;
        try {
            context.execBatch(new SQLActionScalar<Object>() {
                @Override
                public Object exec(SQLContext context, Connection conn) throws Exception {
                    Statement cs = conn.createStatement();
                    cs.execute("drop function test_stored_prop(varchar, out integer)");
                    cs.execute("drop function test_stored_error(varchar, out integer)");
                    cs.execute("drop function test_stored_cursor(varchar, out refcursor)");
                    cs.execute("drop function test_stored_inout(inout integer, varchar, integer, numeric)");
                    cs.execute("drop table test_tbl");
                    return null;
                }
            }, null);
        } catch (SQLException ex) {
            LOG.error("Error!", ex);
        }
    }

    @Test
    public void testCreateSQLConnectionPool() throws Exception {
//        LOG.debug(Utl.buildBeanStateInfo(context.getStat(), null, null));
        context.execBatch(new SQLActionScalar<Object>() {
            @Override
            public Object exec(SQLContext context, Connection conn) throws Exception {
                Assert.assertNotNull(conn);
                return null;
            }
        }, null);

    }

    @Test(enabled = true)
    public void testSQLCommandOpenCursor() {
        try {
            Double dummysum = context.execBatch(new SQLActionScalar<Double>() {
                @Override
                public Double exec(SQLContext context, Connection conn) throws Exception {
                    Double dummysum = 0.0;
                    String sql = "select user as curuser, :dummy as dm, :dummy1 as dm1";
                    List<Param> prms = Paramus.set(new ArrayList<Param>()).add("dummy", 101).pop();
                    try(SQLCursor c = context.createCursor()
                            .init(conn, sql, prms).open();){
                        while(c.reader().next()){
                            dummysum += c.reader().getValue("DM", Double.class);
                        }
                    }
                    return dummysum;
                }
            }, null);
            LOG.debug("dummysum: " + dummysum);
            Assert.assertEquals(dummysum, 101.0);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

    @Test(enabled = true)
    public void testSQLCommandOpenCursor111() {
        try {
            Double dummysum = context.execBatch(new SQLActionScalar<Double>() {
                @Override
                public Double exec(SQLContext context, Connection conn) throws Exception {
                    Double dummysum = 0.0;
                    String sql = "select * from test_tbl where fld2 = :fld2";
                    List<Param> prms = Paramus.set(new ArrayList<Param>()).add(
                            Param.builder()
                                    .name("fld2")
                                    .type(MetaType.INTEGER)
                                    .value(null)
                                    .build()
                    ).pop();
                    try(SQLCursor c = context.createCursor()
                            .init(conn, sql, prms).open();){
                        while(c.reader().next()){
                            dummysum += c.reader().getValue("fld1", String.class).length();
                        }
                    }
                    return dummysum;
                }
            }, null);
            LOG.debug("dummysum: " + dummysum);
            Assert.assertEquals(dummysum, 0.0);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

    @Test(enabled = false)
    public void testSQLCommandOpenCursor1() {
        try {
            Double dummysum = 0.0;
            byte[] schema = context.execBatch(new SQLActionScalar<byte[]>() {
                @Override
                public byte[] exec(SQLContext context, Connection conn) throws Exception {
                    byte[] schema = null;
                    String sql = "select * from table(givcapi.upld.get_schemas)";
                    try(SQLCursor c = context.createCursor()
                            .init(conn, sql, null).open();){
                        while(c.reader().next()){
                            if(schema == null){
                                schema = c.reader().getValue("XSD_BODY", byte[].class);
                            }

                        }
                    }
                    return schema;
                }
            }, null);
            Assert.assertTrue(schema.length > 0);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

    @Test(enabled = true)
    public void testSQLCommandExecSQL() throws Exception {
        try {
            int leng = context.execBatch(new SQLActionScalar<Integer>() {
                @Override
                public Integer exec(SQLContext context, Connection conn) throws Exception {
                    int leng = 0;
                    LOG.debug("conn: " + conn);

                    SQLStoredProc cmd = context.createStoredProc();
                    String storedProgName = "test_stored_prop";
                    try(Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                        paramus.add("p_param1", "FTW")
                              .add(Param.builder()
                                      .name("p_param2")
                                      .type(MetaType.INTEGER)
                                      .direction(Param.Direction.OUT)
                                      .build());
                        cmd.init(conn, storedProgName, paramus.get());
                    }
                    cmd.execSQL();
                    try(Paramus paramus = Paramus.set(cmd.getParams())) {
                        leng = Utl.nvl(paramus.getParamValue("p_param2", Integer.class), 0);
                    }
                    conn.rollback();
                    return leng;
                }
            }, null);
            LOG.debug("leng: " + leng);
            Assert.assertEquals(leng, 3);
        } catch (SQLException ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

    @Test(enabled = true)
    public void testSQLCommandExecINOUTSQL() throws Exception {
        try {
            int leng = context.execBatch(new SQLActionScalar<Integer>() {
                @Override
                public Integer exec(SQLContext context, Connection conn) throws Exception {
                    int leng = 0;
                    LOG.debug("conn: " + conn);

                    SQLStoredProc cmd = context.createStoredProc();
                    try(Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                        paramus.add(Param.builder().name("p_param1").type(MetaType.INTEGER).value(null).build())
                                .add("p_param2", "QWE")
                                .add(Param.builder().name("p_param3").type(MetaType.BOOLEAN).value(true).build())
                                .add(Param.builder().name("p_param4").type(MetaType.DECIMAL).value("").build());
                        cmd.init(conn, "test_stored_inout", paramus.get());
                    }
                    cmd.execSQL();
                    try(Paramus paramus = Paramus.set(cmd.getParams())) {
                        leng = Utl.nvl(paramus.getParamValue("p_param1", Integer.class), 0);
                    }
                    conn.rollback();
                    return leng;
                }
            }, null);
            LOG.debug("leng: " + leng);
            Assert.assertEquals(leng, 3);
        } catch (SQLException ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

    @Test(enabled = true)
    public void testSQLCommandExecExtParam() throws Exception {
        try {
            int leng = context.execBatch(new SQLActionScalar<Integer>() {
                @Override
                public Integer exec(SQLContext context, Connection conn) throws Exception {
                    int leng = 0;
                    LOG.debug("conn: " + conn);

                    SQLStoredProc cmd = context.createStoredProc();
                    String storedProgName = "test_stored_prop";
                    try(Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                        paramus.add("p_param1", "FTW")
                                .add(Param.builder()
                                        .name("p_param2")
                                        .type(MetaType.INTEGER)
                                        .direction(Param.Direction.OUT)
                                        .build())
                                .add("p_param3", "ext");
                        cmd.init(conn, storedProgName, paramus.get());
                    }
                    cmd.execSQL();
                    try(Paramus paramus = Paramus.set(cmd.getParams())) {
                        leng = Utl.nvl(paramus.getParamValue("p_param2", Integer.class), 0);
                    }
                    conn.rollback();
                    return leng;
                }
            }, null);
            LOG.debug("leng: " + leng);
            Assert.assertEquals(leng, 3);
        } catch (SQLException ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

    @Test(enabled = true)
    public void testStoredprocMetadata() throws Exception {

        try {
            context.execBatch(new SQLAction<Object, Object>() {
                @Override
                public Object exec(SQLContext context, Connection conn, Object param) throws Exception {
                    StoredProgMetadata sp = DbUtils.getInstance().detectStoredProcParamsAuto("test_stored_error", conn, null);
                    return null;
                }
            }, "AnContext", null);
        } catch (SQLException ex) {
            LOG.error("Error!", ex);
            Assert.assertEquals(ex.getErrorCode(), 20000);
        }

    }

    @Test(enabled = true)
    public void testSQLCommandExecError() throws Exception {
        try {
            context.execBatch(new SQLAction<Object, Object>() {
                @Override
                public Object exec(SQLContext context, Connection conn, Object param) throws Exception {
                    LOG.debug("conn: " + conn + "; param: " + param);

                    SQLStoredProc cmd = context.createStoredProc();
                    String storedProgName = "test_stored_error";
                    try(Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                        paramus.add("p_param1", "FTW")
                                .add(Param.builder()
                                        .name("p_param2")
                                        .type(MetaType.INTEGER)
                                        .direction(Param.Direction.OUT)
                                        .build());
                        cmd.init(conn, storedProgName, paramus.get());
                    }
                    cmd.execSQL();
                    return null;
                }
            }, "AnContext", null);
        } catch (SQLException ex) {
            LOG.error("Error!", ex);
            Assert.assertEquals(ex.getCause().getMessage(), "ERROR: FTW");
        }
    }
    
    @Test(enabled = true)
    public void testSQLCommandExecSQLAutoCommit() throws Exception {
        try {
            int leng = context.execBatch(new SQLActionScalar<Integer>() {
                @Override
                public Integer exec(SQLContext context, Connection conn) throws Exception {
                    int leng = 0;
                    LOG.debug("conn: " + conn);

                    SQLStoredProc cmd = context.createStoredProc();
                    String storedProgName = "test_stored_prop";
                    try(Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                        paramus.add("p_param1", "FTW")
                            .add(Param.builder()
                                    .name("p_param2")
                                    .type(MetaType.INTEGER)
                                    .direction(Param.Direction.OUT)
                                    .build());
                        cmd.init(conn, storedProgName, paramus.get()).execSQL();
                    }
                    try(Paramus paramus = Paramus.set(cmd.getParams())) {
                        leng = Utl.nvl(paramus.getParamValue("p_param2", Integer.class), 0);
                    }
                    return leng;
                }
            }, null);
            LOG.debug("leng: " + leng);
            Assert.assertEquals(leng, 3);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

    private static <T> T getParamValue(List<Param> params, Class<T> type, String paramName) throws SQLException {
        try {
            return Paramus.set(params).getValueByName(type, paramName, true);
        } catch (ConvertValueException ex) {
            throw new SQLException(ex);
        } finally {
            Paramus.instance().pop();
        }
    }

    @Test(enabled = false)
    public void testSQLCommandStoredProc() throws Exception {
        try {
            int role = -1;
            int org_id = -1;
            Paramus paramus = Paramus.set(new ArrayList<Param>());
            paramus.add("p_user_name", "coordinator")
                    .add("p_password", "siolopon")
                    .add(Param.builder()./*owner(paramus.get()).*/name("v_role_id").type(MetaType.INTEGER).direction(Param.Direction.OUT).build())
                    .add(Param.builder()./*owner(paramus.get()).*/name("v_org_id").type(MetaType.INTEGER).direction(Param.Direction.OUT).build());
            List<Param> params = paramus.pop();
            context.execBatch(new SQLAction<List<Param>, Object>() {
                @Override
                public Object exec(SQLContext context, Connection conn, List<Param> param) throws Exception {
                    SQLStoredProc prc = context.createStoredProc();
                    prc.init(conn, "gacc.check_login", param).execSQL();
                    return null;
                }
            }, params, null);
            role = getParamValue(params, int.class, "v_role_id");
            org_id = getParamValue(params, int.class, "v_org_id");
            LOG.debug(String.format("Login: OK; role: %d; org_id: %d", role, org_id));
            Assert.assertEquals(role, 6);
        } catch (SQLException ex) {
            LOG.error("Error!!!", ex);
        }
    }

    @Test(enabled = true)
    public void testSQLCommandStoredProcRetCursor() throws Exception {
        try {
            int c = context.execBatch(new SQLActionScalar<Integer>() {
                @Override
                public Integer exec(SQLContext context, Connection conn) throws Exception {
                    ResultSet resultSet = null;
                    LOG.debug("conn: " + conn);

                    SQLStoredProc cmd = context.createStoredProc();
                    String storedProgName = "test_stored_cursor";
                    try(Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                        paramus.add("p_param1", "FTW")
                                .add(Param.builder()
                                        .name("p_param2")
                                        .type(MetaType.CURSOR)
                                        .direction(Param.Direction.OUT)
                                        .build());
                        cmd.init(conn, storedProgName, paramus.get()).execSQL();
                    }
                    try(Paramus paramus = Paramus.set(cmd.getParams())) {
                        resultSet = paramus.getParamValue("p_param2", ResultSet.class);
                        if(resultSet.next()) {
                            String userName = resultSet.getString("ROLNAME");
                            LOG.debug("userName: " + userName);
                            Assert.assertEquals(userName.toUpperCase(), "POSTGRES");
                        }
                    }
                    return 0;
                }
            }, null);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

//    @Test(enabled = true)
//    public void testCallStoredProc() {
//        try {
//            try (Connection conn = param.getConnection()) {
//                CallableStatement cs = conn.prepareCall( "{call test_stored_prop(:p_param1, :p_param2)}");
//                cs.setString("p_param1", "FTW");
//                cs.registerOutParameter("p_param2", Types.INTEGER);
//                cs.execute();
//                int leng = cs.getInt("p_param2");
//                Assert.assertEquals(leng, 3);
//            }
//        } catch (SQLException ex) {
//            LOG.error("Error!", ex);
//        }
//    }

    @Test
    public void sqlExceptionExtTest() {
        List<Param> params = new ArrayList<>();
        params.add(Param.builder()
                .name("qwe")
                .value(123)
                .build()
        );
        StringBuilder sb = new StringBuilder();
        sb.append("{Command.Params(before exec): {\n");
        for (Param p : params)
            sb.append("\t"+p.toString()+",\n");
        sb.append("}}");

        SQLException e = new SQLException("QWE-TEST");
        SQLExceptionExt r = new SQLExceptionExt(String.format("%s:\n - sql: %s;\n - %s", "Error on execute command.", "select * from dual", sb.toString()), e);
        String msg = r.getMessage();
        LOG.debug(msg);
    }
}
