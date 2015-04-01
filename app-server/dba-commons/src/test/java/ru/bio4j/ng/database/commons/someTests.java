package ru.bio4j.ng.database.commons;

import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.utils.Sqls;
import ru.bio4j.ng.database.api.NamedParametersStatement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ayrat
 * Date: 02.12.13
 * Time: 21:09
 * To change this template use File | Settings | File Templates.
 */
public class someTests {
    @Test
    public void namedStatementTest() throws Exception {
        Map indexMap = new HashMap();
        String parsedQuery = NamedParametersStatement.parse("SELECT pg_get_function_identity_arguments(:method_name::regproc) as rslt", indexMap);
        Assert.assertEquals(parsedQuery, "SELECT pg_get_function_identity_arguments(?::regproc) as rslt");
    }


}
