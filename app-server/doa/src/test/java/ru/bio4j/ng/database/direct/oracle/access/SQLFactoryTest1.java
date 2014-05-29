package ru.bio4j.ng.database.direct.oracle.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.converter.ConvertValueException;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.database.doa.impl.OraContext;
import ru.bio4j.ng.database.doa.impl.SQLExceptionExt;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLFactoryTest1 {
    private static final Logger LOG = LoggerFactory.getLogger(SQLFactoryTest1.class);
    private static final String testDBDriverName = "oracle.jdbc.driver.OracleDriver";
    private static final String testDBUrl = "jdbc:oracle:thin:@192.168.50.30:1521:GIVCDB";
    //private static final String testDBUrl = "jdbc:oracle:oci:@GIVCDB_EKBS03";
    //private static final String testDBUrl = "jdbc:oracle:thin:@https://databasetrial0901-rugivcmkrftrial07058.db.em1.oraclecloudapps.com/apex:1521:databasetrial0901";
    private static final String testDBUsr = "SCOTT";
    private static final String testDBPwd = "tiger";

    private static SQLContext context;

    @BeforeTest(enabled = false)
    public static void setUpClass() throws Exception {
        context = OraContext.create(
                SQLConnectionPoolConfig.builder()
                        .poolName("TEST-CONN-POOL")
                        .dbDriverName(testDBDriverName)
                        .dbConnectionUrl(testDBUrl)
                        .dbConnectionUsr(testDBUsr)
                        .dbConnectionPwd(testDBPwd)
                        .build()
        );
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
