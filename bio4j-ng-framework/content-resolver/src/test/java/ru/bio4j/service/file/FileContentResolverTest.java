package ru.bio4j.service.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.collections.Pair;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileContentResolverTest {

    private static final Logger LOG = LoggerFactory.getLogger(FileContentResolverTest.class);

    private static final String QUERY = "/*#name*/";
    private static final String BIO = "path.to.file.queryName";

    @Test
    public void testQueryParser() {

        final Pattern pattern = Pattern.compile(QueryExtractor.QUERY_NAME);
        final Matcher matcher = pattern.matcher(QUERY);
        Assert.assertTrue(matcher.find());
        final String group = matcher.group(1);
        LOG.debug("group = {}", group);
        Assert.assertEquals(group, "name");

    }

    @Test
    public void bioCodeQueryParser() {

        final Pair<String,String> pair = QueryExtractor.extractName(BIO);
        Assert.assertEquals("queryName", pair.getLeft());
        Assert.assertEquals("path.to.file", pair.getRight());

    }

    @Test
    public void fullTest() throws IOException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource("test.sql");
        final File file = new File(url.getPath());

        final Map<String,String> test = QueryExtractor.loadQueries("test",
            file.getParent());
        Assert.assertTrue(test.size() == 2);
        Assert.assertTrue(test.get("select").contains("select"));
    }
}
