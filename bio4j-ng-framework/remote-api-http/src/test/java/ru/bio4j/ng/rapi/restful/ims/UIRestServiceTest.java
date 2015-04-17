package ru.bio4j.ng.rapi.restful.ims;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * Created by ayrat on 17.03.14.
 */
public class UIRestServiceTest {
    private static final Logger LOG = LoggerFactory.getLogger(UIRestServiceTest.class);

    @Test
    public void testSerializeBioRequest() throws Exception {
//        ObjectMapper mapper = new ObjectMapper();
//        BioRequest request = new BioRequest();
//        request.setBioCode("emp.test");
//        request.setBioParams(new HashMap<String, Parameter>());
//        request.getBioParams().put("query", new Parameter("SAL%", "string"));
//        request.getBioParams().put("query1", new Parameter("SAL1%", "string"));
//        String json = mapper.writeValueAsString(request);
//        LOG.debug("json: "+json);
//        String expected = "{\"@class\":\"ru.bio4j.model.transport.BioRequest\",\"bioCode\":\"emp.test\",\"bioParams\":{\"query\":{\"value\":\"SAL%\",\"type\":\"string\"},\"query1\":{\"value\":\"SAL1%\",\"type\":\"string\"}}}";
//        Assert.assertEquals(expected, json);
    }


    @Test
    public void testDeserializeBioRequest() throws Exception {
//        ObjectMapper mapper = new ObjectMapper();
//        String src = "{\"@class\":\"ru.bio4j.model.transport.BioRequest\",\"bioCode\":\"emp.test\",\"bioParams\":{\"query\":{\"value\":\"SAL%\",\"type\":\"string\"}}}";
//        ObjectReader reader = mapper.reader(BioRequest.class);
//        BioRequest request = reader.readValue(src);
//        Assert.assertEquals("SAL%", request.getBioParams().get("query").getValue());
    }
}
