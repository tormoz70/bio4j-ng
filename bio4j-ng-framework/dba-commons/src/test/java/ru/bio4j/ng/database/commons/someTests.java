package ru.bio4j.ng.database.commons;

import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.ng.database.api.SQLNamedParametersStatement;

import java.util.HashMap;
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
        String parsedQuery = DbNamedParametersStatement.parse("asdasd :ert SELECT :method_name \"sdf :sdf\" pg_get_function_identity_" +
                " asdasdasd /* :dfgdfgdg*/ fghfghfh\n" +
                " asdasdasd -- :dfgdfgdg\n" +
                "arguments(:method_name::regproc) :ert as rslt :method_name1", null, indexMap);
        Assert.assertEquals(parsedQuery, "asdasd ? SELECT ? \"sdf :sdf\" pg_get_function_identity_ asdasdasd /* :dfgdfgdg*/ fghfghfh\n" +
                " asdasdasd -- :dfgdfgdg\n" +
                "arguments(?::regproc) ? as rslt ?");
        Assert.assertEquals(indexMap.get("ert")[0], 1);
        Assert.assertEquals(indexMap.get("ert")[1], 4);
        Assert.assertEquals(indexMap.get("method_name")[0], 2);
        Assert.assertEquals(indexMap.get("method_name")[1], 3);
    }

    @Test
    public void nameOfByteArray() throws Exception {
        Class<?> t = Byte[].class;
        String className = t.getName();
        Class<?> type = getClass().getClassLoader().loadClass("java.lang.Byte[]");
        System.out.println(type.getCanonicalName());
    }

}
