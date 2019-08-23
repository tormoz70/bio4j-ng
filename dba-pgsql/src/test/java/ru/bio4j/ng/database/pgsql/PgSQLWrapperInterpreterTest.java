package ru.bio4j.ng.database.pgsql;

import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.pgsql.impl.PgSQLWrapperInterpreter;
import ru.bio4j.ng.model.transport.FilterAndSorter;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;

import java.io.InputStream;

@Test(enabled = false)
public class PgSQLWrapperInterpreterTest {

//    @Test(enabled = false)
//    public void filterToSQLTest() throws Exception {
//        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("filter.json");
//        String json = Utl.readStream(inputStream);
//        Filter filter = Jsons.decode(json, Filter.class);
//        OraWrapperInterpreter filterWrapper = new OraWrapperInterpreter();
//        String sql = filterWrapper.filterToSQL("fff", filter);
//        Assert.assertNotNull(sql);
//    }


//    @Test(enabled = true)
//    public void filter1ToSQLTest() throws Exception {
//        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("filter1.json");
//        String json = Utl.readStream(inputStream);
//        Filter filter = Jsons.decodeFilter(json);
//        OraWrapperInterpreter filterWrapper = new OraWrapperInterpreter();
//        String sql = filterWrapper.filterToSQL("fff", (Filter)filter);
//        Assert.assertNotNull(sql);
//    }
//    @Test(enabled = true)
    public void filterAndSorterToSQLTest() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("filter2.json");
        String json = Utl.readStream(inputStream);
        FilterAndSorter fs = Jsons.decodeFilterAndSorter(json);
        PgSQLWrapperInterpreter filterWrapper = new PgSQLWrapperInterpreter();
        String sql = filterWrapper.filterToSQL("fff", (Filter)fs.getFilter(), null);
        Assert.assertNotNull(sql);
    }

//    @Test(enabled = true)
    public void filterAndSorterToSQLTest1() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("filter3.json");
        String json = Utl.readStream(inputStream);
        FilterAndSorter fs = Jsons.decodeFilterAndSorter(json);
        PgSQLWrapperInterpreter filterWrapper = new PgSQLWrapperInterpreter();
        String sql = filterWrapper.filterToSQL("fff", (Filter)fs.getFilter(), null);
        Assert.assertNotNull(sql);
    }

//    @Test(enabled = true)
    public void filterAndSorterToSQLTest2() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("filter4.json");
        String json = Utl.readStream(inputStream);
        FilterAndSorter fs = Jsons.decodeFilterAndSorter(json);
        PgSQLWrapperInterpreter filterWrapper = new PgSQLWrapperInterpreter();
        String sql = filterWrapper.filterToSQL("fff", (Filter)fs.getFilter(), null);
        Assert.assertNull(sql);
    }

//    @Test(enabled = true)
    public void filterAndSorterToSQLTest5() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("filter5.json");
        String json = Utl.readStream(inputStream);
        FilterAndSorter fs = Jsons.decodeFilterAndSorter(json);
        PgSQLWrapperInterpreter filterWrapper = new PgSQLWrapperInterpreter();
        String sql = filterWrapper.filterToSQL("fff", (Filter)fs.getFilter(), null);
        System.out.println(sql);
        Assert.assertNotNull(sql);
    }

}
