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
        Map<String, int[]> indexMap = new HashMap();
        String parsedQuery = NamedParametersStatement.parse("asdasd :ert SELECT :method_name \"sdf :sdf\" pg_get_function_identity_" +
                " asdasdasd /* :dfgdfgdg*/ fghfghfh\n"+
                " asdasdasd -- :dfgdfgdg\n"+
                "arguments(:method_name::regproc) :ert as rslt :method_name1", indexMap);
        Assert.assertEquals(parsedQuery, "asdasd ? SELECT ? \"sdf :sdf\" pg_get_function_identity_ asdasdasd /* :dfgdfgdg*/ fghfghfh\n" +
                " asdasdasd -- :dfgdfgdg\n" +
                "arguments(?::regproc) ? as rslt ?");
        Assert.assertEquals(indexMap.get("ert")[0], 1);
        Assert.assertEquals(indexMap.get("ert")[1], 4);
        Assert.assertEquals(indexMap.get("method_name")[0], 2);
        Assert.assertEquals(indexMap.get("method_name")[1], 3);
    }


}
