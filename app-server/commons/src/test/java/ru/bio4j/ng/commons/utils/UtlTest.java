package ru.bio4j.ng.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.converter.DateTimeParser;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.regex.Matcher;

public class UtlTest {
    private final static Logger LOG = LoggerFactory.getLogger(UtlTest.class);

    @Test
    public void getClassNamesFromPackageTest() {
        LOG.debug("Debug logger test!");
    //	  ArrayList<String> clss = getClassNamesFromPackage("");
    //	  Assert.
    }

    @Test
    public void findAnnotationTest() {
        AnnotationTest annot = Utl.findAnnotation(AnnotationTest.class, AnnotetedClass.class);
        if(annot != null)
            Assert.assertEquals(annot.path(), "/test_path");
        else
            Assert.fail();
    }

    @Test
    public void typesIsSameTest() {
        Assert.assertTrue(Utl.typesIsSame(DateTimeParser.class, DateTimeParser.class));
    }

    @Test
    public void buildBeanStateInfoTest() {
        TBox box = new TBox();
        String rslt = "  ru.bio4j.ng.commons.utils.TBox {\n" +
                "   - name : null;\n" +
                "   - created : null;\n" +
                "   - volume : null;\n" +
                "   - packets : null;\n" +
                "   - ex : null;\n" +
                "  }";
        String info = Utl.buildBeanStateInfo(box, null, "  ");
        System.out.println(info);
        Assert.assertEquals(info, rslt);
    }

    @Test(enabled = false)
    public void regexFindTest() {
        String txt = "ORA-20001: Не верное имя или пароль пользователя!\n" +
                "ORA-06512: на  \"GIVCAPI.GACC\", line 316\n" +
                "ORA-06512: на  \"GIVCAPI.GACC\", line 331\n" +
                "ORA-06512: на  line 1";
        Matcher m = Regexs.match(txt, "(?<=ORA-2\\d{4}:).+(?=\\nORA-\\d{5}:)", true, true, true);
        String fnd = m.group();
        System.out.println(fnd);
    }

    public void normalizePathTest() {
        String path = Utl.normalizePath("asd/sdf\\sdf", '\\');
        Assert.assertEquals("asd\\sdf\\sdf\\", path);
        path = Utl.normalizePath("asd/sdf\\sdf");
        Assert.assertEquals("asd\\sdf\\sdf\\", path);
        path = Utl.normalizePath("asd/sdf\\sdf", '/');
        Assert.assertEquals("asd/sdf/sdf/", path);
        path = Utl.normalizePath("asd/sdf\\sdf/", '/');
        Assert.assertEquals("asd/sdf/sdf/", path);
        path = Utl.normalizePath("asd\\sdf/sdf");
        Assert.assertEquals("asd\\sdf\\sdf\\", path);
        path = Utl.normalizePath("D:\\jdev\\workspace\\bio4j-ng\\as-distribution\\target\\as-distribution-2.0-SNAPSHOT\\as-distribution-2.0-SNAPSHOT/content");
        Assert.assertEquals("D:\\jdev\\workspace\\bio4j-ng\\as-distribution\\target\\as-distribution-2.0-SNAPSHOT\\as-distribution-2.0-SNAPSHOT\\content\\", path);

    }

    public static class TestConfig {
        private String poolName;

        public String getPoolName() {
            return poolName;
        }

        public void setPoolName(String poolName) {
            this.poolName = poolName;
        }
    }

    @Test
    public void applyValuesToBeanTest() throws Exception {
        final String expctd = "ru.bio4j.ng.doa.connectionPool.main";
        Dictionary d = new Hashtable();
        d.put("poolName", expctd);
        TestConfig c = new TestConfig();
        Utl.applyValuesToBean(d, c);
        Assert.assertEquals(c.getPoolName(), expctd);
    }

}
