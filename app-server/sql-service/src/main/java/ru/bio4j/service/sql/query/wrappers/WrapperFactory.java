package ru.bio4j.service.sql.query.wrappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.func.Function;
import ru.bio4j.func.UnsafeFunction;
import ru.bio4j.service.sql.Query;
import ru.bio4j.service.sql.QueryContext;
import ru.bio4j.service.sql.query.wrappers.filtering.FilteringWrapper;
import ru.bio4j.service.sql.query.wrappers.pagination.PaginationWrapper;
import ru.bio4j.service.sql.query.wrappers.sorting.SortingWrapper;

import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;

import static ru.bio4j.collections.Sugar.func;

public class WrapperFactory {

    private static final Logger LOG = LoggerFactory.getLogger(WrapperFactory.class);

    private final Function<WrapQueryType, String> queryFunction;
    private final Function<WrapQueryType, Wrapper> wrapperFunction;

    public static class SingletonHolder {
        public static final WrapperFactory HOLDER_INSTANCE = new WrapperFactory();
    }

    public static WrapperFactory getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    /**
     * @param query         исходный запрос
     * @param wrapQueryType тип врапера
     * @return "Обернутый" запрос
     * @throws java.sql.SQLException
     * @title "Оборачивает" SQL запрос для возможности фильтрации
     */
    public static Query wrapRequest(final Query query, final WrapQueryType wrapQueryType) throws SQLException {
        return QueryContext.call(new UnsafeFunction<QueryContext, Query, SQLException>() {
            @Override
            public Query apply(QueryContext context) throws SQLException {
                Wrapper w = getWrapper(wrapQueryType);
                return w.wrap(context, query);
            }

        });
    }

    private WrapperFactory() {
        queryFunction = WrapperLoader.loadQueries();
        try {
            wrapperFunction = func(register(FilteringWrapper.class, PaginationWrapper.class, SortingWrapper.class));
        } catch (Exception e) {
            LOG.error("Can't build factory", e);
            throw new IllegalStateException("Can't build factory", e);
        }
    }

    private Map<WrapQueryType, Wrapper> register(Class<? extends Wrapper>... wrapperClass) throws Exception {
        final Map<WrapQueryType, Wrapper> typeWrapperMap = new EnumMap<>(WrapQueryType.class);
        for (Class<? extends Wrapper> wrapper : wrapperClass) {
            register(wrapper, typeWrapperMap);
        }
        return typeWrapperMap;
    }

    public final void register(Class<? extends Wrapper> wrapperClass, Map<WrapQueryType,
            Wrapper> typeWrapperMap) throws Exception {
        WrapperType handle = wrapperClass.getAnnotation(WrapperType.class);
        final String query = queryFunction.apply(handle.value());
        final Wrapper wrapper = wrapperClass.getConstructor(String.class).newInstance(query);
        typeWrapperMap.put(handle.value(), wrapper);

    }

    private static Wrapper getWrapper(WrapQueryType wrapQueryType) throws SQLException {
        return getInstance().wrapperFunction.apply(wrapQueryType);
    }
}
