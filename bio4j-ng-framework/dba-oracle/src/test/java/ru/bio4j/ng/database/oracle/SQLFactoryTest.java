package ru.bio4j.ng.database.oracle;

import com.thoughtworks.xstream.exts.XStreamUtility;
import ru.bio4j.ng.commons.converter.ConvertValueException;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.service.api.Prop;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import ru.bio4j.ng.database.commons.BioCursorDeclaration;
import ru.bio4j.ng.database.commons.DbContextAbstract;
import ru.bio4j.ng.database.commons.DbUtils;
import ru.bio4j.ng.database.commons.SQLExceptionExt;
import ru.bio4j.ng.database.oracle.impl.OraContext;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.User;
import ru.bio4j.ng.model.transport.XLRCfg;
import ru.bio4j.ng.model.transport.jstore.Sort;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class SQLFactoryTest {
    private static final Logger LOG = LoggerFactory.getLogger(SQLFactoryTest.class);
    private static final String testDBDriverName = "oracle.jdbc.driver.OracleDriver";
    private static final String testDBUrl = "jdbc:oracle:thin:@192.168.50.30:1521:EKBS02";
//    private static final String testDBUrl = "jdbc:oracle:thin:@stat4-ora-dev:1521:MICEXDB";
//    private static final String testDBUrl = "jdbc:oracle:thin:@cmon-ora-dev:1521:MICEXDB";
    //private static final String testDBUrl = "jdbc:oracle:oci:@GIVCDB_EKBS03";
    //private static final String testDBUrl = "jdbc:oracle:thin:@https://databasetrial0901-rugivcmkrftrial07058.db.em1.oraclecloudapps.com/apex:1521:databasetrial0901";
    private static final String testDBUsr = "SCOTT";
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
                OraContext.class);
        //if(true) return;
        try {
            context.execBatch(new SQLActionScalar<Object>() {
                @Override
                public Object exec(SQLContext context, Connection conn, User usr) throws Exception {
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
                    sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_with_inout.sql"));
                    cs = conn.prepareCall(sql);
                    cs.execute();

                    sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_storeclob.sql"));
                    cs = conn.prepareCall(sql);
                    cs.execute();
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
                public Object exec(SQLContext context, Connection conn, User usr) throws Exception {
                    CallableStatement cs = conn.prepareCall("drop procedure test_stored_prop");
                    cs.execute();
                    cs = conn.prepareCall("drop procedure test_stored_error");
                    cs.execute();
                    cs = conn.prepareCall("drop procedure test_stored_cursor");
                    cs.execute();
                    cs = conn.prepareCall("drop procedure test_stored_inout");
                    cs.execute();
                    cs = conn.prepareCall("drop table test_tbl");
                    cs.execute();
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
            public Object exec(SQLContext context, Connection conn, User usr) throws Exception {
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
                public Double exec(SQLContext context, Connection conn, User usr) throws Exception {
                    Double dummysum = 0.0;
                    String sql = "select user as curuser, :dummy as dm, :dummy1 as dm1 from dual";
                    List<Param> prms = Paramus.set(new ArrayList<Param>()).add("dummy", 101).pop();
                    try(SQLCursor c = context.createCursor()
                            .init(conn, sql, null).open(prms, usr);){
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

    @Test(enabled = false)
    public void testSQLCommandOpenCursor1() {
        try {
            Double dummysum = 0.0;
            byte[] schema = context.execBatch(new SQLActionScalar<byte[]>() {
                @Override
                public byte[] exec(SQLContext context, Connection conn, User usr) throws Exception {
                    byte[] schema = null;
                    String sql = "select * from table(givcapi.upld.get_schemas)";
                    try(SQLCursor c = context.createCursor()
                            .init(conn, sql, null).open(usr);){
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
                public Integer exec(SQLContext context, Connection conn, User usr) throws Exception {
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
                        cmd.init(conn, storedProgName);
                        cmd.execSQL(paramus.get(), usr);
                    }
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
    public void testSQLCommandExecSQL1() throws Exception {
        try {
            int leng = context.execBatch(new SQLActionScalar<Integer>() {
                @Override
                public Integer exec(SQLContext context, Connection conn, User usr) throws Exception {
                    int leng = 0;
                    LOG.debug("conn: " + conn);

                    SQLStoredProc cmd = context.createStoredProc();
                    String storedProgName = "test_stored_prop";
                    List<Param> prms = new ArrayList<Param>();
                    prms.add(Param.builder().name("p_param1").value("FTW").build());
                    prms.add(Param.builder().name("p_param2").type(MetaType.INTEGER).direction(Param.Direction.OUT).build());
                    cmd.init(conn, storedProgName);
                    Paramus.setParamValue(prms, "p_param1", "QWE");
                    Paramus.setParamValue(prms, "p_param3", "ASD");
                    cmd.execSQL(prms, usr);
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
                public Integer exec(SQLContext context, Connection conn, User usr) throws Exception {
                    int leng = 0;
                    LOG.debug("conn: " + conn);

                    SQLStoredProc cmd = context.createStoredProc();
                    try(Paramus paramus = Paramus.set(new ArrayList<Param>())) {
                        paramus.add(Param.builder().name("p_param1").type(MetaType.INTEGER).value(null).build())
                                .add("p_param2", "QWE")
                                .add(Param.builder().name("p_param3").type(MetaType.BOOLEAN).value(true).build())
                                .add(Param.builder().name("p_param4").type(MetaType.DECIMAL).value("").build());
                        cmd.init(conn, "test_stored_inout");
                        cmd.execSQL(paramus.get(), usr);
                    }
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

    private String creXML() throws Exception {
        XLRCfg xlrCfg = new XLRCfg();

        XLRCfg.DataSource ds = new XLRCfg.DataSource();
        ds.setSql("select 1 from dual");

        ds.setSorts(new ArrayList<>());
        Sort s = new Sort();
        s.setFieldName("sortField");
        s.setDirection(Sort.Direction.DESC);
        ds.getSorts().add(s);

        XLRCfg.ColumnDefinition cd = new XLRCfg.ColumnDefinition();
        cd.setFieldName("field1");
        cd.setTitle("Колонка 1");
        cd.setFormat("##0.00");
        ds.getColumnDefinitions().add(cd);

        xlrCfg.setDss(new ArrayList<>());
        xlrCfg.getDss().add(ds);

        xlrCfg.setAppend(new XLRCfg.Append());
        xlrCfg.getAppend().setInParams(new ArrayList<>());
        xlrCfg.getAppend().getInParams().add(Param.builder().name("inparam1").type(MetaType.STRING).direction(Param.Direction.IN).value("inparam1-value").build());
        xlrCfg.getAppend().setSessionID("sess-id");
        xlrCfg.getAppend().setUserUID("user-uid");
        xlrCfg.getAppend().setUserName("user-name");
        xlrCfg.getAppend().setUserOrgId("user-org-id");
        xlrCfg.getAppend().setUserRoles("user-roles");
        xlrCfg.getAppend().setRemoteIP("remote-ip");


        String encoding = "UTF-8";
        return XStreamUtility.getInstance().toXml(xlrCfg, encoding);

    }

    @Test(enabled = true)
    public void testSQLCommandExecStoreCLOB() throws Exception {
        try {
            context.execBatch((context1, conn, usr) -> {
                LOG.debug("conn: " + conn);

                String xml = creXML();

                SQLStoredProc cmd = context1.createStoredProc();
                List<Param> prms = new ArrayList<>();
                        Paramus.setParam(prms, Param.builder().name("p_param1").type(MetaType.STRING).value("ASD").build(), true);
                        Paramus.setParam(prms, Param.builder().name("p_param2").type(MetaType.INTEGER).value(1).build(), true);
                        Paramus.setParam(prms, Param.builder().name("p_param3").type(MetaType.CLOB).value(xml).build(), true);
                cmd.init(conn, "test_store_clob");
                cmd.execSQL(prms, usr);

            }, null);
        } catch (SQLException ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }
    }

    @Test(enabled = true)
    public void testSQLCommandExecStoreCLOB1() throws Exception {
        try {
            context.execBatch((context1, conn, usr) -> {
                LOG.debug("conn: " + conn);

                String xml = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("qqq.xml"));

                SQLStoredProc cmd = context1.createStoredProc();
                List<Param> prms = new ArrayList<>();
                Paramus.setParam(prms, Param.builder().name("p_param1").type(MetaType.STRING).value("ASD").build(), true);
                Paramus.setParam(prms, Param.builder().name("p_param2").type(MetaType.INTEGER).value(1).build(), true);
                Paramus.setParam(prms, Param.builder().name("p_param3").type(MetaType.CLOB).value(xml).build(), true);
                cmd.init(conn, "test_store_clob");
                cmd.execSQL(prms, usr);

            }, null);
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
                public Integer exec(SQLContext context, Connection conn, User usr) throws Exception {
                    int leng = 0;
                    LOG.debug("conn: " + conn);

                    SQLStoredProc cmd = context.createStoredProc();
                    String storedProgName = "test_stored_prop";
                    List<Param> prms = new ArrayList<>();
                    try(Paramus paramus = Paramus.set(prms)) {
                        paramus.add("param1", "FTW")
                                .add("param3", "ext");
                    }
                    cmd.init(conn, storedProgName);
                    cmd.execSQL(prms, usr);
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
    public void testSQLCommandExecError() throws Exception {
        try {
            context.execBatch(new SQLAction<Object, Object>() {
                @Override
                public Object exec(SQLContext context, Connection conn, Object param, User usr) throws Exception {
                    LOG.debug("conn: " + conn + "; param: " + param);

                    SQLStoredProc cmd = context.createStoredProc();
                    String storedProgName = "test_stored_error";
                    List<Param> prms = new ArrayList<Param>();
                    prms.add(Param.builder().name("p_param1").value("FTW").build());
                    prms.add(Param.builder()
                        .name("p_param2")
                        .type(MetaType.INTEGER)
                        .direction(Param.Direction.OUT)
                        .build());
                    cmd.init(conn, storedProgName);

                    cmd.execSQL(prms, usr);
                    return null;
                }
            }, "AnContext", null);
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
                public Integer exec(SQLContext context, Connection conn, User usr) throws Exception {
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
                        cmd.init(conn, storedProgName).execSQL(paramus.get(), usr);
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

            Paramus paramus = Paramus.set(new ArrayList<Param>());
            paramus.add("p_login", "qweqwe@asd.com/qegedipe")
                   .add(Param.builder().name("v_uid").type(MetaType.STRING).direction(Param.Direction.OUT).build());
            List<Param> params = paramus.pop();
            String uid = context.execBatch((SQLAction<List<Param>, String>) (context, conn, param, usr) -> {
                SQLStoredProc prc = context.createStoredProc();
                prc.init(conn, "bio_login2.check_login").execSQL(param, usr);
                try(Paramus paramus1 = Paramus.set(prc.getParams())) {
                    return paramus1.getValueAsStringByName("v_uid", true);
                }

            }, params, null);

            LOG.debug(String.format("Login: OK; uid: %s", uid));
            Assert.assertEquals(uid, "FTW");
        } catch (SQLException ex) {
            LOG.error("Error!!!", ex);
        }
    }

    @Test(enabled = false)
    public void testSQLCommandStoredProcRetCursor() throws Exception {
        try {
            int c = context.execBatch(new SQLActionScalar<Integer>() {
                @Override
                public Integer exec(SQLContext context, Connection conn, User usr) throws Exception {
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
                        cmd.init(conn, storedProgName).execSQL(paramus.get(), usr);
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
        sb.append("{OraCommand.Params(before exec): {\n");
        for (Param p : params)
            sb.append("\t"+p.toString()+",\n");
        sb.append("}}");

        SQLException e = new SQLException("QWE-TEST");
        SQLExceptionExt r = new SQLExceptionExt(String.format("%s:\n - sql: %s;\n - %s", "Error on execute command.", "select * from dual", sb.toString()), e);
        String msg = r.getMessage();
        LOG.debug(msg);
    }

    @Test(enabled = false)
    public void testSQLCommandOpenCursor2() {
        try {

            SQLContext contextLocal = DbContextAbstract.create(
                    SQLConnectionPoolConfig.builder()
                            .poolName("TEST-CONN-POOL-123")
                            .dbDriverName(testDBDriverName)
                            .dbConnectionUrl(testDBUrl)
                            .dbConnectionUsr("GIVCADMIN")
                            .dbConnectionPwd("j12")
                            .build(),
                    OraContext.class);

            String rslt = contextLocal.execBatch(new SQLActionScalar<String>() {
                @Override
                public String exec(SQLContext context, Connection conn, User usr) throws Exception {
                    String sql = "SELECT * FROM (\n" +
                            "        SELECT pgng$wrpr0.*, ROWNUM rnum$pgng\n" +
                            "          FROM ( with \n" +
                            "orgs as (\n" +
                            "  select /*+ MATERIALIZE */ o.id_org, o.holding_id, o.time_zone, o.id_vnd, DECODE (o.test, 1, 'тестовый', 'реальный') AS test,\n" +
                            "        v.vndname, v.contacts AS vndcontacts\n" +
                            "    from org$r o \n" +
                            "      left join givc_org.softvendor v ON v.id_vnd = o.id_vnd\n" +
                            "),\n" +
                            "ekbps as (\n" +
                            "SELECT /*+ MATERIALIZE */\n" +
                            "   a.packet_id as id,\n" +
                            "  a.org_id,\n" +
                            "  a.sess_prnt_org_id,\n" +
                            "  a.sess_org_id,\n" +
                            "  a.ip_addr as ip,\n" +
                            "  a.registred as date_incoming,\n" +
                            "  decode(a.packet_name, '...', a.zip_name, a.packet_name) as packet_name,\n" +
                            "  a.zip_name,\n" +
                            "  a.processed as date_processing,\n" +
                            "  a.is_loaded,\n" +
                            "  a.cur_pstate as cur_pstate0,\n" +
                            "  decode(a.is_loaded, '1', 'загружен', s.description) as cur_pstate,\n" +
                            "  a.cur_pstate_msg as last_pstate_msg,\n" +
                            "  a.is_log_downloaded,\n" +
                            "  a.log_downloaded,\n" +
                            "  nvl(o1.time_zone, o2.time_zone) as time_zone,\n" +
                            "  nvl(o1.id_vnd, o2.id_vnd) as id_vnd, \n" +
                            "  nvl(o1.vndname, o2.vndname) as vndname, \n" +
                            "  nvl(o1.vndcontacts, o2.vndcontacts) as vndcontacts,\n" +
                            "  nvl(o1.test, o2.test) as test,\n" +
                            "  decode(a.load_method, \n" +
                            "          0, 'Авт. система', \n" +
                            "          1, 'Из кабинета', \n" +
                            "          2, 'CreateXMLStatic', \n" +
                            "          3, 'CreateXMLMobile', \n" +
                            "          4, 'EkbUploadRobot', \n" +
                            "          5, 'Grader', 'не определен') as load_method,\n" +
                            "  decode(a.show_date, null, '00000000', to_char(a.show_date, 'YYYYMMDD')) part_key\n" +
                            "  FROM FPACKET a\n" +
                            "        left join orgs o1 on o1.id_org = a.sess_org_id\n" +
                            "        left join orgs o2 on o2.id_org = a.org_id\n" +
                            "        left join nsi$ekbpstate s on s.id = a.cur_pstate\n" +
                            "  WHERE a.packet_name = nvl(:packet_name_full, a.packet_name) and\n" +
                            "        ((a.registred >= biosys.ai_utl.db_datetime(decode(:reg_from, null, trunc(sysdate), :reg_from), :time_zone)) AND\n" +
                            "         (a.registred < biosys.ai_utl.db_datetime(decode(:reg_to, null, trunc(sysdate), :reg_to), :time_zone)+1))\n" +
                            "    AND ((:force_org_id IS NULL) OR \n" +
                            "          ((a.org_id = :force_org_id) OR ((a.sess_org_id = :force_org_id) and (a.org_id = o1.holding_id))\n" +
                            "          or (a.sess_prnt_org_id =:force_org_id))\n" +
                            "         )\n" +
                            "    \n" +
                            ")\n" +
                            "SELECT \n" +
                            "  a.id, \n" +
                            "  a.org_id,\n" +
                            "  a.sess_prnt_org_id,\n" +
                            "  a.sess_org_id,\n" +
                            "  a.ip, \n" +
                            "  a.date_incoming,\n" +
                            "  a.packet_name, \n" +
                            "  a.zip_name,\n" +
                            "  a.date_processing,\n" +
                            "  a.time_zone,\n" +
                            "  a.is_loaded,\n" +
                            "  a.cur_pstate0,\n" +
                            "  a.cur_pstate,\n" +
                            "  a.last_pstate_msg,\n" +
                            "  a.is_log_downloaded,\n" +
                            "  a.log_downloaded, \n" +
                            "  a.id_vnd, a.vndname, a.vndcontacts,\n" +
                            "  a.load_method,\n" +
                            "  a.test,\n" +
                            "  a.part_key\n" +
                            "  FROM ekbps a\n" +
                            "  WHERE ((:p_sys_curusr_roles in ('6')) or\n" +
                            "         ((:p_sys_curusr_roles in ('4')) and (\n" +
                            "           (a.org_id = to_number(:p_sys_curodepuid)) or\n" +
                            "           (a.org_id in (select o.id_org from givc_org.org o where o.holding_id = to_number(:p_sys_curodepuid)))\n" +
                            "          )\n" +
                            "         ) or\n" +
                            "         ((:p_sys_curusr_roles in ('3')) and (\n" +
                            "            /*(a.sess_org_id = to_number(:SYS_CURODEPUID)) and*/\n" +
                            "            ((a.org_id = to_number(:p_sys_curodepuid)) or\n" +
                            "             (a.org_id in (select nvl(o.holding_id, o.id_org) from givc_org.org o where o.id_org = to_number(:p_sys_curodepuid)))\n" +
                            "            )\n" +
                            "          )\n" +
                            "         )\n" +
                            "    ) AND \n" +
                            "    (\n" +
                            "      (a.org_id = decode(:org_id, null, a.org_id, nvl(to_number(regexp_substr(:org_id, '^\\d+$')), 0))) and\n" +
                            "      (nvl(a.sess_prnt_org_id, 0) = decode(:sess_prnt_org_id, null, nvl(a.sess_prnt_org_id, 0), nvl(to_number(regexp_substr(:sess_prnt_org_id, '^\\d+$')), 0))) and\n" +
                            "      (a.sess_org_id = decode(:sess_org_id, null, a.sess_org_id, nvl(to_number(regexp_substr(:sess_org_id, '^\\d+$')), 0))) and\n" +
                            "      (a.packet_name like decode(:packet_name, null, '%', '%'||lower(:packet_name)||'%')) and\n" +
                            "      (a.ip like decode(:ip, null, '%', '%'||lower(:ip)||'%')) and\n" +
                            "      (a.cur_pstate like decode(:cur_pstate, null, '%', '%'||lower(:cur_pstate)||'%')) and\n" +
                            "      (a.last_pstate_msg like decode(:message, null, '%', '%'||lower(:message)||'%')) and\n" +
                            "      (lower(a.load_method) like decode(:load_method, null, '%', '%'||lower(:load_method)||'%')) and\n" +
                            "      (a.test like decode(:test, null, '%', '%'||lower(:test)||'%')) \n" +
                            "    )\n" +
                            "  ORDER BY date_incoming desc\n" +
                            " ) pgng$wrpr0\n" +
                            "    ) pgng$wrpr WHERE (pgng$wrpr.rnum$pgng > :paging$offset) AND (pgng$wrpr.rnum$pgng <= :paging$last)";
                    int cnt = 0;
                    List<Param> params = new ArrayList<>();


                    //params.add(Param.builder().name("packet_name_full").type(MetaType.STRING).build());
                    //params.add(Param.builder().name("reg_from").type(MetaType.DATE).build());
                    //params.add(Param.builder().name("reg_to").type(MetaType.DATE).build());
                    //params.add(Param.builder().name("time_zone").type(MetaType.INTEGER).build());
                    params.add(Param.builder().name("p_sys_curusr_roles").value("6").type(MetaType.STRING).build());
                    //params.add(Param.builder().name("p_sys_curodepuid").type(MetaType.STRING).build());

                    params.add(Param.builder().name("force_org_id").type(MetaType.INTEGER).build());
                    //params.add(Param.builder().name("org_id").type(MetaType.INTEGER).build());
                    //params.add(Param.builder().name("sess_org_id").type(MetaType.INTEGER).build());

                    //params.add(Param.builder().name("sess_prnt_org_id").type(MetaType.INTEGER).build());
                    //params.add(Param.builder().name("packet_name").type(MetaType.STRING).build());
                    //params.add(Param.builder().name("ip").type(MetaType.STRING).build());
                    //params.add(Param.builder().name("cur_pstate").type(MetaType.STRING).build());
                    //params.add(Param.builder().name("message").type(MetaType.STRING).build());
                    //params.add(Param.builder().name("load_method").type(MetaType.STRING).build());
                    //params.add(Param.builder().name("test").type(MetaType.STRING).build());
                    //params.add(Param.builder().name("p_sys_curusr_uid").type(MetaType.STRING).build());
                    //params.add(Param.builder().name("p_sys_curusr_grants").type(MetaType.STRING).build());
                    //params.add(Param.builder().name("prm1").value("qwe").build());
                    //params.add(Param.builder().name("prm2").value("qwe").build());
                    params.add(Param.builder().name("paging$offset").value(0).type(MetaType.INTEGER).build());
                    params.add(Param.builder().name("paging$last").value(25).type(MetaType.INTEGER).build());

                    try(SQLCursor c = context.createCursor()
                            .init(conn, sql, params).open(usr);){
                        while(c.reader().next()){
//                                schema = c.reader().getValue("XSD_BODY", byte[].class);
                            cnt++;
                        }
                    }
                    return ""+cnt;
                }
            }, null);
            Assert.assertEquals("25", rslt);
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

    @Test(enabled = false)
    public void testSQLCommandOpenCursor3() {
        try {

            SQLContext contextLocal = DbContextAbstract.create(
                SQLConnectionPoolConfig.builder()
                    .poolName("TEST-CONN-POOL-123")
                    .dbDriverName(testDBDriverName)
                    .dbConnectionUrl(testDBUrl)
                    .dbConnectionUsr("GIVCADMIN")
                    .dbConnectionPwd("j12")
                    .build(),
                OraContext.class);

            String rslt = contextLocal.execBatch(new SQLActionScalar<String>() {
                @Override
                public String exec(SQLContext context, Connection conn, User usr) throws Exception {
                    String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("111.sql"));

                    int cnt = 0;
                    List<Param> params = new ArrayList<>();

/*
                    1-p_sys_curusr_roles(in)(VARCHAR)..................."6";
                    2-p_sys_curusr_org_uid(in)(VARCHAR)................."5567";
                    3-org_id(in)(INTEGER)...............................[244];
                    4-reg_from(in)(DATE)................................[Thu Aug 04 00:00:00 GMT+03:00 2016];
                    5-reg_to(in)(DATE)..................................[Thu Aug 04 00:00:00 GMT+03:00 2016];
                    6-film(in)(VARCHAR)................................."";
                    7-sroom_id(in)(INTEGER).............................[null];
                    8-force_org_id(in)(VARCHAR).........................[null];
                    9-paging$offset(in)(INTEGER)........................[0];
                    10-paging$last(in)(INTEGER).........................[25];
*/

                    params.add(Param.builder().name("p_sys_curusr_roles").value("6").type(MetaType.STRING).build());
                    params.add(Param.builder().name("p_sys_curusr_org_uid").value("5567").type(MetaType.STRING).build());
                    params.add(Param.builder().name("org_id").value(244).type(MetaType.INTEGER).build());

                    java.util.Date testDateValue = new java.util.Date();
                    params.add(Param.builder().name("reg_from").value(testDateValue).type(MetaType.DATE).build());
                    params.add(Param.builder().name("reg_to").value(testDateValue).type(MetaType.DATE).build());

                    params.add(Param.builder().name("film").value("").type(MetaType.STRING).build());

                            params.add(Param.builder().name("force_org_id").type(MetaType.INTEGER).build());
                    params.add(Param.builder().name("paging$offset").value(0).type(MetaType.INTEGER).build());
                    params.add(Param.builder().name("paging$last").value(25).type(MetaType.INTEGER).build());

                    try(SQLCursor c = context.createCursor()
                            .init(conn, sql, params).open(usr);){
                        while(c.reader().next()){
//                                schema = c.reader().getValue("XSD_BODY", byte[].class);
                            cnt++;
                        }
                    }
                    return ""+cnt;
                }
            }, null);
            Assert.assertEquals(rslt, "17");
        } catch (Exception ex) {
            LOG.error("Error!", ex);
            Assert.fail();
        }

    }

    @Test(enabled = true)
    public void testSQLCommandOpenCursor4() throws Exception {

        SQLContext ctx = DbContextAbstract.create(
                SQLConnectionPoolConfig.builder()
                        .poolName("TEST-CONN-POOL-123")
                        .dbDriverName(testDBDriverName)
                        .dbConnectionUrl(testDBUrl)
                        .dbConnectionUsr("GIVCADMIN")
                        .dbConnectionPwd("j12")
                        .build(),
                OraContext.class);


        String rslt = ctx.execBatch((SQLAction<BioCursorDeclaration, String>) (context, conn, cur, usr) -> {

            String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("111.sql"));

            int cnt = 0;
            List<Param> params = new ArrayList<>();

/*
                1-p_sys_curusr_roles(in)(VARCHAR)..................."6";
                2-p_sys_curusr_org_uid(in)(VARCHAR)................."5567";
                3-org_id(in)(INTEGER)...............................[244];
                4-reg_from(in)(DATE)................................[Thu Aug 04 00:00:00 GMT+03:00 2016];
                5-reg_to(in)(DATE)..................................[Thu Aug 04 00:00:00 GMT+03:00 2016];
                6-film(in)(VARCHAR)................................."";
                7-sroom_id(in)(INTEGER).............................[null];
                8-force_org_id(in)(VARCHAR).........................[null];
                9-paging$offset(in)(INTEGER)........................[0];
                10-paging$last(in)(INTEGER)..........................[25];
*/

            params.add(Param.builder().name("p_sys_curusr_roles").value("6").type(MetaType.STRING).build());
            params.add(Param.builder().name("p_sys_curusr_org_uid").value("5567").type(MetaType.STRING).build());
            params.add(Param.builder().name("org_id").value(244).type(MetaType.INTEGER).build());

            Date testDateValue = new Date();
            params.add(Param.builder().name("reg_from").value(testDateValue).type(MetaType.DATE).build());
            params.add(Param.builder().name("reg_to").value(testDateValue).type(MetaType.DATE).build());

            params.add(Param.builder().name("film").value("").type(MetaType.STRING).build());

            params.add(Param.builder().name("force_org_id").type(MetaType.INTEGER).build());
            params.add(Param.builder().name("paging$offset").value(0).type(MetaType.INTEGER).build());
            params.add(Param.builder().name("paging$last").value(25).type(MetaType.INTEGER).build());

            try(SQLCursor c = context.createCursor()
                    .init(conn, sql, params).open(usr);){
                while(c.reader().next()){
//                                schema = c.reader().getValue("XSD_BODY", byte[].class);
                    cnt++;
                }
            }
            return ""+cnt;

        }, null, null);
        //response;
    }

    @Test(enabled = false)
    public void testSQLCommandOpenCursor5() throws Exception {

        SQLContext ctx = DbContextAbstract.create(
                SQLConnectionPoolConfig.builder()
                        .poolName("TEST-CONN-POOL-123")
                        .dbDriverName(testDBDriverName)
                        .dbConnectionUrl(testDBUrl)
                        .dbConnectionUsr("GIVCADMIN")
                        .dbConnectionPwd("j12")
                        .build(),
                OraContext.class);

        String rslt = ctx.execBatch((context1, conn, cur, usr) -> {

            String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("333.sql"));

            List<Param> params = new ArrayList<>();
            params.add(Param.builder().name("p_rpt_uid").value("4F923F3C7A395D05E0531E32A8C06A1D").type(MetaType.STRING).build());

            try(SQLCursor c = context1.createCursor()
                    .init(conn, sql, params).open(usr);) {
                if(c.reader().next()){
                    return c.reader().getValue("state_desc", String.class);
                }
            }
            return null;

        }, null, null);

        Assert.assertTrue(rslt.length() > 0);
    }

    public static class TestROject {
        @Prop(name = "id")
        public Integer fid;
        @Prop(name = "name")
        public String fname;
    }

    @Test(enabled = true)
    public void test67() throws Exception {

        SQLContext ctx = DbContextAbstract.create(
                SQLConnectionPoolConfig.builder()
                        .poolName("TEST-CONN-POOL-123")
                        .dbDriverName(testDBDriverName)
                        .dbConnectionUrl(testDBUrl)
                        .dbConnectionUsr("GIVCADMIN")
                        .dbConnectionPwd("j12")
                        .build(),
                OraContext.class);


        String rslt = ctx.execBatch((SQLAction<BioCursorDeclaration, String>) (context, conn, cur, usr) -> {

            String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("222.sql"));

            List<Param> params = new ArrayList<>();
            TestROject r = null;
            try(SQLCursor c = context.createCursor()
                    .init(conn, sql, params).open(usr);){
                if(c.reader().next()){
                    r = DbUtils.createBeanFromReader(c.reader(), TestROject.class);
                }
            }
            return r.fname;

        }, null, null);
        Assert.assertEquals(rslt, "qwe");
    }
}
