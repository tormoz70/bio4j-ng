package ru.bio4j.ng.crudhandlers.impl;

import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class DataProviderImplTest {

    @Test
    public void testProcessCursor() throws Exception {
        Assert.assertEquals(DataProviderImpl.calcOffset(00, 25), 0);
        Assert.assertEquals(DataProviderImpl.calcOffset(01, 25), 0);
        Assert.assertEquals(DataProviderImpl.calcOffset(25, 25), 0);
        Assert.assertEquals(DataProviderImpl.calcOffset(26, 25), 25);
        Assert.assertEquals(DataProviderImpl.calcOffset(48, 25), 25);
        Assert.assertEquals(DataProviderImpl.calcOffset(50, 25), 25);
        Assert.assertEquals(DataProviderImpl.calcOffset(51, 25), 50);
    }
}