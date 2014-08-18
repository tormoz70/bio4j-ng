package ru.bio4j.ng.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.converter.Types;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.model.transport.BioError;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class JsonUtlTest {
    private static final Logger LOG = LoggerFactory.getLogger(Utl.class);

	private final TBox testBox = new TBox();

	@BeforeClass
	private void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+04:00"));
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
		 "{\"created\":null,\"err\":null,\"ex\":null,\"name\":\"Test-Box\",\"packets\":null,\"type\":\"undefined\",\"volume\":null}";
		String testJson = Jsons.encode(testBox);
		System.out.println(testJson);
		Assert.assertEquals(expected, testJson);
	}

	@Test(enabled = true)
	public void bdecode() throws Exception {
		String testJson = Jsons.encode(this.testBox);
		TBox restored = Jsons.decode(testJson, TBox.class);
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
                               "{\"name\":\"param2\",\"value\":null},"+
                               "{\"name\":\"param3\",\"value\":123},"+
                               "{\"name\":\"param4\",\"value\":\"1970-03-02T18:43:56.555+0400\"}"+
                "],"+
                "\"bioModuleKey\":\"ekbp\",\"bioCode\":\"cabinet.film-registry\",\"offset\":0,\"pageSize\":26}";
        BioRequestJStoreGet request = Jsons.decode(requestBody, BioRequestJStoreGet.class);
        LOG.debug(Utl.buildBeanStateInfo(request, "Request", "  "));

        Date expectedDateTime = Types.parse("1970.03.02T18:43:56.555+0400", "yyyy.MM.dd'T'HH:mm:ss.SSSZ");
        LOG.debug("expectedDateTime: {}", expectedDateTime);

        TimeZone timeZone = TimeZone.getDefault();
        int offset = timeZone.getOffset(expectedDateTime.getTime());
        LOG.debug("TimeZone: {}, offset: {}", timeZone.getDisplayName(), offset/1000/3600);

        try(Paramus p = Paramus.set(request.getBioParams())){
            Assert.assertEquals(p.getParam("param1").getValue(), "123");
            Assert.assertEquals(p.getParam("param2").getValue(), null);
            Assert.assertEquals(p.getParam("param3").getValue(), 123);
            Assert.assertEquals(p.getParam("param4").getValue(), expectedDateTime);
        }
    }

    // -Duser.timezone=GMT+4
    @Test(enabled = true)
    public void bdecode2() throws Exception {
        LOG.debug("test:");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd'T'HH:mm:ss.SSS");
        format1.setTimeZone(TimeZone.getTimeZone("GMT+04:00"));
        Date d = format1.parse("1970.03.02T18:43:56.555");
        LOG.debug("d:{}", d);
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d);
        LOG.debug("c1:{}, {}", c1.getTime(), c1.getTimeZone());
        Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:00"));
        c2.setTime(d);
        LOG.debug("c2:{}, {}", c2.getTime(), c2.getTimeZone());
    }

    @Test(enabled = true)
    public void bdecode3() throws Exception {
        String json = "{\"bioModuleKey\":\"\",\"bioCode\":\"ekbp@cabinet.film-registry\",\"bioParams\":[{\"name\":\"prm1\",\"value\":\"qwe\"},{\"name\":\"prm2\",\"value\":\"asd\"}],\"offset\":0,\"pageSize\":25,\"sort\":[{\"fieldName\":\"property\",\"direction\":\"ASC\"}]}";
        BioRequestJStoreGet rq = Jsons.decode(json, BioRequestJStoreGet.class);
        Assert.assertNotNull(rq);
        json = "{\"bioModuleKey\":\"\",\"bioCode\":\"ekbp@cabinet.film-registry\",\"bioParams\":[{\"name\":\"prm1\",\"value\":\"qwe\"},{\"name\":\"prm2\",\"value\":\"asd\"}],\"offset\":0,\"pageSize\":25,\"sort\":[]}";
        rq = Jsons.decode(json, BioRequestJStoreGet.class);
        Assert.assertNotNull(rq);
        json = "{\"bioModuleKey\":\"\",\"bioCode\":\"ekbp@cabinet.film-registry\",\"bioParams\":[{\"name\":\"prm1\",\"value\":\"qwe\"},{\"name\":\"prm2\",\"value\":\"asd\"}],\"offset\":0,\"pageSize\":25,\"sort\":null}";
        rq = Jsons.decode(json, BioRequestJStoreGet.class);
        Assert.assertNotNull(rq);
    }

    @Test(enabled = true)
    public void bdecode4() throws Exception {
        String json = "{\"bioCode\":\"ekbp@cabinet.get-org\",\"bioParams\":[{\"name\":\"org_id\",\"value\":{}}]}";
        BioRequest bioRequest = Jsons.decode(json, BioRequestJStoreGet.class);
        Assert.assertEquals("org_id", bioRequest.getBioParams().get(0).getName());
        Assert.assertNull(bioRequest.getBioParams().get(0).getValue());
    }

}
