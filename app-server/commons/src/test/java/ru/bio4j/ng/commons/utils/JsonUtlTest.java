package ru.bio4j.ng.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.converter.Types;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGet;

import java.util.Date;
import java.util.TimeZone;

public class JsonUtlTest {
    private static final Logger LOG = LoggerFactory.getLogger(Utl.class);

	private final TBox testBox = new TBox();

	@BeforeClass
	private void setUp() {
		this.testBox.setName("Test-Box");
		this.testBox.setCreated(Types.parse("2012.12.20-15:11:24", "yyyy.MM.dd-HH:mm:ss"));
		this.testBox.setVolume(123.05);
		this.testBox.setPackets(new TPacket[] { new TPacket() });
		this.testBox.getPackets()[0].setName("packet-0");
		this.testBox.getPackets()[0].setVolume(100.10);
		this.testBox.getPackets()[0].setApples(new TApple[] { new TApple(), new TApple() });
		this.testBox.getPackets()[0].getApples()[0].setName("apple-0-0");
		this.testBox.getPackets()[0].getApples()[0].setWheight(10.100);
		this.testBox.getPackets()[0].getApples()[1].setName("apple-0-1");
		this.testBox.getPackets()[0].getApples()[1].setWheight(10.200);
		this.testBox.setEx(new Exception("FTW TestException"));
	}

	@Test(enabled = true)
	public void aencode() throws Exception {
        TBox testBox = new TBox();
        testBox.setName("Test-Box");
		String expected =
		 "{\"created\":null,\"ex\":null,\"name\":\"Test-Box\",\"packets\":null,\"volume\":null}";
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
	}

    @Test(enabled = true)
    public void bdecode1() throws Exception {
        final String requestBody = "{"+
                "\"bioParams\":[{\"name\":\"param1\",\"value\":\"123\"},"+
                               "{\"name\":\"param2\",\"value\":null},"+
                               "{\"name\":\"param3\",\"value\":123},"+
                               "{\"name\":\"param4\",\"value\":\"1970-03-02T18:43:56.555+0300\"}"+
                "],"+
                "\"bioModuleKey\":\"ekbp\",\"bioCode\":\"cabinet.film-registry\",\"offset\":0,\"pagesize\":26}";
        BioRequestJStoreGet request = Jsons.decode(requestBody, BioRequestJStoreGet.class);
        LOG.debug(Utl.buildBeanStateInfo(request, "Request", "  "));

        Date expectedDateTime = Types.parse("1970.03.02T18:43:56.555+0300", "yyyy.MM.dd'T'HH:mm:ss.SSSZ");
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
}
