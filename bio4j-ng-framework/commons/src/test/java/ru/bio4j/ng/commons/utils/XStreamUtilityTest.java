package ru.bio4j.ng.commons.utils;

import com.thoughtworks.xstream.exts.XStreamUtility;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.XLRCfg;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.Sort;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;

public class XStreamUtilityTest {

    @Test
    public void toXmlTest() throws Exception {
        XLRCfg xlrCfg = new XLRCfg();

        XLRCfg.DataSource ds = new XLRCfg.DataSource();
        ds.setSql("select 1 from dual");

        ds.setSorts(new ArrayList<>());
        Sort s = new Sort();
        s.setFieldName("sortField");
        s.setDirection(Sort.Direction.DESC);
        ds.getSorts().add(s);

        XLRCfg.ColumnDefinition cd = new XLRCfg.ColumnDefinition();
        cd.setFieldName("field1");
        cd.setTitle("Колонка 1");
        cd.setFormat("##0.00");
        ds.getColumnDefinitions().add(cd);

        xlrCfg.setDss(new ArrayList<>());
        xlrCfg.getDss().add(ds);

        xlrCfg.setAppend(new XLRCfg.Append());
        xlrCfg.getAppend().setInParams(new ArrayList<>());
        xlrCfg.getAppend().getInParams().add(Param.builder().name("inparam1").type(MetaType.STRING).direction(Param.Direction.IN).value("inparam1-value").build());
        xlrCfg.getAppend().setSessionID("sess-id");
        xlrCfg.getAppend().setUserUID("user-uid");
        xlrCfg.getAppend().setUserName("user-name");
        xlrCfg.getAppend().setUserOrgId("user-org-id");
        xlrCfg.getAppend().setUserRoles("user-roles");
        xlrCfg.getAppend().setRemoteIP("remote-ip");


        String encoding = "UTF-8";
        String aString = XStreamUtility.getInstance().toXml(xlrCfg, encoding);

        Assert.assertTrue(true);

        Utl.storeString(aString, "d:\\exp-rpt-cfg-test.xml", encoding);

//        XLRCfg restored = XStreamUtility.getInstance().toJavaBean(aString);
        Assert.assertTrue(true);
    }
}