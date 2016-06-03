package ru.bio4j.ng.database.oracle;

import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.oracle.impl.OraWrapperInterpreter;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;

import java.io.InputStream;

public class OraWrapperInterpreterTest {

    @Test(enabled = true)
    public void filterToSQLTest() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("filter.json");
        String json = Utl.readStream(inputStream);
        Filter filter = Jsons.decode(json, Filter.class);
        OraWrapperInterpreter filterWrapper = new OraWrapperInterpreter();
        String sql = filterWrapper.filterToSQL("fff", filter);
        Assert.assertNotNull(sql);
    }
}
