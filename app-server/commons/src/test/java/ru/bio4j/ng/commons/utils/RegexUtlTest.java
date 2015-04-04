package ru.bio4j.ng.commons.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: ayrat
 * Date: 29.11.13
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */
public class RegexUtlTest {
    @Test
    public void testMatch() throws Exception {
        Matcher m = Regexs.match("select :w1, :w2, :w3 from dual", "(?<=:)\\b[\\w\\#\\$]+", Pattern.CASE_INSENSITIVE);
        StringBuilder paramsList = new StringBuilder();
        while(m.find())
            paramsList.append(m.group()+";");
        Assert.assertEquals(paramsList.toString(), "w1;w2;w3;");

    }

    @Test
    public void testFind() throws Exception {

    }

    @Test
    public void testPos() throws Exception {

    }

    @Test
    public void testReplace0() throws Exception {
        String sql = "select 'sdf' as f1 from dual";
        sql = Regexs.replace(sql, "(['])(.*?)\\1", "", Pattern.CASE_INSENSITIVE);
        System.out.println(sql);
        Assert.assertEquals(sql, "select  as f1 from dual");
    }


    @Test
    public void testReplace1() throws Exception {
        final String query_ph = "${QUERY_PLACEHOLDER}";
        final String templ = "SELECT COUNT(1) ttlCnt$wrpr\n" +
                "FROM ( ${QUERY_PLACEHOLDER} )";
        final String sql = "select 'sdf' as f1 from dual";
        String preparedSQL = Regexs.replace(templ, query_ph, sql, Pattern.MULTILINE+Pattern.LITERAL);
        System.out.println(preparedSQL);
        Assert.assertTrue(true);
    }

    @Test
    public void testGetPos() throws Exception {
        final String r = "\\:\\b[\\w\\#\\$]+";
        final String line = "asdklj aslkdjn  :asd asdasd yuuuc\n" +
                "asd :qwe asd poibvk; ';lksdflk :asd   l;sdkjflkj " +
                "asdf;lkv sd;flk :qwe  sdgfsdfg";
        Pattern pattern = Pattern.compile(r, Pattern.MULTILINE+Pattern.CASE_INSENSITIVE);
        Matcher m = pattern.matcher(line);
        int indx = 0;
        StringBuffer sb = new StringBuffer(line.length());
        while (m.find()) {
            System.out.println(indx+" - "+m.group());
            m.appendReplacement(sb, "?");
            indx++;
        }
        System.out.println(sb.toString());

        Assert.assertTrue(true);
    }

    @Test
    public void testReplace() throws Exception {
        final String sql = "\n" +
                "    SELECT * FROM (\n" +
                "        SELECT pgng$wrpr0.*, row_number() over (order by aterminalid) rnum$pgng\n" +
                "          FROM ( SELECT\n" +
                "  aterminalid,\n" +
                "  aterminalkey,\n" +
                "  ip4addr,\n" +
                "  tstate,\n" +
                "  aname,\n" +
                "  anote,\n" +
                "  addr,\n" +
                "  latitude,\n" +
                "  longitude,\n" +
                "  isdeleted\n" +
                "FROM aterminal\n" +
                "WHERE not isdeleted ) as pgng$wrpr0\n" +
                "    ) pgng$wrpr WHERE (pgng$wrpr.rnum$pgng > :paging$offset) AND (pgng$wrpr.rnum$pgng <= :paging$last)\n" +
                "    ";
        final String sql1 = "\n" +
                "    SELECT * FROM (\n" +
                "        SELECT pgng$wrpr0.*, row_number() over (order by aterminalid) rnum$pgng\n" +
                "          FROM ( SELECT\n" +
                "  aterminalid,\n" +
                "  aterminalkey,\n" +
                "  ip4addr,\n" +
                "  tstate,\n" +
                "  aname,\n" +
                "  anote,\n" +
                "  addr,\n" +
                "  latitude,\n" +
                "  longitude,\n" +
                "  isdeleted\n" +
                "FROM aterminal\n" +
                "WHERE not isdeleted ) as pgng$wrpr0\n" +
                "    ) pgng$wrpr WHERE (pgng$wrpr.rnum$pgng > ?) AND (pgng$wrpr.rnum$pgng <= :paging$last)\n" +
                "    ";
        String paramName = "paging$offset";
        String preparedQuery = Regexs.replace(sql, "\\Q:"+paramName+"\\E\\b", "?", Pattern.MULTILINE+Pattern.CASE_INSENSITIVE);
        Assert.assertEquals(preparedQuery, sql1);
    }
}
