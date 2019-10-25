package ru.bio4j.ng.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.converter.DateTimeParser;
import ru.bio4j.ng.commons.converter.Types;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.StoreData;
import ru.bio4j.ng.model.transport.jstore.StoreRow;

import java.text.SimpleDateFormat;
import java.util.*;

@Test
public class JecksonsTest {
    private static final Logger LOG = LoggerFactory.getLogger(JecksonsTest.class);

	private final TBox testBox = new TBox();

	@BeforeClass
	private void setUp() {
//        TimeZone.setDefault(TimeZone.getTimeZone("GMT+03:00"));
        this.testBox.setType(MetaType.INTEGER);
		this.testBox.setName("Test-Box");
		this.testBox.setCreated(Types.parse("2012.12.20-15:11:24", "yyyy.MM.dd-HH:mm:ss"));
		this.testBox.setVolume(123.05);
		this.testBox.setPackets(new TPacket[]{new TPacket()});
		this.testBox.getPackets()[0].setName("packet-0");
		this.testBox.getPackets()[0].setVolume(100.10);
		this.testBox.getPackets()[0].setApples(new TApple[]{new TApple(), new TApple()});
		this.testBox.getPackets()[0].getApples()[0].setName("apple-0-0");
		this.testBox.getPackets()[0].getApples()[0].setWheight(10.100);
		this.testBox.getPackets()[0].getApples()[1].setName("apple-0-1");
		this.testBox.getPackets()[0].getApples()[1].setWheight(10.200);
		this.testBox.setEx(new Exception("FTW TestException"));
        this.testBox.setErr(new BioError("BIO TestError"));
	}

	@Test(enabled = true)
	public void aencode() throws Exception {
        TBox testBox = new TBox();
        testBox.setName("Test-Box");
		String expected =
		 "{\"type\":\"UNDEFINED\",\"name\":\"Test-Box\",\"volume\":null,\"packets\":null,\"ex\":null,\"err\":null,\"crd\":null}";
		String testJson = Jecksons.getInstance().encode(testBox);
		System.out.println(testJson);
		Assert.assertEquals(testJson, expected);
	}

	@Test(enabled = true)
	public void bdecode() throws Exception {
		String testJson = Jecksons.getInstance().encode(this.testBox);
        Assert.assertTrue(testJson.indexOf("stackTrace") == -1);
        Assert.assertTrue(testJson.indexOf("cause") == -1);
        Assert.assertTrue(testJson.indexOf("rootCause") == -1);
        Assert.assertTrue(testJson.indexOf("localizedMessage") == -1);
        Assert.assertTrue(testJson.indexOf("suppressed") == -1);

		TBox restored = Jecksons.getInstance().decode(testJson, TBox.class);
		System.out.println("restored: " + restored);
		Assert.assertEquals(this.testBox.getName(), restored.getName());
		Assert.assertEquals(this.testBox.getCreated(), restored.getCreated());
		Assert.assertEquals(this.testBox.getVolume(), restored.getVolume());
		Assert.assertEquals(this.testBox.getPackets()[0].getName(), restored.getPackets()[0].getName());
		Assert.assertEquals(this.testBox.getPackets()[0].getVolume(), restored.getPackets()[0].getVolume());
		Assert.assertEquals(this.testBox.getPackets()[0].getApples()[0].getName(), restored.getPackets()[0].getApples()[0].getName());
		Assert.assertEquals(this.testBox.getPackets()[0].getApples()[0].getWheight(), restored.getPackets()[0].getApples()[0].getWheight());
		Assert.assertEquals(this.testBox.getPackets()[0].getApples()[1].getName(), restored.getPackets()[0].getApples()[1].getName());
		Assert.assertEquals(this.testBox.getPackets()[0].getApples()[1].getWheight(), restored.getPackets()[0].getApples()[1].getWheight());
        Assert.assertEquals(restored.getEx().getMessage(), this.testBox.getEx().getMessage());
        Assert.assertEquals(restored.getErr().getMessage(), this.testBox.getErr().getMessage());
	}


    // -Duser.timezone=GMT+3
    @Test(enabled = true)
    public void bdecode2() throws Exception {
        if(LOG.isDebugEnabled())
            LOG.debug("test:");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd'T'HH:mm:ss.SSS");
//        format1.setTimeZone(TimeZone.getTimeZone("GMT+03:00"));
        Date d = format1.parse("1970.03.02T18:43:56.555");
        if(LOG.isDebugEnabled())
            LOG.debug("d:{}", d);
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d);
        if(LOG.isDebugEnabled())
            LOG.debug("c1:{}, {}", c1.getTime(), c1.getTimeZone());
        Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:00"));
        c2.setTime(d);
        if(LOG.isDebugEnabled())
            LOG.debug("c2:{}, {}", c2.getTime(), c2.getTimeZone());
    }

    @Test(enabled = true)
    public void bencode6() throws Exception {
        StoreData data = new StoreData();
        List<StoreRow> rows = new ArrayList<>();
        StoreRow row = new StoreRow();
        row.setData(new ABean());
        row.setValue("field1", "qwe");
        row.setValue("field2", 123);
        rows.add(row);
        row = new StoreRow();
        row.setData(new ABean());
        row.setValue("field1", "asd");
        row.setValue("field2", 321);
        rows.add(row);
        data.setRows(rows);

        String json = Jecksons.getInstance().encode(data);

        StoreData dataRe = Jecksons.getInstance().decode(json, StoreData.class);

        Assert.assertNotNull(dataRe);

    }

    private static final String tstPost7 = "{\n" +
            "        \"loocaption\": \"item-finance-type - Вид финансирования\",\n" +
            "        \"aname\": \"Вид финансирования1\",\n" +
            "        \"adesc\": null,\n" +
            "        \"acode\": \"item-finance-type\",\n" +
            "        \"tdictId\": 7\n" +
            "}";
    @Test(enabled = true)
    public void bdecode7() throws Exception {
        //List<ABean> dummy = Jsons1.getInstance().decodeABeans(tstPost7);
        List<ABean> dummy = Jecksons.getInstance().decodeABeans(tstPost7);
        Assert.assertEquals(dummy.size(), 1);
        Assert.assertEquals(dummy.get(0).get("tdictId"), 7);
    }

    @Test(enabled = true)
    public void bdecode71() throws Exception {
        ABean dummy = Jecksons.getInstance().decodeABean(tstPost7);
        Assert.assertEquals(dummy.get("tdictId"), 7);
    }

    private static final String tstPost8 = "[\n" +
            "    {\n" +
            "        \"loocaption\": \"item-finance-type - Вид финансирования\",\n" +
            "        \"aname\": \"Вид финансирования\",\n" +
            "        \"adesc\": null,\n" +
            "        \"acode\": \"item-finance-type\",\n" +
            "        \"tdict_id\": 7\n" +
            "    },\n" +
            "    {\n" +
            "        \"loocaption\": \"geoframe - Географические рамки\",\n" +
            "        \"aname\": \"Географические рамки\",\n" +
            "        \"adesc\": null,\n" +
            "        \"acode\": \"geoframe\",\n" +
            "        \"tdict_id\": 27\n" +
            "    }]\n";
    @Test(enabled = true)
    public void bdecode8() throws Exception {
        //List<ABean> dummy = Jecksons.getInstance().decodeABeans(tstPost8);
        //List<Map<String, Object>> dummy = Jecksons.getInstance().decode(tstPost8, new TypeReference<List<Map<String, Object>>>() {});
        List<ABean> dummy = Jecksons.getInstance().decodeABeans(tstPost8);

        Assert.assertEquals(dummy.size(), 2);
        Assert.assertEquals(dummy.get(1).get("tdict_id"), 27);
    }

    private static final String tstPost9 = "{" +
            "\"trtr1\":{\n" +
            "        \"loocaption\": \"item-finance-type - Вид финансирования\",\n" +
            "        \"aname\": \"Вид финансирования\",\n" +
            "        \"adesc\": null,\n" +
            "        \"acode\": \"item-finance-type\",\n" +
            "        \"tdict_id\": 7,\n" +
            "        \"seld\":[1,2,3]\n" +
            "    },\n" +
            "\"trtr2\":{\n" +
            "        \"loocaption\": \"geoframe - Географические рамки\",\n" +
            "        \"aname\": \"Географические рамки\",\n" +
            "        \"adesc\": null,\n" +
            "        \"acode\": \"geoframe\",\n" +
            "        \"tdict_id\": 27,\n" +
            "        \"seld\":[1,2,3]\n" +
            "    }\n" +
            "}";
    @Test(enabled = true)
    public void bdecode9() throws Exception {
        //List<ABean> dummy = Jsons.decodeABeans("{seld:[1,2,3]}");
        //Map<String, Object> dummy = Jecksons.getInstance().decode(tstPost9, new TypeReference<Map<String, Object>>() {});
        //ABean dummy = Jecksons.getInstance().decodeABean(tstPost9);
        List<ABean> dummy = Jecksons.getInstance().decodeABeans(tstPost9);
        Assert.assertEquals(dummy.size(), 1);
    }

    @Test(enabled = true)
    public void bdecode10() throws Exception {
        TBox dummy = new TBox();
        dummy.setCreated(DateTimeParser.getInstance().pars("2019-09-11T15:43:02"));
        String json = Jecksons.getInstance().encode(dummy);
        Assert.assertTrue(json.endsWith("2019-09-11T15:43:02\"}"));
    }

    private static final String cs_json0001 = "{\"storeId\":\"PrjsInProd\",\"bioParams\":[{\"name\":\"p_company_id\",\"value\":34}],\"totalCount\":999999999,\"offset\":0,\"limit\":-1,\"sort\":null,\"superclass\":{\"superclass\":{\"superclass\":{\"defaultConfig\":{},\"config\":{},\"$className\":\"Ext.Base\",\"isInstance\":true,\"$configPrefixed\":true,\"$configStrict\":true,\"isConfiguring\":false,\"isFirstInstance\":false,\"destroyed\":false,\"clearPropertiesOnDestroy\":true,\"clearPrototypeOnDestroy\":false,\"$links\":null},\"defaultConfig\":{},\"config\":{},\"$configPrefixed\":false,\"rqt\":\"\",\"bioCode\":\"\",\"$className\":\"Bio.request.Request\"},\"defaultConfig\":{},\"config\":{},\"$className\":\"Bio.request.store.Request\"},\"defaultConfig\":{},\"config\":{},\"pageSize\":0,\"$className\":\"Bio.request.store.GetDataSet\",\"$configPrefixed\":false,\"rqt\":\"\",\"bioCode\":\"\",\"isInstance\":true,\"$configStrict\":true,\"isConfiguring\":false,\"isFirstInstance\":false,\"destroyed\":false,\"clearPropertiesOnDestroy\":true,\"clearPrototypeOnDestroy\":false,\"$links\":null}";
    @Test(enabled = true)
    public void bdecode11() throws Exception {
        List<Param> bioParams = Utl.anjsonToParams(cs_json0001);
        Assert.assertNotNull(bioParams);
        Assert.assertNotNull(Paramus.getParam(bioParams, "p_company_id"));
    }


    @Test(enabled = true)
    public void testPolymorphingErrors() throws Exception {
        LoginResult src = LoginResult.Builder.error(new BioError.Login.Unauthorized());
        String json = Jecksons.getInstance().encode(src);
        LoginResult restored = Jecksons.getInstance().decode(json, LoginResult.class);
        Assert.assertTrue(restored.getException() instanceof BioError.Login);
    }

    @Test(enabled = true)
    public void testPolymorphingUser1() throws Exception {
        LoginResult src = LoginResult.Builder.success(new User());
        String json = Jecksons.getInstance().encode(src);
        LoginResult restored = Jecksons.getInstance().decode(json, LoginResult.class);
        Assert.assertTrue(restored.getUser() instanceof User);
    }

    @Test(enabled = true)
    public void testPolymorphingUserSso() throws Exception {
        LoginResult src = LoginResult.Builder.success(new SsoUser());
        String json = Jecksons.getInstance().encode(src);
        LoginResult restored = Jecksons.getInstance().decode(json, LoginResult.class);
        Assert.assertTrue(restored.getUser() instanceof SsoUser);
    }

}
