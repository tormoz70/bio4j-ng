package ru.bio4j.ng.commons.utils;

import com.thoughtworks.xstream.exts.XStreamUtility;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.types.Paramus;
import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.XLRCfg;
import ru.bio4j.ng.model.transport.jstore.Field;
import ru.bio4j.ng.model.transport.jstore.Sort;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

public class XStreamUtilityTest {

    @Test
    public void toXmlTest() throws Exception {
        XLRCfg xlrCfg = new XLRCfg();

        XLRCfg.DataSource ds = new XLRCfg.DataSource();
        ds.setSql("select 1 from dual");

        ds.setSort(new ArrayList<>());
        Sort s = new Sort();
        s.setFieldName("sortField");
        s.setDirection(Sort.Direction.DESC);
        ds.getSort().add(s);

        XLRCfg.ColumnDefinition cd = new XLRCfg.ColumnDefinition();
        cd.setFieldName("field1");
        cd.setTitle("Field 1");
        cd.setFormat("##0.00");
        ds.getColumnDefinitions().add(cd);

        xlrCfg.setDss(ds);

        OutputStream output = new OutputStream()
        {
            private StringBuilder string = new StringBuilder();
            @Override
            public void write(int b) throws IOException {
                this.string.append((char) b );
            }
            public String toString(){
                return this.string.toString();
            }
        };

        XStreamUtility.getInstance().toXml(xlrCfg, output);

        String aString = output.toString();
        Assert.assertTrue(aString != null);

        XLRCfg restored = XStreamUtility.getInstance().toJavaBean(aString);
        Assert.assertTrue(restored != null);
    }
}