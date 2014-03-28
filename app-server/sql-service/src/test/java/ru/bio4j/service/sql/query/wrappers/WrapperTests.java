package ru.bio4j.service.sql.query.wrappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import ru.bio4j.func.UnsafeFunction;
import ru.bio4j.model.transport.jstore.Sort;
import ru.bio4j.model.transport.jstore.filter.Filter;
import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.QueryContext;
import ru.bio4j.service.sql.TestSuiteSetup;
import ru.bio4j.service.sql.query.QueryHelper;
import ru.bio4j.service.sql.query.handlers.ResultReceiver;
import ru.bio4j.util.Strings;

import java.sql.Connection;
import java.sql.ResultSet;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class WrapperTests extends TestSuiteSetup {

    private static final Logger LOG = LoggerFactory.getLogger(WrapperTests.class);

    @Test(enabled = true)
    public void pagingTestHSQLDB() {

        String reslt = QueryContext.call(new UnsafeFunction<QueryContext, String, Throwable>() {

            @Override
            public String apply(QueryContext context) throws Throwable {
                Connection connection = context.getConnection();
                connection.createStatement().execute(
                    "CREATE TABLE dummy(result VARCHAR(36))");
                connection.createStatement().execute(
                    "INSERT INTO dummy values('test-first-value'), ('test-second-value'), ('test-third-value');");

                final Query query = new Query("select * from dummy");
                query.setOffset(1);
                query.setCount(1);

                return QueryHelper.query(
                    query,
                    new ResultReceiver<ResultSet, String>() {
                        @Override
                        public String handle(ResultSet r, Query query1, QueryContext context) throws Exception {
                            r.next();
                            return r.getString(1);
                        }
                    });
            }
        });
        LOG.debug(reslt);
        assertNotNull(reslt);
        assertEquals("test-second-value", reslt);

    }

    @Test(enabled = false)
    public void pagingTestORA() {

        int reslt = QueryContext.call(new UnsafeFunction<QueryContext, Integer, Throwable>() {

            @Override
            public Integer apply(QueryContext context) throws Throwable {
                Connection connection = context.getConnection();

                final Query query = new Query("select * from EMP");
                query.setOffset(5);
                query.setCount(6);

                return QueryHelper.query(
                        query,
                        new ResultReceiver<ResultSet, Integer>() {
                            @Override
                            public Integer handle(ResultSet r, Query query1, QueryContext context) throws Exception {
                                int selectedCnt = 0;
                                while(r.next())
                                    selectedCnt++;
                                return selectedCnt;
                            }
                        });
            }
        });
        LOG.debug("Selected rows count: " + reslt);
        assertNotNull(reslt);
        assertEquals(6, reslt);

    }

    @Test(enabled = false)
    public void filterTestORA() {

        Integer reslt = QueryContext.call(new UnsafeFunction<QueryContext, Integer, Throwable>() {

            @Override
            public Integer apply(QueryContext context) throws Throwable {
                Connection connection = context.getConnection();
                connection.setAutoCommit(false);

                final Query query = new Query("select a.* from EMP a where a.deptno=/*$deptno, type=numeric {*/20/*}*/");
                query.setParam("DEPTNO", 20, null);
                query.setFilter(
                        Filter.and (
                            Filter.bgn("JOB", "ANAL", false),
                            Filter.contains("ENAME", "o", true)
                        )
                );

                return QueryHelper.query(
                        query,
                        new ResultReceiver<ResultSet, Integer>() {
                            @Override
                            public Integer handle(ResultSet r, Query query1, QueryContext context) throws Exception {
                                int selectedCnt = 0;
                                while(r.next())
                                    selectedCnt++;
                                return selectedCnt;
                            }
                        });
            }
        });
        LOG.debug("Selected count: " + reslt);
        assertNotNull(reslt);
        assertEquals(2, reslt.intValue());

    }

    @Test(enabled = false)
    public void sortTestORA() {

        String reslt = QueryContext.call(new UnsafeFunction<QueryContext, String, Throwable>() {

            @Override
            public String apply(QueryContext context) throws Throwable {
                Connection connection = context.getConnection();

                final Query query = new Query("select a.* from EMP a where a.deptno=/*$deptno, type=numeric {*/20/*}*/");
                query.setParam("DEPTNO", 20, null);
                query.setSort(
                    new Sort()
                        .add("job", Sort.Direction.DESC)
                        .add("empno", Sort.Direction.ASC)
                );

                return QueryHelper.query(
                        query,
                        new ResultReceiver<ResultSet, String>() {
                            @Override
                            public String handle(ResultSet r, Query query1, QueryContext context) throws Exception {
                                int selectedCnt = 0;
                                String firstENAME = null;
                                while(r.next()){
                                    if(Strings.empty(firstENAME))
                                        firstENAME = r.getString(r.findColumn("ENAME"));
                                    selectedCnt++;
                                }
                                return firstENAME + "-" + selectedCnt;
                            }
                        });
            }
        });
        LOG.debug("Result: " + reslt);
        assertNotNull(reslt);
        assertEquals("JONES-5", reslt);

    }

}

