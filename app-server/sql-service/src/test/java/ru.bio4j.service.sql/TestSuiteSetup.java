package ru.bio4j.service.sql;

import org.hsqldb.jdbc.JDBCDataSource;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import ru.bio4j.service.sql.query.ConnectionFactoryImpl;


public class TestSuiteSetup {

    @BeforeTest
    public void prepare() {
        JDBCDataSource ds = new JDBCDataSource();
        ds.setUrl("jdbc:hsqldb:mem:memdb");
        ds.setUser("SA");
        ds.setPassword("");
        QueryContext.create(new ConnectionFactoryImpl(ds));
       /*
        try {
            OracleDataSource ds = new OracleDataSource();
            ds.setURL("jdbc:oracle:thin:scott/123@//giac-ora-dev:1521/MICEXDB");
            QueryContext.create(new ConnectionFactoryImpl(ds));
        } catch (SQLException e) {
           e.printStackTrace();
        }
        */
    }

    @AfterTest
    public void clean() {
        QueryContext.get().close();
        QueryContext.remove();
    }
}
