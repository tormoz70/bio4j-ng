package ru.bio4j.ng.crudhandlers.impl.cursor.wrappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.filtering.FilteringWrapper;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.pagination.PaginationWrapper;
import ru.bio4j.ng.crudhandlers.impl.cursor.wrappers.sorting.SortingWrapper;
import ru.bio4j.ng.service.api.BioCursor;

import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;

public class Wrappers {

    private static final Logger LOG = LoggerFactory.getLogger(Wrappers.class);

    private Map<WrapQueryType, String> templates = null;
    private Map<WrapQueryType, Wrapper> wrappers = null;

    public static class SingletonHolder {
        public static final Wrappers HOLDER_INSTANCE = new Wrappers();
    }

    public static Wrappers getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    /**
     * @param cursor        исходный запрос
     * @param wrapQueryType тип врапера
     * @return "Обернутый" запрос
     * @throws java.sql.SQLException
     * @title "Оборачивает" SQL запрос для возможности фильтрации
     */
    public static BioCursor wrapRequest(final BioCursor cursor, final WrapQueryType wrapQueryType) throws Exception {
        Wrapper w = getWrapper(wrapQueryType);
        return w.wrap(cursor);
    }

    private Wrappers() {
    }

    public void init(String dbmsName) throws Exception {
        LOG.debug("Wrapper initializing for \"{}\" database...", dbmsName);
        templates = WrapperLoader.loadQueries(dbmsName);
        wrappers = register(FilteringWrapper.class, PaginationWrapper.class, SortingWrapper.class);
        LOG.debug("Wrapper initialized.");
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

    private static Wrapper getWrapper(WrapQueryType wrapQueryType) throws SQLException {
        return getInstance().wrappers.get(wrapQueryType);
    }
}
