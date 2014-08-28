package ru.bio4j.ng.database.oracle.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.database.api.*;
import ru.bio4j.ng.database.commons.AbstractWrapper;
import ru.bio4j.ng.database.commons.WrapperLoader;
import ru.bio4j.ng.database.commons.wrappers.filtering.GetrowWrapper;
import ru.bio4j.ng.database.commons.wrappers.pagination.TotalsWrapper;
import ru.bio4j.ng.database.commons.wrappers.filtering.FilteringWrapper;
import ru.bio4j.ng.database.commons.wrappers.pagination.LocateWrapper;
import ru.bio4j.ng.database.commons.wrappers.pagination.PaginationWrapper;
import ru.bio4j.ng.database.commons.wrappers.sorting.SortingWrapper;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;

public class OraWrappersImpl implements Wrappers {

    private static final Logger LOG = LoggerFactory.getLogger(OraWrappersImpl.class);

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


    public OraWrappersImpl(String dbmsName) throws Exception {
        LOG.debug("Wrappers initializing for \"{}\" database...", dbmsName);
        final String templFileName = "/cursor/wrapper/templates/" + dbmsName + ".xml";
        final InputStream is = this.getClass().getResourceAsStream(templFileName);
        if(is == null)
            throw new IllegalArgumentException(String.format("Resource %s not found!", templFileName));
        templates = WrapperLoader.loadQueries(is, dbmsName);
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
        ((AbstractWrapper)wrapper).setWrapperInterpreter(new OraWrapperInterpreter());
        typeWrapperMap.put(handle.value(), wrapper);

    }

    private Wrapper getWrapper(WrapQueryType wrapQueryType) throws SQLException {
        return wrappers.get(wrapQueryType);
    }
}
