package ru.bio4j.service.sql;

import org.testng.annotations.Test;
import ru.bio4j.collections.Parameter;
import ru.bio4j.func.Function;
import ru.bio4j.service.sql.query.parser.ParsedParameter;
import ru.bio4j.service.sql.query.parser.ParsedQuery;
import ru.bio4j.service.sql.query.parser.QueryParser;
import ru.bio4j.service.sql.query.parser.StatementFunction;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class QueryParserTest {

    public QueryParserTest() {
    }

    @Test
    public void test() {
        Function<String, Parameter> f = new Function<String, Parameter>() {

            @Override
            public Parameter apply(String key) throws RuntimeException {
                if (key.equals("DEVICEID")) {
                    return new Parameter(2, "integer");
                } else {
                    throw new IllegalArgumentException("Param not found" + key);
                }
            }
        };
        QueryParser parser = new QueryParser();
        Query query = new Query("select * from \"#sometable\" where deviceId = /*$deviceId, type=varchar {*/'009884707195'/*}*/");
        ParsedQuery pq = parser.parse(query, f);
        assertEquals("select * from \"#sometable\" where deviceId = ?", pq.getQuery());
        List<ParsedParameter> pps = pq.getParameters();
        assertEquals(1, pps.size());
        ParsedParameter pp = pps.get(0);
        assertEquals(1, pp.getPosition());
        assertEquals("DEVICEID", pp.getName());
        assertEquals(false, pp.isOutput());
        assertEquals("varchar", pp.getSqlTypeName());

    }

    @Test
    public void testCases() {
        final QueryParser parser = new QueryParser();
        final Query query = new Query("select * from \"#sometable\" where deviceId = /*$deviceId, type=varchar {*/'2'/*}*/ " +
                "and param = /*$param, type=varchar {*/'3'/*}*/");
        query.setParam("deviceid", 2, null);
        query.setParam("PARAM", 3, null);
        final StatementFunction function = new StatementFunction(query);
        ParsedQuery pq = parser.parse(query, function);
        List<ParsedParameter> pps = pq.getParameters();
        assertEquals(2, pps.size());
        ParsedParameter pp1 = pps.get(0);
        assertEquals(1, pp1.getPosition());
        assertEquals("DEVICEID", pp1.getName());
        ParsedParameter pp2 = pps.get(1);
        assertEquals(2, pp2.getPosition());
        assertEquals("PARAM", pp2.getName());
    }

}
