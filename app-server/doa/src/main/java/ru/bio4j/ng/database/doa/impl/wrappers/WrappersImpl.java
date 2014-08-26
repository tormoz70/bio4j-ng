package ru.bio4j.ng.database.doa.impl.wrappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.database.doa.impl.oracle.OracleWrapperInterpreter;
import ru.bio4j.ng.database.doa.impl.wrappers.filtering.FilteringWrapper;
import ru.bio4j.ng.database.doa.impl.wrappers.filtering.GetrowWrapper;
import ru.bio4j.ng.database.doa.impl.wrappers.pagination.LocateWrapper;
import ru.bio4j.ng.database.doa.impl.wrappers.pagination.PaginationWrapper;
import ru.bio4j.ng.database.doa.impl.wrappers.pagination.TotalsWrapper;
import ru.bio4j.ng.database.doa.impl.wrappers.sorting.SortingWrapper;

import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;

public class WrappersImpl implements Wrappers {

    private static final Logger LOG = LoggerFactory.getLogger(WrappersImpl.class);

    private Map<WrapQueryType, String> templates = null;
    private Map<WrapQueryType, Wrapper> wrappers = null;

    /**
     * @param cursor        исходный запрос
     * @param wrapQueryType тип врапера
     * @return "Обернутый" запрос
     * @throws java.sql.SQLException
     * @title "Оборачивает" SQL запрос для возможности фильтрации
     */
    private BioCursor wrapCursor(final BioCursor cursor, final WrapQueryType wrapQueryType) throws Exception {
        Wrapper w = getWrapper(wrapQueryType);
        return w.wrap(cursor);
    }

    @Override
    public BioCursor wrapCursor(final BioCursor cursor) throws Exception {
        wrapCursor(cursor, WrapQueryType.FILTERING);
        wrapCursor(cursor, WrapQueryType.TOTALS);
        wrapCursor(cursor, WrapQueryType.SORTING);
        wrapCursor(cursor, WrapQueryType.LOCATE);
        wrapCursor(cursor, WrapQueryType.PAGING);
        wrapCursor(cursor, WrapQueryType.GETROW);
        return cursor;
    }


    public WrappersImpl(String dbmsName) throws Exception {
        LOG.debug("Wrappers initializing for \"{}\" database...", dbmsName);
        templates = WrapperLoader.loadQueries(dbmsName);
        wrappers = register(
                FilteringWrapper.class,
                SortingWrapper.class,
                PaginationWrapper.class,
                TotalsWrapper.class,
                LocateWrapper.class,
                GetrowWrapper.class
        );
        LOG.debug("Wrappers initialized.");
    }

    private Map<WrapQueryType, Wrapper> register(Class<? extends Wrapper> ... wrapperClass) throws Exception {
        final Map<WrapQueryType, Wrapper> typeWrapperMap = new EnumMap<>(WrapQueryType.class);
        for (Class<? extends Wrapper> wrapper : wrapperClass) {
            register(wrapper, typeWrapperMap);
        }
        return typeWrapperMap;
    }

    public final void register(Class<? extends Wrapper> wrapperClass, Map<WrapQueryType,
            Wrapper> typeWrapperMap) throws Exception {
        WrapperType handle = wrapperClass.getAnnotation(WrapperType.class);
        final String query = templates.get(handle.value());
        final Wrapper wrapper = wrapperClass.getConstructor(String.class).newInstance(query);
        //TODO Тут надо сделать Mapper с привязкой к имени СУБД
        ((AbstractWrapper)wrapper).setWrapperInterpreter(new OracleWrapperInterpreter());
        typeWrapperMap.put(handle.value(), wrapper);

    }

    private Wrapper getWrapper(WrapQueryType wrapQueryType) throws SQLException {
        return wrappers.get(wrapQueryType);
    }
}
