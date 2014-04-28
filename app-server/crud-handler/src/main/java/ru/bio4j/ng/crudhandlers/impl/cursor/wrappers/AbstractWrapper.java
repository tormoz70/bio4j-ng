package ru.bio4j.ng.crudhandlers.impl.cursor.wrappers;

/**
 * Base class for all cursor.wrapper
 */
public abstract class AbstractWrapper implements Wrapper {

    public static final String QUERY = "${QUERY_PLACEHOLDER}";
    protected WrapperInterpreter wrapperInterpreter;

    public AbstractWrapper(String template) {
        this.parseTemplate(template);
    }

    /**
     * @title Разбор запроса
     * @param template
     */
    protected abstract void parseTemplate(String template);

    public void setWrapperInterpreter(WrapperInterpreter wrapperInterpreter) {
        this.wrapperInterpreter = wrapperInterpreter;
    }
}
