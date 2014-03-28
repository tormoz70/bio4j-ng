package ru.bio4j.service.sql.query.wrappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.func.Function;
import ru.bio4j.service.sql.TestSuiteSetup;

public class WrapperLoaderTest extends TestSuiteSetup {

    private static final Logger LOG = LoggerFactory.getLogger(WrapperLoaderTest.class);

    @Test
    public void testQuery() {
        final Function<WrapQueryType,String> queryFunction = WrapperLoader.loadQueries();
        Assert.assertNotNull(queryFunction);
        for (WrapQueryType type : WrapQueryType.values()) {
            final String apply = queryFunction.apply(type);
            Assert.assertNotNull(apply);
            LOG.debug(apply);
        }
    }
}
