package ru.bio4j.ng.database.commons;

import org.testng.Assert;
import org.testng.annotations.Test;
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
//        Assert.assertEquals(parsedQuery, "asdasd ? SELECT ? \"sdf :sdf\" pg_get_function_identity_ asdasdasd /* :dfgdfgdg*/ fghfghfh\n" +
//                " asdasdasd -- :dfgdfgdg\n" +
//                "arguments(?::regproc) ? as rslt ?");

        Assert.assertEquals(parsedQuery, "asdasd ? SELECT ? \"sdf ?\" pg_get_function_identity_ asdasdasd /* :dfgdfgdg*/ fghfghfh\n" +
                " asdasdasd -- :dfgdfgdg\n" +
                "arguments(?::regproc) ? as rslt ?");

        Assert.assertEquals(indexMap.get("ert")[0], 1);
        Assert.assertEquals(indexMap.get("ert")[1], 5);
        Assert.assertEquals(indexMap.get("method_name")[0], 2);
        Assert.assertEquals(indexMap.get("method_name")[1], 4);
    }

    @Test
    public void nameOfByteArray() throws Exception {
        Class<?> t = Byte[].class;
        String className = t.getName();
        Class<?> type = getClass().getClassLoader().loadClass("java.lang.String");
        System.out.println(type.getCanonicalName());
    }

    @Test
    public void cutParamPrefix() throws Exception {
        String r = DbUtils.cutParamPrefix("okeeper_id");
        Assert.assertEquals(r, "okeeper_id");
        r = DbUtils.cutParamPrefix("p_okeeper_id");
        Assert.assertEquals(r, "okeeper_id");
        r = DbUtils.cutParamPrefix("v_okeeper_id");
        Assert.assertEquals(r, "okeeper_id");
        r = DbUtils.cutParamPrefix("P_OKEEPER_ID");
        Assert.assertEquals(r, "OKEEPER_ID");
        r = DbUtils.cutParamPrefix("V_OKEEPER_ID");
        Assert.assertEquals(r, "OKEEPER_ID");
    }


}
