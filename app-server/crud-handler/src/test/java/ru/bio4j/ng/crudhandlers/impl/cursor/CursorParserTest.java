package ru.bio4j.ng.crudhandlers.impl.cursor;

import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.MetaType;
import ru.bio4j.ng.model.transport.jstore.Alignment;
import ru.bio4j.ng.model.transport.jstore.Column;
import ru.bio4j.ng.service.api.Cursor;

/**
 * Created by ayrat on 16.04.14.
 */
public class CursorParserTest {
    @Test
    public void testPars() throws Exception {
        String sql = Utl.readStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("test.sql"));
        Cursor cursor = CursorParser.pars("test", sql);
        Assert.assertTrue(cursor != null);
        Column col = cursor.getMetadata().getColumns().get(0);
        Assert.assertEquals(col.getName(), "subdivision");
        Assert.assertEquals(col.getTitle(), "\"Дивизион\"; (miter)");
        Assert.assertEquals(col.isPk(), true);
        Assert.assertEquals(col.isMandatory(), true);
        Assert.assertEquals(col.isHidden(), true);
        Assert.assertEquals(col.isReadonly(), true);
        Assert.assertEquals(col.getAlign(), Alignment.RIGHT);
        Assert.assertEquals(col.getWidth(), "150");
        Assert.assertEquals(col.getFormat(), "0,00");
        Assert.assertEquals(col.getType(), MetaType.UNDEFINED);
        Assert.assertEquals(cursor.getWrapMode(), Cursor.WrapMode.SORT.code()+Cursor.WrapMode.PAGING.code());
    }
}