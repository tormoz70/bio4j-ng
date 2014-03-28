package ru.bio4j.service.processing;

import org.hsqldb.jdbc.JDBCDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import ru.bio4j.collections.Parameter;
import ru.bio4j.func.UnsafeFunction;
import ru.bio4j.model.transport.BioRequest;
import ru.bio4j.model.transport.jstore.*;
import ru.bio4j.service.processing.config.ProcessingConfig;
import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.QueryContext;
import ru.bio4j.service.sql.query.ConnectionFactoryImpl;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@Test(singleThreaded = true)
public class QueryProcessorTest {

    private static final Logger LOG = LoggerFactory.getLogger(QueryProcessorTest.class);
    final QueryProcessorImpl processor = new QueryProcessorImpl();

    @BeforeSuite
    public void prepareClass() {
        JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setUrl("jdbc:hsqldb:mem:memdb");
        dataSource.setUser("SA");
        dataSource.setPassword(" ");
        ConnectionFactoryImpl factory = new ConnectionFactoryImpl(dataSource);
        QueryContext.create(factory);
        QueryContext.call(new UnsafeFunction<QueryContext, Object, Exception>() {
            @Override
            public Object apply(QueryContext key) throws Exception {
                key.getConnection().createStatement().execute(
                    "CREATE TABLE dummy(result_column VARCHAR(36))");
                key.getConnection().createStatement().execute(
                    "create procedure inserter(IN dat varchar(36))  " +
                        "MODIFIES SQL DATA " +
                        "BEGIN ATOMIC " +
                        "insert into dummy " +
                        "        ( " +
                        "          result_column " +
                        "        ) " +
                        "values  (  " +
                        "          dat " +
                        "        ); " +
                        "end");
                key.getConnection().createStatement().execute(
                    "call inserter('test1')");
                key.getConnection().createStatement().execute(
                    "call inserter('test2')");
                return null;
            }
        });
        QueryContext.remove();
    }

    @BeforeTest
    public void prepare() {
        Dictionary<String, String> dictionary = new Hashtable<>();
        dictionary.put(ProcessingConfig.CONNECT_URI, "jdbc:hsqldb:mem:memdb");
        dictionary.put(ProcessingConfig.DRIVER_CLASSNAME, "org.hsqldb.jdbc.JDBCDriver");
        dictionary.put(ProcessingConfig.USER, "SA");
        dictionary.put(ProcessingConfig.PASSWORD, " ");
        processor.updated(dictionary);
    }

    @Test(priority = 2)
    void testRead() throws Exception {
        final QueryProviderImpl provider = mock(QueryProviderImpl.class);
        final Map<String, Parameter> parameterMap = new HashMap<>();
        final Parameter parameter = new Parameter("test%", null);
        BioRequest bioRequest = new BioRequest();
        parameterMap.put("resultColumn", parameter);
        bioRequest.setBioParams(parameterMap);
        when(provider.createQuery(any(BioRequest.class), anyMap())).thenReturn(new Query("select result_column /*@result_column, title=Строка, javaType=string*/ " +
                "from dummy where result_column like /*$resultColumn, type=varchar {*/'test%'/*}*/ order by result_column", bioRequest.getBioParams()));
        processor.setQueryProvider(provider);
        final StoreData data = processor.read(bioRequest, Collections.<String, Parameter>emptyMap());
        final StoreMetadata metadata = data.getMetadata();
        final ColumnMetadata columnMetadata = metadata.getFields().get(0);
        assertNotNull(columnMetadata.getTitle());
        LOG.debug("columnMetadata.getTitle() = {}", columnMetadata.getTitle());
        assertEquals("test1", data.getValue(0, 0));
        assertEquals("test4", data.getValue(3, 0));
        LOG.debug("data.getValue(0,0) = {}", data.getValue(0, 0));
    }

    @Test(priority = 1)
    void testWrite() throws Exception {

        final QueryProviderImpl provider = mock(QueryProviderImpl.class);
        BioRequestJStorePost bioRequest = new BioRequestJStorePost();
        final Map<String, Parameter> parameterMap = new HashMap<>();
        bioRequest.setBioParams(parameterMap);
        List<StoreRow> rows = new ArrayList<>();
        StoreRow row1 = new StoreRow();
        row1.setChangeType(RowChangeType.MODIFIED);
        row1.setValues(Collections.<Object>singletonList("test3"));
        rows.add(row1);

        StoreRow row2 = new StoreRow();
        row2.setChangeType(RowChangeType.MODIFIED);
        row2.setValues(Collections.<Object>singletonList("test4"));
        rows.add(row2);

        List<ColumnMetadata> metadataList = new ArrayList<>();
        final ColumnMetadata metadata = new ColumnMetadata();
        metadata.setName("data");
        metadataList.add(metadata);

        Map<String, Integer> map = new HashMap<>();
        map.put("data", 0);

        final StoreData storeData = new StoreData(0, 0, new StoreMetadata(true, false, metadataList), rows, map);
        bioRequest.setPacket(storeData);

        when(provider.createQuery(any(BioRequest.class), anyMap())).thenReturn(new Query("{call inserter(/*$data, in, type=VARCHAR*/)}", bioRequest.getBioParams()));
        processor.setQueryProvider(provider);
        processor.write(bioRequest, Collections.<String, Parameter>emptyMap());

    }
}
