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
import ru.bio4j.ng.database.commons.DbContextFactory;
import ru.bio4j.ng.database.commons.DbUtils;
import ru.bio4j.ng.database.api.BioSQLException;
import ru.bio4j.ng.database.pgsql.impl.PgSQLContext;
import ru.bio4j.ng.database.pgsql.impl.PgSQLUtilsImpl;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Test(enabled = false)
public class SQLFactoryTest {
    private static final Logger LOG = LoggerFactory.getLogger(SQLFactoryTest.class);
//    private static final String testDBDriverName = "oracle.jdbc.driver.OracleDriver";
//    private static final String testDBUrl = "jdbc:oracle:thin:@stat4-ora-dev:1521:MICEXDB";
    private static final String testDBDriverName = "org.postgresql.Driver";
//    private static final String testDBUrl = "jdbc:postgresql://192.168.50.47:5432/postgres";
//    private static final String testDBUrl = "jdbc:postgresql://localhost:5435/postgres";
    private static final String testDBUrl = "jdbc:postgresql://10.10.0.221:5432/postgres";

//    private static final String testDBUrl = "jdbc:oracle:thin:@cmon-ora-dev:1521:MICEXDB";
    //private static final String testDBUrl = "jdbc:oracle:oci:@GIVCDB_EKBS03";
    //private static final String testDBUrl = "jdbc:oracle:thin:@https://databasetrial0901-rugivcmkrftrial07058.db.em1.oraclecloudapps.com/apex:1521:databasetrial0901";
//    private static final String testDBUsr = "master";
//    private static final String testDBPwd = "sysdba";
    private static final String testDBUsr = "postgres";
    private static final String testDBPwd = "root";

    private static SQLContext context;

    private static class Var {
        int iummy;
        double dummy;
        String summy;
        byte[] bummy;
    }

    @BeforeTest
    public static void setUpClass() throws Exception {
        context = DbContextFactory.createHikariCP(
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
            context.execBatch((context) -> {
                try(Statement cs = context.getCurrentConnection().createStatement()) {
                    String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_test_table.sql"));
                    cs.execute(sql);
                    sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_simple.sql"));
                    cs.execute(sql);
                    sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_error.sql"));
                    cs.execute(sql);
                    sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_ret_cursor.sql"));
                    cs.execute(sql);
                    sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_with_inout.sql"));
                    cs.execute(sql);
                }
            }, null);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
        }
    }

    @AfterTest
    public static void finClass() throws Exception {
        //if(true) return;
        try {
            context.execBatch((context) -> {
                Statement cs = context.getCurrentConnection().createStatement();
                cs.execute("drop function test_stored_prop(varchar, out integer)");
                cs.execute("drop function test_stored_error(varchar, out integer)");
                cs.execute("drop function test_stored_cursor(varchar, out refcursor)");
                cs.execute("drop function test_stored_inout(inout integer, varchar, integer, numeric)");
                cs.execute("drop table test_tbl");
                return null;
            }, null);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
        }
    }

//    @Test
    public void testCreateSQLConnectionPool() throws Exception {
//        LOG.debug(Utl.buildBeanStateInfo(context.getStat(), null, null));
        context.execBatch(new SQLActionScalar0<Object>() {
            @Override
            public Object exec(SQLContext context) {
                Assert.assertNotNull(context.getCurrentConnection());
                return null;
            }
        }, null);

    }

//    @Test(enabled = true)
    public void testSQLCommandOpenCursor() {
        try {
            Double dummysum = context.execBatch((context) -> {
                Var var = new Var();
                String sql = "select user as curuser, :dummy as dm, :dummy1 as dm1";
                List<Param> prms = Paramus.set(new ArrayList<Param>()).add("dummy", 101).pop();
                context.createCursor()
                        .init(context.getCurrentConnection(), sql, null)
                        .fetch(prms, context.getCurrentUser(), rs->{
                            var.dummy += rs.getValue("DM", Double.class);
                            return true;
                        });
                return var.dummy;
            }, null);
            if(LOG.isDebugEnabled())
                LOG.debug("dummysum: " + dummysum);
            Assert.assertEquals(dummysum, 101.0);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

//    @Test(enabled = true)
    public void testSQLCommandOpenCursor111() {
        try {
            Double dummysum = context.execBatch((context) -> {
                Var var = new Var();
                String sql = "select * from test_tbl where fld2 = :fld2";
                List<Param> prms = Paramus.set(new ArrayList<Param>()).add(
                        Param.builder()
                                .name("fld2")
                                .type(MetaType.INTEGER)
                                .value(null)
                                .build()
                ).pop();
                context.createCursor()
                        .init(context.getCurrentConnection(), sql, null)
                        .fetch(prms, context.getCurrentUser(), rs->{
                            var.dummy += rs.getValue("fld1", Double.class);
                            return true;
                        });
                return var.dummy;
            }, null);
            if(LOG.isDebugEnabled())
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
            byte[] schema = context.execBatch((context) -> {
                Var var = new Var();
                String sql = "select * from table(givcapi.upld.get_schemas)";
                context.createCursor()
                        .init(context.getCurrentConnection(), sql, null)
                        .fetch(context.getCurrentUser(), rs->{
                            if(var.bummy == null)
                                var.bummy = rs.getValue("XSD_BODY", byte[].class);
                            return true;
                        });
                return var.bummy;
            }, null);
            Assert.assertTrue(schema.length > 0);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

//    @Test(enabled = true)
    public void testSQLCommandExecSQL() throws Exception {
        try {
            int leng = context.execBatch((context) -> {
                int leng1 = 0;
                if(LOG.isDebugEnabled())
                    LOG.debug("conn: " + context.getCurrentConnection());

                SQLStoredProc cmd = context.createStoredProc();
                String storedProgName = "test_stored_prop";
                try(Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                    paramus.add("p_param1", "FTW")
                          .add(Param.builder()
                                  .name("p_param2")
                                  .type(MetaType.INTEGER)
                                  .direction(Param.Direction.OUT)
                                  .build());
                    cmd.init(context.getCurrentConnection(), storedProgName);
                    cmd.execSQL(paramus.get(), context.getCurrentUser());
                }
                try(Paramus paramus = Paramus.set(cmd.getParams())) {
                    leng1 = Utl.nvl(paramus.getParamValue("p_param2", Integer.class), 0);
                }
                context.getCurrentConnection().rollback();
                return leng1;
            }, null);
            if(LOG.isDebugEnabled())
                LOG.debug("leng: " + leng);
            Assert.assertEquals(leng, 3);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

//    @Test(enabled = true)
    public void testSQLCommandExecINOUTSQL() throws Exception {
        try {
            int leng = context.execBatch((context) -> {
                int leng1 = 0;
                if(LOG.isDebugEnabled())
                    LOG.debug("conn: " + context.getCurrentConnection());

                SQLStoredProc cmd = context.createStoredProc();
                List<Param> prms;
                try(Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                    paramus.add(Param.builder().name("p_param1").type(MetaType.INTEGER).value(null).build())
                            .add("p_param2", "QWE")
                            .add(Param.builder().name("p_param3").type(MetaType.INTEGER).value(1).build())
                            .add(Param.builder().name("p_param4").type(MetaType.DECIMAL).value(0).build());
                    prms = paramus.get();
                }
                cmd.init(context.getCurrentConnection(), "test_stored_inout");
                cmd.execSQL(prms, context.getCurrentUser());
                leng1 = cmd.getParamValue("p_param1", Integer.class, null);
                context.getCurrentConnection().rollback();
                return leng1;
            }, null);
            if(LOG.isDebugEnabled())
                LOG.debug("leng: " + leng);
            Assert.assertEquals(leng, 3);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

//    @Test(enabled = true)
    public void testSQLCommandExecINOUTSQL11() throws Exception {
        try {
            int leng = context.execBatch((context) -> {
                int leng1 = 0;
                if(LOG.isDebugEnabled())
                    LOG.debug("conn: " + context.getCurrentConnection());

                SQLStoredProc cmd = context.createStoredProc();
                List<Param> prms;
                try(Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                    paramus.add(Param.builder().name("p_param1").value(new BigDecimal(123)).build())
                            .add("p_param2", "QWE")
                            .add(Param.builder().name("p_param3").value(1D).build())
                            .add(Param.builder().name("p_param4").value(5L).build());
                    prms = paramus.get();
                }
                cmd.init(context.getCurrentConnection(), "test_stored_inout");
                cmd.execSQL(prms, context.getCurrentUser());
                leng1 = cmd.getParamValue("p_param1", Integer.class, null);
                context.getCurrentConnection().rollback();
                return leng1;
            }, null);
            if(LOG.isDebugEnabled())
                LOG.debug("leng: " + leng);
            Assert.assertEquals(leng, 3);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

//    @Test(enabled = true)
    public void testDetectParamsOfSP() throws Exception {
        try {
            long leng = context.execBatch((context) -> {
                long leng1 = 0;
                if(LOG.isDebugEnabled())
                    LOG.debug("conn: " + context.getCurrentConnection());

                PgSQLUtilsImpl utl = new PgSQLUtilsImpl();
                StoredProgMetadata md = utl.detectStoredProcParamsAuto("test_stored_inout", context.getCurrentConnection(), null);
                if(LOG.isDebugEnabled())
                    LOG.debug("md: " + md);
                leng1 = md.getParamDeclaration().size();
                return leng1;
            }, null);
            if(LOG.isDebugEnabled())
                LOG.debug("leng: " + leng);
            Assert.assertEquals(leng, 4);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

    private static class TestParamObj {
        public Long param1;
        public String param2;
        public Integer param3;
        public Double param4;
    }

//    @Test(enabled = true)
    public void testSQLCommandExecINOUTSQL1() throws Exception {
        try {
            long leng = context.execBatch((context) -> {
                long leng1 = 0;
                if(LOG.isDebugEnabled())
                    LOG.debug("conn: " + context.getCurrentConnection());

                SQLStoredProc cmd = context.createStoredProc();

                TestParamObj prms = new TestParamObj() {{
                    param1 = null;
                    param2 = "QWE";
                    param3 = 1;
                    param4 = null;
                }};

                cmd.init(context.getCurrentConnection(), "test_stored_inout");
                cmd.execSQL(prms, context.getCurrentUser());
                leng1 = cmd.getParamValue("p_param1", Long.class, null);
                context.getCurrentConnection().rollback();
                return leng1;
            }, null);
            if(LOG.isDebugEnabled())
                LOG.debug("leng: " + leng);
            Assert.assertEquals(leng, 3);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

//    @Test(enabled = true)
    public void testSQLCommandExecExtParam() throws Exception {
        try {
            int leng = context.execBatch((context) -> {
                int leng1 = 0;
                if(LOG.isDebugEnabled())
                    LOG.debug("conn: " + context.getCurrentConnection());

                SQLStoredProc cmd = context.createStoredProc();
                String storedProgName = "test_stored_prop";
                List<Param> prms = new ArrayList<>();
                try(Paramus paramus = Paramus.set(prms)) {
                    paramus.add("param1", "FTW")
                            .add(Param.builder()
                                    .name("param2")
                                    .type(MetaType.INTEGER)
                                    .direction(Param.Direction.OUT)
                                    .build())
                            .add("param3", "ext");
                }
                cmd.init(context.getCurrentConnection(), storedProgName);
                cmd.execSQL(prms, context.getCurrentUser());
                leng1 = Paramus.paramValue(cmd.getParams(), "param2", Integer.class, 0);
                context.getCurrentConnection().rollback();
                return leng1;
            }, null);
            if(LOG.isDebugEnabled())
                LOG.debug("leng: " + leng);
            Assert.assertEquals(leng, 3);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

//    @Test(enabled = true)
    public void testStoredprocMetadata() throws Exception {

        try {
            context.execBatch((SQLActionVoid1<String>) (context, param) -> {
                StoredProgMetadata sp = DbUtils.getInstance().detectStoredProcParamsAuto("test_stored_error", context.getCurrentConnection(), null);
            }, "AnContext", null);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
            Assert.assertEquals(ex.getErrorCode(), 20000);
        }

    }

//    @Test(enabled = true)
    public void testSQLCommandExecError() throws Exception {
        try {
            context.execBatch((SQLActionVoid1<String>) (context, param) -> {
                if(LOG.isDebugEnabled())
                    LOG.debug("conn: " + context.getCurrentConnection() + "; param: " + param);

                SQLStoredProc cmd = context.createStoredProc();
                String storedProgName = "test_stored_error";
                try(Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                    paramus.add("p_param1", "FTW")
                            .add(Param.builder()
                                    .name("p_param2")
                                    .type(MetaType.INTEGER)
                                    .direction(Param.Direction.OUT)
                                    .build());
                    cmd.init(context.getCurrentConnection(), storedProgName);
                    cmd.execSQL(paramus.get(), context.getCurrentUser());
                }
            }, "AnContext", null);
        } catch (BioSQLException ex) {
            LOG.error("Error!", ex);
            Boolean errMsgOk = ex.getCause().getMessage().indexOf(": FTW") >= 0;
            Assert.assertTrue(errMsgOk);
        }
    }
    
//    @Test(enabled = true)
    public void testSQLCommandExecSQLAutoCommit() throws Exception {
        try {
            int leng = context.execBatch((context) -> {
                int leng1 = 0;
                if(LOG.isDebugEnabled())
                    LOG.debug("conn: " + context.getCurrentConnection());

                SQLStoredProc cmd = context.createStoredProc();
                String storedProgName = "test_stored_prop";
                List<Param> prms = new ArrayList<>();
                try(Paramus paramus = Paramus.set(prms)) {
                    paramus.add("param1", "FTW")
                        .add(Param.builder()
                                .name("param2")
                                .type(MetaType.INTEGER)
                                .direction(Param.Direction.OUT)
                                .build());
                }
                cmd.init(context.getCurrentConnection(), storedProgName).execSQL(prms, context.getCurrentUser());
                try(Paramus paramus = Paramus.set(cmd.getParams())) {
                    leng1 = Utl.nvl(paramus.getParamValue("param2", Integer.class), 0);
                }
                return leng1;
            }, null);
            if(LOG.isDebugEnabled())
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
            context.execBatch((context, param) -> {
                SQLStoredProc prc = context.createStoredProc();
                prc.init(context.getCurrentConnection(), "gacc.check_login", param).execSQL(context.getCurrentUser());
                return null;
            }, params, null);
            role = getParamValue(params, int.class, "v_role_id");
            org_id = getParamValue(params, int.class, "v_org_id");
            if(LOG.isDebugEnabled())
                LOG.debug(String.format("Login: OK; role: %d; org_id: %d", role, org_id));
            Assert.assertEquals(role, 6);
        } catch (SQLException ex) {
            LOG.error("Error!!!", ex);
        }
    }

//    @Test(enabled = true)
    public void testSQLCommandStoredProcRetCursor() throws Exception {
        try {
            int c = context.execBatch((context) -> {
                ResultSet resultSet = null;
                if(LOG.isDebugEnabled())
                    LOG.debug("conn: " + context.getCurrentConnection());

                SQLStoredProc cmd = context.createStoredProc();
                String storedProgName = "test_stored_cursor";
                try(Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                    paramus.add("p_param1", "FTW")
                            .add(Param.builder()
                                    .name("p_param2")
                                    .type(MetaType.CURSOR)
                                    .direction(Param.Direction.OUT)
                                    .build());
                    cmd.init(context.getCurrentConnection(), storedProgName).execSQL(paramus.get(), context.getCurrentUser());
                }
                try(Paramus paramus = Paramus.set(cmd.getParams())) {
                    resultSet = paramus.getParamValue("p_param2", ResultSet.class);
                    if(resultSet.next()) {
                        String userName = resultSet.getString("ROLNAME");
                        if(LOG.isDebugEnabled())
                            LOG.debug("userName: " + userName);
                        Assert.assertTrue(Arrays.asList("PG_SIGNAL_BACKEND", "PG_MONITOR").contains(userName.toUpperCase()));
                    }
                }
                return 0;
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

//    @Test
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
        BioSQLException r = BioSQLException.create(String.format("%s:\n - sql: %s;\n - %s", "Error on execute command.", "select * from dual", sb.toString()), e);
        String msg = r.getMessage();
        if(LOG.isDebugEnabled())
            LOG.debug(msg);
    }

    @Test(enabled = false)
    public void testSQLCommandOpenCursorNewSbkItem() throws Exception {
        final String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("new-sbkitem.sql"));
        try {
            Integer dummy = context.execBatch((context) -> {
                context.createCursor()
                        .init(context.getCurrentConnection(), sql, null)
                        .fetch(context.getCurrentUser(), rs->{
                           return false;
                        });
                return 0;
            }, null);
            if(LOG.isDebugEnabled())
                LOG.debug("dummy: " + dummy);
            Assert.assertEquals(dummy, new Integer(0));
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

}
