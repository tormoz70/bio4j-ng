package ru.bio4j.ng.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.converter.DateTimeParser;
import ru.bio4j.ng.commons.types.Prop;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.Sort;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                "   - type : UNDEFINED;\n" +
                "   - name : null;\n" +
                "   - created : null;\n" +
                "   - volume : null;\n" +
                "   - packets : null;\n" +
                "   - ex : null;\n" +
                "   - err : null;\n" +
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
        Matcher m = Regexs.match(txt, "(?<=ORA-2\\d{4}:).+(?=\\nORA-\\d{5}:)", Pattern.CASE_INSENSITIVE+Pattern.MULTILINE+Pattern.DOTALL);
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

    public static class TestConfig1 {
        @Prop(name = "pool.name")
        private String poolName;

        public String getPoolName() {
            return poolName;
        }

        public void setPoolName(String poolName) {
            this.poolName = poolName;
        }
    }
    public static class TestConfig2 {
        @Prop(name = "pool.name")
        private String poolName;

        public String getPoolName() {
            return poolName;
        }

        public void setPoolName(String poolName) {
            this.poolName = poolName;
        }
    }

    @Test
    public void applyValuesToBeanTest1() throws Exception {
        final String expctd = "ru.bio4j.ng.doa.connectionPool.main";
        Dictionary d = new Hashtable();
        d.put("pool.name", expctd);
        TestConfig1 c = new TestConfig1();
        Utl.applyValuesToBeanFromDict(d, c);
        Assert.assertEquals(c.getPoolName(), expctd);
    }
    @Test
    public void applyValuesToBeanTest2() throws Exception {
        TestConfig1 c1 = new TestConfig1();
        c1.setPoolName("ru.bio4j.ng.doa.connectionPool.main");
        TestConfig2 c2 = new TestConfig2();
        Utl.applyValuesToBeanFromBean(c1, c2);
        Assert.assertEquals(c2.getPoolName(), c1.getPoolName());
    }

    @Test
    public void applyValuesToBeanTest3() throws Exception {
        TestConfig1 c1 = new TestConfig1();
        c1.setPoolName("ru.bio4j.ng.doa.connectionPool.main");
        ImsConfig c2 = new ImsConfig();
        Utl.applyValuesToBeanFromBean(c1, c2);
        Assert.assertEquals(c2.getPoolName(), c1.getPoolName());
    }

    @Test
    public void applyValuesToBeanTest4() throws Exception {
        Sort s1 = new Sort();
        s1.setFieldName("f1");
        s1.setDirection(Sort.Direction.DESC);
        Sort s2 = (Sort)Utl.cloneBean(s1);
        Assert.assertEquals(s2.getFieldName(), s1.getFieldName());
    }

    @Test(enabled = false)
    public void getTypeParamsTest() throws Exception {
        TestGeneric<TestGenericBean> t = new TestGeneric<>();
        Assert.assertEquals(t.getparamType(), TestGenericBean.class);
    }

    @Test(enabled = true)
    public void arrayCopyTest() throws Exception {
        String[] a = {"1", "2"};
        Object b = Utl.arrayCopyOf(a);
        Assert.assertEquals(((Object[])b).length, a.length);
        Assert.assertEquals(((Object[])b)[0], a[0]);
        Assert.assertEquals(((Object[])b)[1], a[1]);
    }

    @Test(enabled = true)
    public void nullIntegerToIntTest() throws Exception {
        Integer t = null;
        int t1 = Utl.nvl(t, 0);
        Assert.assertEquals(0, t1);
    }

    @Test(enabled = false)
    public void checkSum() throws Exception {
        final String chksum = "9F993B28F29B53178DA58EC2781A9506";
        final String chksumAct = MD5Checksum.checkSum("d:\\downloads\\ibexpert.rar");
        Assert.assertEquals(chksumAct.toUpperCase(), chksum);
    }

    @Test(enabled = true)
    public void fileExtTest() throws Exception {
        final String fileName = "d:\\downloads\\ibexpert.rar";
        Assert.assertEquals(Utl.fileExt(fileName), "rar");
    }

    @Test(enabled = true)
    public void dictionaryInfoTest() throws Exception {
        Dictionary d = new Hashtable();
        d.put("1", "Chocolate");
        d.put("2", "Cocoa");
        d.put("5", "Coffee");
        String rslt = Utl.dictionaryInfo(d, "testDict", "\t");
        Assert.assertEquals(rslt, "\ttestDict {\n" +
                "\t - 5 : Coffee;\n" +
                "\t - 2 : Cocoa;\n" +
                "\t - 1 : Chocolate;\n" +
                "\t}");
    }

    @Test(enabled = true)
    public void confIsEmptyTest() throws Exception {
        Dictionary d = new Hashtable();
        d.put("component", "Chocolate");
        Boolean rslt = Utl.confIsEmpty(d);
        Assert.assertTrue(rslt);

    }

    @Test(enabled = true)
    public void fileWithoutExtTest() throws Exception {
        Assert.assertEquals(Utl.fileWithoutExt("d:/qwe.asd/fgh.fgh.txt"), "d:/qwe.asd/fgh.fgh");
    }

    @Test(enabled = true)
    public void extractBioPathTest() throws Exception {
        Assert.assertEquals(Utl.extractBioPath("qwe.asd.fgh.fgh"), "/qwe/asd/fgh/fgh");
        Assert.assertEquals(Utl.extractBioParentPath("qwe.asd.fgh.fgh"), "/qwe/asd/fgh");
        Assert.assertEquals(Utl.extractBioParentPath("qwe"), "/");
    }

//    @Test(enabled = true)
//    public void encode2xmlTest() throws Exception {
//        try(OutputStream s = new FileOutputStream("d:\\tmp\\test-encode2xml.xml")) {
//            XLRCfg testBox = new XLRCfg();
//            testBox.setBioCode("Test-Box");
//
//            XLRCfg.DataSource ds = new XLRCfg.DataSource();
//            XLRCfg.ColumnDefinition cd = new XLRCfg.ColumnDefinition();
//
//            cd.setFieldName("ID");
//            cd.setTitle("Идентификатор");
//            cd.setFormat("0");
//
//            ds.setRangeName("mRng");
//            ds.getColumnDefinitions().add(cd);
//            ds.setSql("select * from dual");
//
//            testBox.setTitle("Экспорт ИО");
//            testBox.getDss().add(ds);
//
//            Utl.encode2xml(testBox, s);
//        }
//    }

    @Test
    public void getPath() {
        for (int i = 0; i < 1000; i++) {
            UUID uuid = UUID.randomUUID();
            String hex = uuid.toString().replace("-", "").toLowerCase();
            Assert.assertTrue(hex.length() == 32 /* UUID is 128 bit (32 hex byte)! */, "Bad UUID format");
            System.out.println(hex.substring(0, 4) + "/" + hex.substring(4, 8) + "/" + hex.substring(8, 12));
        }
        // normal UUID looks like 'd3761577-a7f1-41ae-b2a3-adbb1ae987a4' in any letters case
        // therefore we need to remove '-' and convert to lowercase

    }

    @Test
    public void storeStringTest() throws IOException {
        Utl.storeString("Тест", "d:\\storeStringTest.txt");
        Assert.assertTrue(true);
    }

    @Test
    public void openFileTest() throws IOException {
        Utl.storeString("Тест", "d:\\storeStringTest.txt");
        InputStream ins = Utl.openFile("d:\\storeStringTest.txt");
        String rslt = Utl.readStream(ins);
        Assert.assertTrue("Тест".equals(rslt.trim()));
    }

    @Test
    public void beanToParamsTest() throws Exception {
        TObject o = new TObject(){{
            tobject_id = null;
            factory_org_id = 123L;
            tobjtype_id = 345L;
            autor_person_uid = "qwe";
            filesuid = "asd";
            aname = "dfg";
            adesc = null;
            prodplace = "fgh";

        }};
        List<Param> p = Utl.beanToParams(o);
        Assert.assertTrue(true);
    }

    @Test
    public void BooleanTest() throws Exception {
        Boolean b = null;
        Assert.assertTrue(b == null);
    }
}
