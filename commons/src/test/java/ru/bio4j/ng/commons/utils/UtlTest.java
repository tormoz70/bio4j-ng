package ru.bio4j.ng.commons.utils;

import flexjson.ObjectBinder;
import flexjson.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.converter.DateTimeParser;
import ru.bio4j.ng.model.transport.FileSpec;
import ru.bio4j.ng.model.transport.Prop;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.Sort;
import ru.bio4j.ng.model.transport.jstore.filter.Filter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtlTest {
    private final static Logger LOG = LoggerFactory.getLogger(UtlTest.class);

    @Test
    public void getClassNamesFromPackageTest() {
        if(LOG.isDebugEnabled())
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

    @Test(enabled = true)
    public void regexFindTest() {
        String txt = "ORA-20001: Не верное имя или пароль пользователя!\n" +
                "ORA-06512: на  \"GIVCAPI.GACC\", line 316\n" +
                "ORA-06512: на  \"GIVCAPI.GACC\", line 331\n" +
                "ORA-06512: на  line 1";
        System.out.println(txt);
        System.out.println("-----------------------------------------------------------------------------------------------------------------");
        Matcher m = Regexs.match(txt, "(?<=ORA-2\\d{4}:).+", Pattern.CASE_INSENSITIVE);
        if(m.find()) {
            String fnd = m.group();
            System.out.println(fnd);
        }
    }

    @Test(enabled = true)
    public void normalizePathTest() {
        String path = Utl.normalizePath("asd/sdf\\sdf", '\\');
        Assert.assertEquals("asd\\sdf\\sdf\\", path);
        path = Utl.normalizePath("asd/sdf/sdf", '\\');
        Assert.assertEquals("asd\\sdf\\sdf\\", path);
        path = Utl.normalizePath("asd/sdf\\sdf");
        Assert.assertEquals("asd\\sdf\\sdf\\", path);
        path = Utl.normalizePath("asd/sdf\\sdf", '/');
        Assert.assertEquals("asd/sdf/sdf/", path);
        path = Utl.normalizePath("asd/sdf/sdf/", '\\');
        Assert.assertEquals("asd\\sdf\\sdf\\", path);
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
        Assert.assertEquals(Utl.fileNameExt(fileName), "rar");
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
        Assert.assertEquals(Utl.fileNameWithoutExt("d:/qwe.asd/fgh.fgh.txt"), "d:/qwe.asd/fgh.fgh");
    }

    @Test(enabled = true)
    public void tmpFileNameTest() throws Exception {
        String tmpPath = "./bio-tmp";
        String fileName = "asd-qwe_345.xls";
        String rslt = Utl.generateTmpFileName(tmpPath, fileName);

        String randomPart = Regexs.find(rslt, "\\$\\((\\w|\\d)+\\)", 0);
        String expected = Utl.normalizePath(tmpPath) + Utl.fileNameWithoutExt(fileName) + "-" + randomPart + "." + Utl.fileNameExt(fileName);

        Assert.assertEquals(rslt, expected);
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

    @Test
    public void roundTest() throws Exception {
        double b = 123.456123;
        double b1 = Utl.round(b, 2);
        Assert.assertTrue(b1 == 123.46);
    }

    @Test
    public void parsLoginTest() throws Exception {
        LoginRec loginRec = Utl.parsLogin("qwe/asd");
        Assert.assertEquals(loginRec.getUsername(), "qwe");
        Assert.assertEquals(loginRec.getPassword(), "asd");
    }
    @Test
    public void restoreSimpleSortTest() throws Exception {
        List<Sort> sort = Utl.restoreSimpleSort("+rentName,-rentDate");
        Assert.assertEquals(sort.get(0).getFieldName(), "rentName");
        Assert.assertEquals(sort.get(0).getDirection(), Sort.Direction.ASC);
        Assert.assertEquals(sort.get(1).getFieldName(), "rentDate");
        Assert.assertEquals(sort.get(1).getDirection(), Sort.Direction.DESC);
    }
    @Test
    public void restoreSimpleFilterTest() throws Exception {
        Filter filter = Utl.restoreSimpleFilter("{rentName:\"something1\"|\"something2\",rentDate:\"val3\"}");
        Assert.assertEquals(filter.getChildren().size(), 1);
        Assert.assertEquals(filter.getChildren().get(0).getName(), "and");
        Assert.assertEquals(filter.getChildren().get(0).getChildren().size(), 2);
        Assert.assertEquals(filter.getChildren().get(0).getChildren().get(0).getName(), "or");
        Assert.assertEquals(filter.getChildren().get(0).getChildren().get(1).getName(), "contains");
    }

    @Test
    public void fileNameWithoutExtTest() throws Exception {
        String r = Utl.fileNameWithoutExt("e:\\arch\\20171204\\57\\posted000\\ekb_57_20171021_100012002(31fc455a).xml.pattrs");
        Assert.assertEquals(r,"e:\\arch\\20171204\\57\\posted000\\ekb_57_20171021_100012002(31fc455a).xml");
    }

    @Test
    public void readFileTest() throws Exception {
        long fs = Utl.fileSize("e:\\arch\\20171204\\57\\posted000\\ekb_57_20171021_100012002(31fc455a).xml");
        Assert.assertEquals(fs, 3601);
    }

    @Test
    public void JsonsDecodeTest() throws Exception {
        String json = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("1e1a01b6c2bc4b81b76a08cc16afe951.attrs"));
        FileSpec fileSpec = Jsons.decode(json, new ObjectFactory(){
            @Override
            public Object instantiate(ObjectBinder objectBinder, Object o, Type type, Class aClass) throws Exception {
                FileSpec rslt = new FileSpec();
                Utl.applyValuesToBeanFromHashMap((HashMap) o, rslt, null, "innerFiles");
                Object innerFilesVal = ((HashMap) o).get("innerFiles");
                if(innerFilesVal != null) {
                    List<HashMap> innerFiles = (List<HashMap>)innerFilesVal;
                    rslt.setInnerFiles(new ArrayList<>());
                    for(HashMap hf : innerFiles){
                        FileSpec ip = new FileSpec();
                        Utl.applyValuesToBeanFromHashMap(hf, ip, null, "innerFiles");
                        rslt.getInnerFiles().add(ip);
                    }
                }
                return rslt;
            }
        });

        Assert.assertTrue(fileSpec != null);
    }

    @Test
    public void readwritefileTest() throws IOException {
        String fname = "d:/test-list-store.lst";
        List<String> lst1 = new ArrayList<>();
        lst1.add("фыв");
        lst1.add("fdgh");
        lst1.add("123");
        Utl.storeListToFile(lst1, fname);
        List<String> lst2 = Utl.readFileAsList(fname);
        Assert.assertEquals(lst1.get(0), lst2.get(0));
        Assert.assertEquals(lst1.get(1), lst2.get(1));
        Assert.assertEquals(lst1.get(2), lst2.get(2));
    }

}
