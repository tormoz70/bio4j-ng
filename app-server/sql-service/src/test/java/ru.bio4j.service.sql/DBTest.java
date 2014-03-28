package ru.bio4j.service.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import ru.bio4j.collections.Parameter;
import ru.bio4j.func.UnsafeFunction;
import ru.bio4j.service.sql.db.Procedure;
import ru.bio4j.service.sql.query.QueryHelper;
import ru.bio4j.service.sql.query.handlers.ResultReceiver;
import ru.bio4j.service.sql.result.QueryResult;
import ru.bio4j.service.sql.result.handlers.DefaultResultHandler;
import ru.bio4j.service.sql.result.handlers.ResultHandler;

import java.lang.reflect.UndeclaredThrowableException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

@Test(singleThreaded = true)
public class DBTest extends TestSuiteSetup {

    private static final Logger LOG = LoggerFactory.getLogger(DBTest.class);

    @Test
    public void testSimpleProcCall() throws Exception {
        String guid = QueryContext.call(new UnsafeFunction<QueryContext, String, Throwable>() {

            @Override
            public String apply(QueryContext key) throws Throwable {
                Connection connection = key.getConnection();
                connection.createStatement().executeUpdate(
                        "CREATE PROCEDURE procName(OUT result VARCHAR(36)) " +
                                "  BEGIN ATOMIC" +
                                "    SET result = uuid(uuid());" +
                                "  END");

                return QueryHelper.query(
                        new Query("{call procName(/*$GUID, out, type=BINARY*/)}"),
                        new ResultReceiver<CallableStatement, String>() {

                            @Override
                            public String handle(CallableStatement r, Query query, QueryContext context) throws Exception {
                                return r.getString(1);
                            }
                        });
            }
        });
        LOG.info("Generated guid: {}", guid);
        assertNotNull(guid);
    }

    @Test(enabled = false) //not work with in memory db, tested on oracle - можно применять для процедур - которые не возвращают на ui сложной структуры, утилитарные процедуры и пр
    public void testProceduresSchemaCall() {
        QueryContext.call(new UnsafeFunction<QueryContext, Object, Exception> () {

            @Override
            public Object apply(QueryContext key) throws Exception {
                /*key.getConnection().createStatement().executeUpdate(

                "PROCEDURE findMin(x IN number, y IN number, z OUT number) IS " +
                "        BEGIN " +
                "IF x < y THEN " +
                "z:= x; " +
                "ELSE " +
                "z:= y; " +
                "END IF; " +
                "END; "
                ); */
                final Parameter x = new Parameter(4, null);
                final Parameter y = new Parameter(5, null);
                Map<String, Parameter> parameterMap = new HashMap<>();
                parameterMap.put("X", x);
                parameterMap.put("Y", y);

                return QueryHelper.query(
                        new Query("{call findMin}", parameterMap),
                        new ResultReceiver<CallableStatement, String>() {

                            @Override
                            public String handle(CallableStatement r, Query query, QueryContext context) throws Exception {
                                LOG.debug("result = {}", r.getInt(3));
                                assertEquals(r.getInt(3), x.getValue());
                                return r.getString(3);
                            }
                        });
            }
        });
    }

    @Test
    public void testSimpleSelect() throws Exception {
        String reslt = QueryContext.call(new UnsafeFunction<QueryContext, String, Throwable>() {

            @Override
            public String apply(QueryContext context) throws Throwable {
                Connection connection = context.getConnection();
                connection.createStatement().execute(
                        "CREATE TABLE dummy_test(result VARCHAR(36))");
                connection.createStatement().execute(
                        "INSERT INTO dummy_test values('test-value')");

                return QueryHelper.query(
                        new Query("select * from dummy_test"),
                        new ResultReceiver<ResultSet, String>() {
                            @Override
                            public String handle(ResultSet r, Query query, QueryContext context) throws Exception {
                                r.next();
                                return r.getString(1);
                            }
                        });
            }
        });
        LOG.info("reslt: {}", reslt);
        assertNotNull(reslt);
        assertEquals("test-value", reslt);
    }

    @Test
    public void testCustomMappingSelect() throws Exception {

        QueryResult rslt = QueryContext.call(new UnsafeFunction<QueryContext, QueryResult, Throwable>() {
            @Override
            public QueryResult apply(QueryContext context) throws Throwable {
                Connection connection = context.getConnection();
                connection.createStatement().execute(
                        "CREATE TABLE dummy2(result_column VARCHAR(36))");
                connection.createStatement().execute(
                        "INSERT INTO dummy2 values('true')");
                final Parameter testParam = new Parameter("true", null);
                final DefaultResultHandler defaultResultHandler = new DefaultResultHandler();
                return QueryHelper.query(
                        new Query("select result_column /*@result_column, title=Булеан, javaType=boolean*/ from dummy2 where result_column = /*$result_column, type=varchar {*/'true'/*}*/",
                                Collections.<String, Parameter>singletonMap("RESULT_COLUMN", testParam)),
                        defaultResultHandler);
            }
        });
        LOG.info("rslt: {}", rslt);
        assertNotNull(rslt);
        assertEquals("RESULT_COLUMN", rslt.getFields().get(0));
        assertEquals(Boolean.TRUE, rslt.getValue(0, 0));
    }

    @Test(expectedExceptions = {UndeclaredThrowableException.class, IllegalArgumentException.class})
    public void testException() throws Exception {
        QueryContext.call(new UnsafeFunction<QueryContext, QueryResult, Throwable>() {

            @Override
            public QueryResult apply(QueryContext context) throws Throwable {
                Connection connection = context.getConnection();
                connection.createStatement().execute(
                        "CREATE TABLE dummy_excep(result_column VARCHAR(36))");
                final Parameter testParam = new Parameter("true", null);
                return QueryHelper.query(
                        new Query("select result_column from dummy_excep where result_column = /*$result_column, type=varchar {*/'test-value'/*}*/",
                                Collections.<String, Parameter>singletonMap("TEST", testParam)),
                        new ResultHandler<QueryResult>() {
                            @Override
                            public QueryResult handle(ResultSet rs, Query query, QueryContext context) throws SQLException {
                                return null;
                            }
                        });
            }
        });
    }

    @Test(enabled = false)
    public void testProceduresSchemaInfo() {
        QueryContext.call(new UnsafeFunction<QueryContext, Object, Exception> () {

            @Override
            public Object apply(QueryContext key) throws Exception {
                String procName = "TEST_" + System.currentTimeMillis();
                key.getConnection().createStatement().execute(
                        "CREATE FUNCTION " + procName + " (e_type INT) " +
                                "  RETURNS VARCHAR(16) " +
                                "  RETURN (select CURRENT_TIME || '_date-' || e_type from (values(0)) );");

                Procedure procedure = key.getDB().getDefaultSchema().getProcedure(procName);
                assertEquals(procName, procedure.getName());
                List<ru.bio4j.service.sql.db.Parameter> parameters = procedure.getColumns();
                for(ru.bio4j.service.sql.db.Parameter parameter : parameters) {
                    LOG.debug("parameter: {}", parameter);
                }
                return null;
            }
        });
    }


}
