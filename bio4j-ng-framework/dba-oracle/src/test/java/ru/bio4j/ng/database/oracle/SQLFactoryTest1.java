package ru.bio4j.ng.database.oracle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.database.commons.DbContextAbstract;
import ru.bio4j.ng.database.oracle.impl.OraContext;

import java.sql.Connection;

public class SQLFactoryTest1 {
    private static final Logger LOG = LoggerFactory.getLogger(SQLFactoryTest1.class);
    private static final String testDBDriverName = "oracle.jdbc.driver.OracleDriver";
    private static final String testDBUrl = "jdbc:oracle:thin:@192.168.50.30:1521:GIVCDB";
//    private static final String testDBUrl = "jdbc:oracle:thin:@stat4-ora-dev:1521:MICEXDB";
//    private static final String testDBUrl = "jdbc:oracle:thin:@cmon-ora-dev:1521:MICEXDB";
//    private static final String testDBUrl = "jdbc:oracle:oci:@GIVCDB_EKBS03";
//    private static final String testDBUrl = "jdbc:oracle:thin:@https://databasetrial0901-rugivcmkrftrial07058.db.em1.oraclecloudapps.com/apex:1521:databasetrial0901";
    private static final String testDBUsr = "SCOTT";
    private static final String testDBPwd = "tiger";

    private static SQLContext context;

    @BeforeTest(enabled = false)
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
    }

    @AfterTest(enabled = false)
    public static void finClass() throws Exception {
    }

    @Test(enabled = false)
    public void testCreateSQLConnectionPool() throws Exception {
        context.execBatch(new SQLActionScalar<Object>() {
            @Override
            public Object exec(SQLContext context, Connection conn) throws Exception {
                Assert.assertNotNull(conn);
                return null;
            }
        });

    }

}
