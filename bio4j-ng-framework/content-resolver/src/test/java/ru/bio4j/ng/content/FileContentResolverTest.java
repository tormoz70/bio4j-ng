package ru.bio4j.ng.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.collections.Pair;
import ru.bio4j.ng.commons.utils.Regexs;
import ru.bio4j.ng.content.impl.QueryExtractor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Pattern;

public class FileContentResolverTest {

    private static final Logger LOG = LoggerFactory.getLogger(FileContentResolverTest.class);

    private static final String QUERY = "/*${sql.an query_name}*/";
    private static final String BIO = "path.to.content.queryName";

    @Test
    public void testQueryParser() {

        final String group = Regexs.find(QUERY, QueryExtractor.QUERY_NAME, Pattern.CASE_INSENSITIVE);
        LOG.debug("group = {}", group);
        Assert.assertEquals(group, "an query_name");

    }

    @Test
    public void bioCodeQueryParser() {

        final Pair<String,String> pair = QueryExtractor.extractName(BIO);
        Assert.assertEquals("queryName", pair.getLeft());
        Assert.assertEquals("path.to.content", pair.getRight());

    }

    @Test
    public void fullTest() throws IOException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource("test.sql");
        final File file = new File(url.getPath());

        final Map<String,String> test = QueryExtractor.loadQueries("test",
            file.getParent());
        Assert.assertTrue(test.size() == 2);
        StringBuilder expected = new StringBuilder();
        expected.append("-- SQL comments for select"+System.lineSeparator());
        expected.append(System.lineSeparator());
        expected.append("        select * from test"+System.lineSeparator());
        Assert.assertTrue(test.get("select").equals(expected.toString()));
    }
    @Test
    public void fullTest1() throws IOException {
        LOG.debug("start...");
        final URL url = Thread.currentThread().getContextClassLoader().getResource("users.sql");
        final File file = new File(url.getPath());

        final Map<String,String> test = QueryExtractor.loadQueries("users", file.getParent());
        Assert.assertTrue(test.size() == 1);
        Assert.assertTrue(test.get("list").startsWith("SELECT"));
    }
}
