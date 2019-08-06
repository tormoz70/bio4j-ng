package ru.bio4j.ng.database.oracle;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.ng.database.api.SQLConnectionPoolConfig;
import ru.bio4j.ng.database.api.SQLContext;
import ru.bio4j.ng.database.commons.CrudReaderApi;
import ru.bio4j.ng.database.commons.DbContextFactory;
import ru.bio4j.ng.database.oracle.impl.OraContext;
import ru.bio4j.ng.database.api.SQLDefinition;
import ru.bio4j.ng.service.types.CursorParser;

import java.io.FileOutputStream;

@Test
public class RestApiAdapterTest {
    private static final Logger LOG = LoggerFactory.getLogger(RestApiAdapterTest.class);
    private static final String testDBDriverName = "oracle.jdbc.driver.OracleDriver";
    private static final String testDBUrl = "jdbc:oracle:thin:@192.168.70.30:1521:EKBS02";
//    private static final String testDBUrl = "jdbc:oracle:thin:@stat4-ora-dev:1521:MICEXDB";
//    private static final String testDBUrl = "jdbc:oracle:thin:@cmon-ora-dev:1521:MICEXDB";
    //private static final String testDBUrl = "jdbc:oracle:oci:@GIVCDB_EKBS03";
    //private static final String testDBUrl = "jdbc:oracle:thin:@https://databasetrial0901-rugivcmkrftrial07058.db.em1.oraclecloudapps.com/apex:1521:databasetrial0901";
    private static final String testDBUsr = "SCOTT";
    private static final String testDBPwd = "tiger";

    private static SQLContext context;

//    @BeforeTest
    public static void setUpClass() throws Exception {
        context = DbContextFactory.createApache(
                SQLConnectionPoolConfig.builder()
                        .poolName("TEST-CONN-POOL")
                        .dbDriverName(testDBDriverName)
                        .dbConnectionUrl(testDBUrl)
                        .dbConnectionUsr(testDBUsr)
                        .dbConnectionPwd(testDBPwd)
                        .build(),
                OraContext.class);
        //if(true) return;
//        try {
//            context.execBatch((context) -> {
//                String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_test_table.sql"));
//                DbUtils.execSQL(context.getCurrentConnection(), sql);
//                sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_simple.sql"));
//                DbUtils.execSQL(context.getCurrentConnection(), sql);
//                sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_error.sql"));
//                DbUtils.execSQL(context.getCurrentConnection(), sql);
//                sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_ret_cursor.sql"));
//                DbUtils.execSQL(context.getCurrentConnection(), sql);
//                sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_with_inout.sql"));
//                DbUtils.execSQL(context.getCurrentConnection(), sql);
//
//                sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("ddl_cre_prog_storeclob.sql"));
//                DbUtils.execSQL(context.getCurrentConnection(), sql);
//                return null;
//            }, null);
//        } catch (SQLException ex) {
//            LOG.error("Error!", ex);
//        }
    }

//    @AfterTest
    public static void finClass() throws Exception {
        //if(true) return;
//        try {
//            context.execBatch(new SQLActionScalar0<Object>() {
//                @Override
//                public Object exec(SQLContext context) throws Exception {
//                    DbUtils.execSQL(context.getCurrentConnection(), "drop procedure test_stored_prop");
//                    DbUtils.execSQL(context.getCurrentConnection(), "drop procedure test_stored_error");
//                    DbUtils.execSQL(context.getCurrentConnection(), "drop procedure test_stored_cursor");
//                    DbUtils.execSQL(context.getCurrentConnection(), "drop procedure test_stored_inout");
//                    DbUtils.execSQL(context.getCurrentConnection(), "drop table test_tbl");
//                    return null;
//                }
//            }, null);
//        } catch (SQLException ex) {
//            LOG.error("Error!", ex);
//        }
    }


    @Test(enabled = true)
    public void testExport() throws Exception {

        SQLContext ctx = DbContextFactory.createApache(
                SQLConnectionPoolConfig.builder()
                        .poolName("TEST-CONN-POOL-123")
                        .dbDriverName(testDBDriverName)
                        .dbConnectionUrl(testDBUrl)
                        .dbConnectionUsr("GIVCADMIN")
                        .dbConnectionPwd("j12")
                        .build(),
                OraContext.class);


        ctx.execBatch((context) -> {

            SQLDefinition cursor = CursorParser.pars(Thread.currentThread().getContextClassLoader().getResourceAsStream("films.xml"), "films");
            HSSFWorkbook wb = CrudReaderApi.toExcel(null, null, null, context, cursor);
            FileOutputStream out = new FileOutputStream("d:\\test.xls");
            wb.write(out);

        }, null);
        Assert.assertEquals("qwe", "qwe");
    }


}
