package ru.bio4j.ng.commons.utils;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.converter.Types;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGet;

public class JsonUtlTest {

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
	public void a_encode() throws Exception {
        TBox testBox = new TBox();
        testBox.setName("Test-Box");
		String expected =
		 "{\"created\":null,\"ex\":null,\"name\":\"Test-Box\",\"packets\":null,\"volume\":null}";
		String testJson = Jsons.encode(testBox);
		System.out.println(testJson);
		Assert.assertEquals(expected, testJson);
	}

	@Test(enabled = true)
	public void b_decode() throws Exception {
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
    public void b_decode1() throws Exception {
        final String requestBody = "{\"bioModuleKey\":\"ekbp\",\"bioCode\":\"cabinet.film-registry\",\"offset\":0,\"pagesize\":25}";
        BioRequestJStoreGet request = Jsons.decode(requestBody, BioRequestJStoreGet.class);
        System.out.println(Utl.buildBeanStateInfo(request, "Request", "  "));
    }
}
