package ru.bio4j.ng.router;

import org.testng.annotations.Test;
import ru.bio4j.ng.model.transport.BioRequest;
import ru.bio4j.ng.model.transport.BioRequestGetJson;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.BioRequestJStoreGetDataSet;
import ru.bio4j.ng.service.api.BioRequestFactory;
import ru.bio4j.ng.service.api.SrvcUtils;

import java.util.List;

import static org.testng.Assert.*;

public class BioRequestFactoryTest {

    @Test(enabled = true)
    public void testRestore0() throws Exception {
        BioRequestFactory<BioRequestGetJson> factory = new BioRequestFactory.GetJson();
        SrvcUtils.BioQueryParams qprms = new SrvcUtils.BioQueryParams();
        qprms.jsonData = "{ 'bioParams': [{'name': 'nam', 'value': 'tessa'}], 'sort': [ {'fieldName': 'aname', 'direction': 'DESC'} ] }";
        BioRequest request = factory.restore(qprms, BioRequestGetJson.class, null);
        List<Param> prms = request.getBioParams();
    }

    @Test(enabled = true)
    public void testRestore1() throws Exception {
        BioRequestFactory<BioRequestGetJson> factory = new BioRequestFactory.GetJson();
        SrvcUtils.BioQueryParams qprms = new SrvcUtils.BioQueryParams();
        qprms.jsonData = "{ 'bioParams': {'nam1':'tessa1', 'nam2':'tessa2'}, 'sort': [ {'fieldName': 'aname', 'direction': 'DESC'} ] }";
        BioRequest request = factory.restore(qprms, BioRequestGetJson.class, null);
        List<Param> prms = request.getBioParams();
    }
}