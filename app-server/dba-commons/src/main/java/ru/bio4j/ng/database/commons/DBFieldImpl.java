package ru.bio4j.ng.database.commons;

import ru.bio4j.ng.database.api.DBField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created with IntelliJ IDEA.
 * User: ayrat
 * Date: 02.12.13
 * Time: 18:17
 * To change this template use File | Settings | File Templates.
 */
public class DBFieldImpl implements DBField {
    private static final Logger LOG = LoggerFactory.getLogger(DBFieldImpl.class);

    private String name;
    private Class<?> type;
    private int sqlType;
    private int id;

    public DBFieldImpl(Class<?> type, int id, String name, int sqlType) {
        this.id = id;
        this.type = type;
        this.name = name.toUpperCase();
        this.sqlType = sqlType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getSqlType() {
        return this.sqlType;
    }

    @Override
    public Class<?> getType() {
        return this.type;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

}
