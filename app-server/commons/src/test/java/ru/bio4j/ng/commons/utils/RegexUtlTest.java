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
    public void testReplace() throws Exception {
        String sql = "select 'sdf' as f1 from dual";
        sql = Regexs.replace(sql, "(['])(.*?)\\1", "", Pattern.CASE_INSENSITIVE);
        System.out.println(sql);
        Assert.assertEquals(sql, "select  as f1 from dual");
    }
}
