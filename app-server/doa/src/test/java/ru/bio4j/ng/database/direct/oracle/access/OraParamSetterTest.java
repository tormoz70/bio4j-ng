package ru.bio4j.ng.database.direct.oracle.access;

import ru.bio4j.ng.database.doa.impl.OraUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ayrat
 * Date: 02.12.13
 * Time: 21:09
 * To change this template use File | Settings | File Templates.
 */
public class OraParamSetterTest {
    @Test
    public void testExtractParamNamesFromSQL1() throws Exception {
        List<String> params = OraUtils.extractParamNamesFromSQL("select user as curuser, :dummy as dummy_param, ':wer' as mak /* :ert */ from dual");
        Assert.assertEquals(params.size(), 1);
        Assert.assertEquals(params.get(0), "dummy");
    }

    @Test
    public void testExtractParamNamesFromSQL2() throws Exception {
        List<String> params = OraUtils.extractParamNamesFromSQL("begin :rslt := :param1 + :param2; end;");
        Assert.assertEquals(params.size(), 3);
        Assert.assertEquals(params.get(0), "rslt");
        Assert.assertEquals(params.get(1), "param1");
        Assert.assertEquals(params.get(2), "param2");
    }

}
