package ru.bio4j.ng.commons.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.converter.DateTimeParser;
import ru.bio4j.ng.commons.converter.Types;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.model.transport.*;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetDataSet;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStorePost;
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

    @Test(enabled = true)
    public void bdecode1() throws Exception {
        final String requestBody = "{"+
                "\"bioParams\":[{\"name\":\"param1\",\"value\":\"123\"},"+
                               "{\"name\":\"param2\",\"value\":null, \"type\":\"string\"},"+
                               "{\"name\":\"param3\",\"value\":123},"+
                               "{\"name\":\"param4\",\"value\":\"1970-03-02T18:43:56.000+0300\"}"+
                "],"+
                "\"bioModuleKey\":\"ekbp\",\"bioCode\":\"cabinet.film-h2registry\",\"offset\":0,\"pageSize\":26}";
        Jecksons.getInstance().setDefaultDateTimeFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        BioRequestJStoreGetDataSet request = Jecksons.getInstance().decode(requestBody, BioRequestJStoreGetDataSet.class);
        if(LOG.isDebugEnabled())
            LOG.debug(Utl.buildBeanStateInfo(request, "Request", "  "));

        Date expectedDateTime = Types.parse("1970.03.02T18:43:56.000+0300", "yyyy.MM.dd'T'HH:mm:ss.SSSZ");
        if(LOG.isDebugEnabled())
            LOG.debug("expectedDateTime: {}", expectedDateTime);

        String expectedDateTimeStr = Jecksons.getInstance().encode(expectedDateTime);

        TimeZone timeZone = TimeZone.getDefault();
        int offset = timeZone.getOffset(expectedDateTime.getTime());
        if(LOG.isDebugEnabled())
            LOG.debug("TimeZone: {}, offset: {}", timeZone.getDisplayName(), offset/1000/3600);

        try(Paramus p = Paramus.set(request.getBioParams())){
            Assert.assertEquals(p.getParam("param1").getValue(), "123");
            Assert.assertEquals(p.getParam("param2").getValue(), null);
            Assert.assertEquals(p.getParam("param2").getType(), MetaType.STRING);
            Assert.assertEquals(p.getParam("param3").getValue(), 123);
            Object dateTime = p.getParam("param4").getValue();
            Assert.assertEquals(dateTime, expectedDateTime);
        }
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
    public void bdecode3() throws Exception {
        String json = "{\"bioModuleKey\":\"\",\"bioCode\":\"cabinet.film-h2registry\",\"bioParams\":[{\"name\":\"prm1\",\"value\":\"qwe\"},{\"name\":\"prm2\",\"value\":\"asd\"}],\"offset\":0,\"pageSize\":25,\"sort\":[{\"fieldName\":\"property\",\"direction\":\"ASC\"}]}";
        BioRequestJStoreGetDataSet rq = Jecksons.getInstance().decode(json, BioRequestJStoreGetDataSet.class);
        Assert.assertNotNull(rq);
        json = "{\"bioModuleKey\":\"\",\"bioCode\":\"cabinet.film-h2registry\",\"bioParams\":[{\"name\":\"prm1\",\"value\":\"qwe\"},{\"name\":\"prm2\",\"value\":\"asd\"}],\"offset\":0,\"pageSize\":25,\"sort\":[]}";
        rq = Jecksons.getInstance().decode(json, BioRequestJStoreGetDataSet.class);
        Assert.assertNotNull(rq);
        json = "{\"bioModuleKey\":\"\",\"bioCode\":\"cabinet.film-h2registry\",\"bioParams\":[{\"name\":\"prm1\",\"value\":\"qwe\"},{\"name\":\"prm2\",\"value\":\"asd\"}],\"offset\":0,\"pageSize\":25,\"sort\":null}";
        rq = Jecksons.getInstance().decode(json, BioRequestJStoreGetDataSet.class);
        Assert.assertNotNull(rq);
    }

    @Test(enabled = true)
    public void bdecode4() throws Exception {
        String json = "{\"bioCode\":\"cabinet.get-org\",\"bioParams\":[{\"name\":\"org_id\",\"value\":null}]}";
        BioRequest bioRequest = Jecksons.getInstance().decode(json, BioRequestJStoreGetDataSet.class);
        Assert.assertEquals("org_id", bioRequest.getBioParams().get(0).getName());
        Assert.assertNull(bioRequest.getBioParams().get(0).getValue());
    }

    private static final String tstPost = "{\"bioCode\":\"cabinet.org-sroom-list\",\"bioParams\":[{\"name\":\"id_org\",\"value\":305}],\"modified\":[{\"changeType\":\"update\",\"class\":\"ru.bio4j.ng.model.transport.jstore.StoreRow\",\"values\":[533,305,\"1111\",100]}]}";
    @Test(enabled = true)
    public void bdecode5() throws Exception {
        BioRequest bioRequest = Jecksons.getInstance().decode(tstPost, BioRequestJStorePost.class);
        Assert.assertEquals("id_org", bioRequest.getBioParams().get(0).getName());
        Assert.assertEquals(305, bioRequest.getBioParams().get(0).getValue());
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

}
