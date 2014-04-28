package ru.bio4j.ng.database.api;

import java.sql.ResultSet;
import java.util.List;

/**
 * Created by ayrat on 24.04.14.
 */
public interface SQLReader {
    boolean read() throws Exception;

    ResultSet getResultSet();

    List<Field> getFields();

    Long getRowPos();

    Field getField(int fieldId);
    Field getField(String fieldName);

    boolean isDBNull(String fieldName);
    boolean isDBNull(int fieldId);

    <T> T getValue(String fieldName, Class<T> type) throws Exception;
    <T> T getValue(int fieldId, Class<T> type) throws Exception;

    Object getValue(String fieldName);
    Object getValue(int fieldId);

    void close() throws Exception;

    List<Object> gerValues();
}
