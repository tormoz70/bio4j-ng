package ru.bio4j.ng.fcloud.h2registry;

import org.h2.jdbcx.JdbcDataSource;
import org.testng.annotations.Test;
import ru.bio4j.ng.database.api.SQLNamedParametersStatement;
import ru.bio4j.ng.database.api.SQLParamGetter;
import ru.bio4j.ng.database.api.SQLParamSetter;
import ru.bio4j.ng.database.commons.DbCallableParamGetter;
import ru.bio4j.ng.database.commons.DbCallableParamSetter;
import ru.bio4j.ng.database.commons.DbNamedParametersStatement;
import ru.bio4j.ng.database.commons.DbSelectableParamSetter;
import ru.bio4j.ng.model.transport.Param;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public class H2ApiTest {

    @Test
    public void getConnectionTest() throws Exception {
//        Connection conn = H2Api.getConnection("jdbc:h2:d:/test", "sa", "sa");
//        FCloudDBApi.initDB(conn);

    }

}
