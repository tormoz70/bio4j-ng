package ru.bio4j.service.sql.query.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.QueryContext;
import ru.bio4j.service.sql.db.DBUtils;
import ru.bio4j.service.sql.query.parser.ParsedParameter;
import ru.bio4j.service.sql.query.parser.ParsedQuery;
import ru.bio4j.service.sql.query.parser.QueryParser;
import ru.bio4j.service.sql.query.parser.StatementFunction;
import ru.bio4j.service.sql.query.wrappers.WrapperFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static ru.bio4j.service.sql.query.wrappers.WrapQueryType.*;

public class SimpleSelect implements QueryHandler<ResultSet> {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleSelect.class);

    private final QueryParser parser = new QueryParser();

    /**
     * @title Обработка простого запроса
     * @param <T>
     * @param sql
     * @param context
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public<T> T handle(Query sql, QueryContext context, ResultReceiver<ResultSet, T> handler) throws Exception {
        T result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            if(sql.getFilter() != null)
                WrapperFactory.wrapRequest(sql, FILTERING);
            if(sql.getSort() != null)
                WrapperFactory.wrapRequest(sql, SORTING);
            if(( sql.getOffset() != 0 || sql.getCount() != -1) ){
                WrapperFactory.wrapRequest(sql, PAGING);
            }
            rs = select(context, ps, sql, false);
            result = handler.handle(rs, sql, context);
        } finally {
            DBUtils.close(rs);
            DBUtils.close(ps);
        }
        return result;
    }

    /**
     * Выполняет замену названий переменных на '?', подстановку параметров,
     * построение и выполнение {@link PreparedStatement}
     * @title Замена названий переменных на '?', подстановка переменных, постоение и выполнение скомпилированного sql-запроса
     * @param context
     * @param query
     * @return Результат выполнения SQL-запроса
     * @throws SQLException
     */
    private ResultSet select(QueryContext context, PreparedStatement ps,
                             Query query, boolean update) throws SQLException{
        final ParsedQuery pq = parser.parse(query, new StatementFunction(query));
        final List<ParsedParameter> params = pq.getParameters();
        final String sql = pq.getQuery();
        LOG.debug("SQL: {}", query);
        ps = context.getConnection().prepareStatement(sql,
            ResultSet.TYPE_FORWARD_ONLY,
            update? ResultSet.CONCUR_UPDATABLE : ResultSet.CONCUR_READ_ONLY);
        HandlersCommon.subtituteParams(ps, params, context);
        //выполняем запрос
        return ps.executeQuery();
    }
}
