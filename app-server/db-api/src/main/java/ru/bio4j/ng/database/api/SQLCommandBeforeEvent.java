package ru.bio4j.ng.database.api;

/**
 * Created with IntelliJ IDEA.
 * User: ayrat
 * Date: 27.11.13
 * Time: 23:49
 * To change this template use File | Settings | File Templates.
 */
public interface SQLCommandBeforeEvent {
    void handle(SQLCommandBase sender, SQLCommandBeforeEventAttrs attrs);
}
