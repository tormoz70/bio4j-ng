package ru.bio4j.ng.database.oracle;

import flexjson.JSONDeserializer;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.database.oracle.impl.OraWrapperInterpreter;
import ru.bio4j.ng.model.transport.jstore.filter.And;
import ru.bio4j.ng.model.transport.jstore.filter.Expression;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;
import ru.bio4j.ng.model.transport.jstore.filter.FilterBuilder;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.*;

public class OraWrapperInterpreterTest {

    @Test(enabled = false)
    public void filterToSQLTest() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("filter.json");
        String json = Utl.readStream(inputStream);
        Filter filter = Jsons.decode(json, Filter.class);
        OraWrapperInterpreter filterWrapper = new OraWrapperInterpreter();
        String sql = filterWrapper.filterToSQL("fff", filter);
        Assert.assertNotNull(sql);
    }


    @Test(enabled = true)
    public void filter1ToSQLTest() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("filter1.json");
        String json = Utl.readStream(inputStream);
        Filter filter = Jsons.decodeFilter(json);
        OraWrapperInterpreter filterWrapper = new OraWrapperInterpreter();
        String sql = filterWrapper.filterToSQL("fff", (Filter)filter);
        Assert.assertNotNull(sql);
    }
}
