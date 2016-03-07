package ru.bio4j.ng.crudhandlers.impl;

import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class DataProviderImplTest {

    @Test
    public void testProcessCursor() throws Exception {
        Assert.assertEquals(ProviderGetDataset.calcOffset(00, 25), 0);
        Assert.assertEquals(ProviderGetDataset.calcOffset(01, 25), 0);
        Assert.assertEquals(ProviderGetDataset.calcOffset(25, 25), 0);
        Assert.assertEquals(ProviderGetDataset.calcOffset(26, 25), 25);
        Assert.assertEquals(ProviderGetDataset.calcOffset(48, 25), 25);
        Assert.assertEquals(ProviderGetDataset.calcOffset(50, 25), 25);
        Assert.assertEquals(ProviderGetDataset.calcOffset(51, 25), 50);
    }
}