package ru.bio4j.ng.database.pgsql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.database.commons.DbContextAbstract;
import ru.bio4j.ng.database.commons.DbContextFactory;
import ru.bio4j.ng.database.pgsql.impl.PgSQLContext;
import ru.bio4j.ng.database.pgsql.impl.PgSQLUtilsImpl;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;

import java.util.ArrayList;
import java.util.List;

public class SQLFactoryTest1 {
    private static final Logger LOG = LoggerFactory.getLogger(SQLFactoryTest1.class);
    private static final String testDBDriverName = "org.postgresql.Driver";
//    private static final String testDBUrl = "jdbc:postgresql://192.168.50.47:5432/postgres";
//    private static final String testDBUrl = "jdbc:postgresql://localhost:5432/postgres";
//    private static final String testDBUrl = "jdbc:oracle:thin:@cmon-ora-dev:1521:MICEXDB";
    private static final String testDBUrl = "jdbc:postgresql://192.168.10.136:5432/postgres";
    //private static final String testDBUrl = "jdbc:oracle:oci:@GIVCDB_EKBS03";
    //private static final String testDBUrl = "jdbc:oracle:thin:@https://databasetrial0901-rugivcmkrftrial07058.db.em1.oraclecloudapps.com/apex:1521:databasetrial0901";
//    private static final String testDBUsr = "SCOTT";
//    private static final String testDBPwd = "tiger";
    private static final String testDBUsr = "postgres";
    private static final String testDBPwd = "root";

    private static SQLContext context;

    @BeforeTest(enabled = false)
    public static void setUpClass() throws Exception {
        context = DbContextFactory.createApache(
                SQLConnectionPoolConfig.builder()
                        .poolName("TEST-CONN-POOL")
                        .dbDriverName(testDBDriverName)
                        .dbConnectionUrl(testDBUrl)
                        .dbConnectionUsr(testDBUsr)
                        .dbConnectionPwd(testDBPwd)
                        .build(),
                PgSQLContext.class);
    }

    @AfterTest(enabled = false)
    public static void finClass() throws Exception {
    }

    @Test(enabled = false)
    public void testCreateSQLConnectionPool() throws Exception {
        context.execBatch((context, conn, usr) -> {
            Assert.assertNotNull(conn);
            return null;
        }, null);

    }

    @Test(enabled = true)
    public void testCutDirName() throws Exception {
        String s = PgSQLUtilsImpl.cutDirNames("OUT p_param2 integer");
        Assert.assertEquals(s, "p_param2 integer");
    }

    @Test(enabled = true)
    public void testCutDirName1() throws Exception {
        String s = PgSQLUtilsImpl.cutDirNames("p_param2 integer");
        Assert.assertEquals(s, "p_param2 integer");
    }

    @Test(enabled = true)
    public void extractDirName() throws Exception {
        String s = PgSQLUtilsImpl.extractDirName("OUT p_param2 integer");
        Assert.assertEquals(s, "OUT");
    }

    @Test(enabled = true)
    public void extractDirName1() throws Exception {
        String s = PgSQLUtilsImpl.extractDirName("p_param2 integer");
        Assert.assertEquals(s, "IN");
    }

    @Test(enabled = true)
    public void testParsParams() throws Exception {
        StringBuilder args = new StringBuilder();
        String paramsList = "p_param1 character varying, OUT p_param2 integer";
        List<Param> params = new ArrayList<>();
        PgSQLUtilsImpl.parsParams(paramsList, params, null);
        Assert.assertEquals(params.get(0).getDirection(), Param.Direction.IN);
        Assert.assertEquals(params.get(0).getType(), MetaType.STRING);
        Assert.assertEquals(params.get(1).getDirection(), Param.Direction.OUT);
        Assert.assertEquals(params.get(1).getType(), MetaType.INTEGER);
    }

    @Test(enabled = true)
    public void testParsParams1() throws Exception {
        StringBuilder args = new StringBuilder();
        String paramsList = "p_param1 character varying, OUT p_param2 integer";
        List<Param> pps = new ArrayList<>();
        pps.add(Param.builder().name("param1").value("qwe").type(MetaType.STRING).direction(Param.Direction.INOUT).override(true).build());
        pps.add(Param.builder().name("param2").value(0).type(MetaType.INTEGER).override(true).build());
        List<Param> params = new ArrayList<>();
        PgSQLUtilsImpl.parsParams(paramsList, params, pps);
        Assert.assertEquals(params.get(0).getDirection(), Param.Direction.IN);
        Assert.assertEquals(params.get(0).getType(), MetaType.STRING);
        Assert.assertEquals(params.get(1).getDirection(), Param.Direction.OUT);
        Assert.assertEquals(params.get(1).getType(), MetaType.INTEGER);
    }
}
