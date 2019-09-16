package ru.bio4j.ng.database.api;

import java.sql.ResultSet;
import java.util.List;

/**
 * Created by ayrat on 24.04.14.
 */
public interface SQLReader {

    boolean next(final ResultSet resultSet);

    List<DBField> getFields();

    long getRowPos();

    boolean isFirstRow();

    DBField getField(int fieldId);
    DBField getField(String fieldName);

    boolean isDBNull(String fieldName);
    boolean isDBNull(int fieldId);

    <T> T getValue(String fieldName, Class<T> type);
    <T> T getValue(int fieldId, Class<T> type);

    Object getValue(String fieldName);
    Object getValue(int fieldId);

    List<Object> getValues();
}
