package ru.bio4j.ng.commons.converter;

import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.converter.hanlers.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ayrat on 22.03.14.
 */
public class TypeHandlerBaseTest {
    @Test
    public void testGetGenericIfcsType() throws Exception {
        TypeHandler bh;
        bh = TypeHandlerMapper.getHandler(Boolean.class);
        Assert.assertTrue(bh instanceof BooleanHandler);
        bh = TypeHandlerMapper.getHandler(boolean.class);
        Assert.assertTrue(bh instanceof BooleanHandler);

        bh = TypeHandlerMapper.getHandler(String.class);
        Assert.assertTrue(bh instanceof StringHandler);

        bh = TypeHandlerMapper.getHandler(Date.class);
        Assert.assertTrue(bh instanceof DateHandler);

        bh = TypeHandlerMapper.getHandler(java.sql.Date.class);
        Assert.assertTrue(bh instanceof DateHandler);

        bh = TypeHandlerMapper.getHandler(java.sql.Timestamp.class);
        Assert.assertTrue(bh instanceof DateHandler);

        bh = TypeHandlerMapper.getHandler(int.class);
        Assert.assertTrue(bh instanceof NumberHandler);
        bh = TypeHandlerMapper.getHandler(long.class);
        Assert.assertTrue(bh instanceof NumberHandler);
        bh = TypeHandlerMapper.getHandler(Double.class);
        Assert.assertTrue(bh instanceof NumberHandler);
        bh = TypeHandlerMapper.getHandler(BigDecimal.class);
        Assert.assertTrue(bh instanceof NumberHandler);
    }

}
