package ru.bio4j.ng.commons.tmodel;

import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bio4j.ng.commons.utils.Jsons;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.commons.utils.Utl;
import ru.bio4j.ng.model.transport.jstore.filter.*;


import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static ru.bio4j.ng.model.transport.jstore.filter.FilterBuilder.*;

public class FilterFactoryTest {
    private static final Map<Class<?>, String> compareTemplates = new HashMap<Class<?>, String>() {{
        put(Eq.class, "%s = %s");
        put(Gt.class, "%s > %s");
        put(Ge.class, "%s >= %s");
        put(Lt.class, "%s < %s");
        put(Le.class, "%s <= %s");
        put(Bgn.class, "%s like %s");
        put(End.class, "%s like %s");
        put(Contains.class, "%s like %s");
    }};

    private static String appendAlias(String alias, String column){
        return (Strings.isNullOrEmpty(alias) ? ""  : alias+".")+column;
    }

    private static String decodeCompare(String alias, Expression e) {
        String column = appendAlias(alias, e.getColumn());
        Object value = e.getValue();
        String templ = compareTemplates.get(e.getClass());
        if(e instanceof Bgn)
            value = value+"%";
        if(e instanceof End)
            value = "%"+value;
        if(e instanceof Contains)
            value = "%"+value+"%";
        if (Strings.isString(value))
            value = "'"+value+"'";
        if (e.ignoreCase()) {
            value = "upper("+value+")";
            column = "upper("+column+")";
        }

        if (value != null && value instanceof Date)
            value = "to_date('YYYYMMDD', '" + new SimpleDateFormat("YYYYMMdd").format(value) + "')";
        return "("+String.format(templ, column, value)+")";
    }

    @Test
    public void decodeCompareTest() {
        String s = decodeCompare("fltr", new Eq("fld", "qwe"));
        System.out.println("s1: "+s);
        Assert.assertEquals(s, "(fltr.fld = 'qwe')");
        s = decodeCompare("fltr", new Bgn("fld", "qwe", true));
        System.out.println("s2: "+s);
        Assert.assertEquals(s, "(upper(fltr.fld) like upper('qwe%'))");
        Date testDateValue = new Date();
        s = decodeCompare("fltr", new Le("datef", testDateValue));
        System.out.println("s3: "+s);
        Assert.assertEquals(s, "(fltr.datef <= to_date('YYYYMMDD', '"+new SimpleDateFormat("YYYYMMdd").format(testDateValue)+"'))");
    }

    private static String buildSql(String alias, Expression e) {
        if(e instanceof Logical){
            String logicalOp = (e instanceof And) ? " and " : (
                                 (e instanceof Or) ? " or " : " unknown-logical "
                                );
            StringBuilder rslt = new StringBuilder();
            for(Object chld : e.getChildren()){
                rslt.append(((rslt.length() == 0) ? "" : logicalOp) + buildSql(alias, (Expression)chld));
            }
            return "("+rslt.toString()+")";
        }

        if(e instanceof Compare){
           return decodeCompare(alias, e);
        }
        if(e instanceof IsNull){
            return "("+appendAlias(alias, e.getColumn()) + " is null)";
        }
        if(e instanceof Not){
            return "not "+buildSql(alias, (Expression)e.getChildren().get(0))+"";
        }
        return null;
    }

    @Test
    public void testDo() {

        Expression filter = and()
            .add(
                and()
                    .add(not(eq("field10", 1)))
                    .add(eq("field12", 23L))
                    .add(gt("fieldD", 2.5)))
            .add(
                    or()
                            .add(bgn("field2", "qwe", true))
                            .add(end("field2", "ads", false))
            )
            .add(
                    not(isNull("nnfld"))
            );
        String sql = buildSql("fltr", filter);
        System.out.println("sql: "+sql);
        Assert.assertTrue(true);
    }

    @Test
    public void testDo1() {

        Expression filter = filter(and()
                .add(
                        and()
                                .add(not(eq("field10", 1)))
                                .add(eq("field12", 23L))
                                .add(gt("fieldD", 2.5)))
                .add(
                        or()
                                .add(bgn("field2", "qwe", true))
                                .add(end("field2", "ads", false))
                )
                .add(
                        not(isNull("nnfld"))
                )
                .add(contains("field4", "some", true))
        );
        String json = Jsons.encode(filter);
        System.out.println("json: "+json);
        Assert.assertTrue(true);
    }


    @Test
    public void testDo2() throws Exception {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("filter.json");
        String json = Utl.readStream(inputStream);
        Expression filter = Jsons.decode(json, Filter.class);
        Assert.assertNotNull(filter);
    }

}
