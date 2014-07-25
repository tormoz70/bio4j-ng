package ru.bio4j.ng.commons.types;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;

import java.util.ArrayList;
import java.util.List;

public class ParamsTest {

//	private final Params testParams = new Params();

	@BeforeClass
	private void setUp() {
//		testParams.add("param1", 11);
//		testParams.add("param2", 22);
	}

	@Test(enabled = true)
	public void add() {
		List<Param> testParams = new ArrayList<>();
        try(Paramus paramus  = Paramus.set(testParams);){
            paramus.add("param1", 111).add("param1", 111, true);
            Assert.assertEquals(paramus.getValueByName("param1", false), 111);
            paramus.add("param3", 33);
            Assert.assertNotNull(paramus.getParam("param3"));
            Assert.assertTrue(paramus.paramExists("param3"));
            Assert.assertFalse(paramus.paramExists("Param3", false));
            Assert.assertTrue(paramus.paramExists("Param3"));
        }
	}

	@Test(enabled = false)
	public void addListStringObjectchar() {
//		Params prms = new Params();
//		prms.addList("p1,p2", new Object[]{11, 22}, ",");
	}

	@Test(enabled = false)
	public void addListStringObject() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void addListStringStringchar() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void addListStringString() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void buildUrlParams() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void buildUrlParamsString() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void containsStringObject() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void containsParam() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void containsParams() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void encode() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void first() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void getIndexOf() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void getInnerObjectByName() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void getNamesList() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void getParamStringBooleanBoolean() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void getParamStringBoolean() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void getParamString() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void getValsList() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void getValueAsStringByName() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void getValueByName() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void merge() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void paramExistsString() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void paramExistsStringBoolean() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void process() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void removeParam() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void removeString() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void removeListStringchar() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void removeListString() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void setListStringObjectchar() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void setListStringStringchar() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void setListStringString() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void setValue() {
		throw new RuntimeException("Test not implemented");
	}

	@Test(enabled = false)
	public void toMap() {
		throw new RuntimeException("Test not implemented");
	}

    @Test(enabled = true)
    public void getParamValueTest() throws Exception {
        try(Paramus paramus = Paramus.set(new ArrayList<Param>());){
            Param.Builder pb = Param.builder()/*.owner(paramus.get())*/;
            paramus.add(pb.name("v_packetzip_id").type(MetaType.INTEGER).direction(Param.Direction.OUT).build());
            long org = paramus.getParamValue("v_packetzip_id", long.class);
            Assert.assertEquals(0L, org);
        }
    }

}
