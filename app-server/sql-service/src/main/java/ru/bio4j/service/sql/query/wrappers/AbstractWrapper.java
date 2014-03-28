package ru.bio4j.service.sql.query.wrappers;

/**
 * Base class for all wrappers
 */
public abstract class AbstractWrapper implements Wrapper {

    public static final String QUERY = "$QUERY";

    public AbstractWrapper(String query) {
        this.parseQuery(query);
    }

    /**
     * @title Разбор запроса
     * @param query
     */
    protected abstract void parseQuery(String query);

}
