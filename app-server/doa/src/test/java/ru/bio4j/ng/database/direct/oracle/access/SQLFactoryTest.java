package ru.bio4j.ng.database.direct.oracle.access;

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
import ru.bio4j.ng.database.doa.impl.OraContext;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLFactoryTest {
    private static final Logger LOG = LoggerFactory.getLogger(SQLFactoryTest.class);
    private static final String testDBUrl = "jdbc:oracle:thin:@192.168.50.32:1521:EKBDB";
    //private static final String testDBUrl = "jdbc:oracle:oci:@GIVCDB_EKBS03";
    //private static final String testDBUrl = "jdbc:oracle:thin:@https://databasetrial0901-rugivcmkrftrial07058.db.em1.oraclecloudapps.com/apex:1521:databasetrial0901";
    private static final String testDBUsr = "SCOTT";
    private static final String testDBPwd = "tiger";

    private static SQLContext context;

    @BeforeTest
    public static void setUpClass() throws Exception {
        context = OraContext.create("TEST-CONN-POOL",
                SQLConnectionPoolConfig.builder()
                        .dbConnectionUrl(testDBUrl)
                        .dbConnectionUsr(testDBUsr)
                        .dbConnectionPwd(testDBPwd)
                                //.currentSchema("GIVCAPI")
                        .build()
        );
        try {
            context.execBatch(new SQLActionScalar<Object>() {
                @Override
                public Object exec(SQLContext context, Connection conn) throws Exception {
                    String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_test_table.sql"));
                    CallableStatement cs = conn.prepareCall(sql);
                    cs.execute();
                    sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_simple.sql"));
                    cs = conn.prepareCall(sql);
                    cs.execute();
                    sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_error.sql"));
                    cs = conn.prepareCall(sql);
                    cs.execute();
                    sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_ret_cursor.sql"));
                    cs = conn.prepareCall(sql);
                    cs.execute();
                    return null;
                }
            });
        } catch (SQLException ex) {
            LOG.error("Error!", ex);
        }
    }

    @AfterTest
    public static void finClass() throws Exception {
        if(true) return;
        try {
            context.execBatch(new SQLActionScalar<Object>() {
                @Override
                public Object exec(SQLContext context, Connection conn) throws Exception {
                    CallableStatement cs = conn.prepareCall("drop procedure test_stored_prop");
                    cs.execute();
                    cs = conn.prepareCall("drop procedure test_stored_error");
                    cs.execute();
                    cs = conn.prepareCall("drop procedure test_stored_cursor");
                    cs.execute();
                    cs = conn.prepareCall("drop table test_tbl");
                    cs.execute();
                    return null;
                }
            });
        } catch (SQLException ex) {
            LOG.error("Error!", ex);
        }
    }

    @Test
    public void testCreateSQLConnectionPool() throws Exception {
        LOG.debug(Utl.buildBeanStateInfo(context.getStat(), null, null));
        context.execBatch(new SQLActionScalar<Object>() {
            @Override
            public Object exec(SQLContext context, Connection conn) throws Exception {
                Assert.assertNotNull(conn);
                return null;
            }
        });

    }

    @Test(enabled = true)
    public void testSQLCommandOpenCursor() {
        try {
            Double dummysum = context.execBatch(new SQLActionScalar<Double>() {
                @Override
                public Double exec(SQLContext context, Connection conn) throws Exception {
                    Double dummysum = 0.0;
                    String sql = "select user as curuser, :dummy as dm, :dummy1 as dm1 from dual";
                    List<Param> prms = Paramus.set(new ArrayList<Param>()).add("dummy", 101).pop();
                    try(SQLCursor c = context.CreateCursor()
                            .init(conn, sql, prms).open();){
                        while(c.reader().read()){
                            dummysum += c.reader().getValue("DM", Double.class);
                        }
                    }
                    return dummysum;
                }
            });
            LOG.debug("dummysum: " + dummysum);
            Assert.assertEquals(dummysum, 101.0);
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
                    try(SQLCursor c = context.CreateCursor()
                            .init(conn, sql, null).open();){
                        while(c.reader().read()){
                            if(schema == null){
                                schema = c.reader().getValue("XSD_BODY", byte[].class);
                            }

                        }
                    }
                    return schema;
                }
            });
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

                    SQLStoredProc cmd = context.CreateStoredProc();
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
            });
            LOG.debug("leng: " + leng);
            Assert.assertEquals(leng, 3);
        } catch (SQLException ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

    @Test(enabled = true)
    public void testSQLCommandExecError() throws Exception {
        try {
            context.execBatch(new SQLAction<Object, Object>() {
                @Override
                public Object exec(SQLContext context, Connection conn, Object param) throws Exception {
                    LOG.debug("conn: " + conn + "; param: " + param);

                    SQLStoredProc cmd = context.CreateStoredProc();
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
            }, "AnContext");
        } catch (SQLException ex) {
            LOG.error("Error!", ex);
            Assert.assertEquals(ex.getErrorCode(), 20000);
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

                    SQLStoredProc cmd = context.CreateStoredProc();
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
            });
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
            List<Param> params = paramus.get();
            context.execBatch(new SQLAction<List<Param>, Object>() {
                @Override
                public Object exec(SQLContext context, Connection conn, List<Param> param) throws Exception {
                    SQLStoredProc prc = context.CreateStoredProc();
                    prc.init(conn, "gacc.check_login", param).execSQL();
                    return null;
                }
            }, params);
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

                    SQLStoredProc cmd = context.CreateStoredProc();
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
                            String userName = resultSet.getString("USERNAME");
                            LOG.debug("userName: " + userName);
                            Assert.assertEquals(userName, "SCOTT");
                        }
                    }
                    return 0;
                }
            });
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

}
