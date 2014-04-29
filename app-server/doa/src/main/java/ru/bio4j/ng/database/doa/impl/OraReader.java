package ru.bio4j.ng.database.doa.impl;

import ru.bio4j.ng.commons.converter.ConvertValueException;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.Field;
import ru.bio4j.ng.database.api.SQLReader;

import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayrat on 24.04.14.
 */
public class OraReader implements SQLReader {
    public static final int FETCH_ROW_LIMIT = 10*10^6; // Максимальное кол-во записей, которое может вернуть запрос к БД (10 млн)

    private long currentFetchedRowPosition = 0L;
    private ResultSet resultSet;
    private List<Field> fields;
    private List<Object> rowValues;

    public OraReader(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    private static String readClob(Clob clob) throws Exception {
        String result = null;
        Reader is = clob.getCharacterStream();
        StringBuffer sb = new StringBuffer();
        int length = (int) clob.length();
        if (length > 0) {
            char[] buffer = new char[length];
            try {
                while (is.read(buffer) != -1)
                    sb.append(buffer);
                result = new String(sb);
            } catch (IOException e) {
                new SQLException(e);
            }
        }
        return result;
    }

    @Override
    public boolean read() throws Exception {
        if(resultSet == null)
            throw new IllegalArgumentException("ResultSet must be defined!");
        if(resultSet.next()){
            if(this.fields == null) {
                ResultSetMetaData metadata = resultSet.getMetaData();
                this.rowValues = new ArrayList<>(metadata.getColumnCount());
                for (int i = 0; i < metadata.getColumnCount(); i++)
                    this.rowValues.add(null);

                this.fields = new ArrayList<>();
                for (int i = 1; i <= metadata.getColumnCount(); i++) {
                    Class<?> type = null;
                    try {
                        type = getClass().getClassLoader().loadClass(metadata.getColumnClassName(i));
                    } catch (ClassNotFoundException ex) {
                        throw new SQLException(ex);
                    }
                    String fieldName =  metadata.getColumnName(i);
                    int sqlType = metadata.getColumnType(i);
                    Field field = new FieldImpl(type, i, fieldName, sqlType);
                    this.fields.add(field);
                }
            }
            for (Field field : this.fields) {
                int valueIndex = field.getId() - 1;
                Object value;
                if(field.getSqlType() == Types.CLOB){
                    value = readClob(resultSet.getClob(field.getId()));
                } else
                    value = resultSet.getObject(field.getId());
                this.rowValues.set(valueIndex, value);
            }
            return true;
        }
        return false;
    }

    @Override
    public ResultSet getResultSet() {
        return resultSet;
    }

    public void close() throws Exception {
        final ResultSet rsltSet = this.resultSet;
        if(rsltSet != null)
            rsltSet.close();
    }

    @Override
    public List<Field> getFields() {
        return this.fields;
    }

    @Override
    public Long getRowPos() {
        return this.currentFetchedRowPosition;
    }

    @Override
    public boolean isDBNull(String fieldName) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public boolean isDBNull(int fieldId) {
        throw new IllegalArgumentException("Not implemented");
    }

    private final String EXMSG_FieldNotFound = "Поле %s не найдено!";
    private final String EXMSG_IndexOutOfBounds = "Индекс [%d] за пределами диапазона!";
    private final String EXMSG_ParamIsNull = "Обязательный параметр [%s] пуст!";

    @Override
    public Field getField(String fieldName) {
        if (Strings.isNullOrEmpty(fieldName))
            throw new IllegalArgumentException(String.format(EXMSG_ParamIsNull, "fieldName"));
        for(Field f : fields)
            if(f.getName().toUpperCase().equals(fieldName.toUpperCase()))
                return f;
        return null;
    }
    @Override
    public Field getField(int fieldId) {
        if((fieldId > 0) && (fieldId <= this.rowValues.size()))
            return this.fields.get(fieldId - 1);
        throw new IllegalArgumentException(String.format(EXMSG_IndexOutOfBounds, fieldId));
    }

    @Override
    public <T> T getValue(int fieldId, Class<T> type) throws SQLException {
        if((fieldId > 0) && (fieldId <= this.rowValues.size())) {
            try {
                return Converter.toType(this.rowValues.get(fieldId - 1), type);
            } catch (ConvertValueException e) {
                throw new SQLException(e);
            }
        }
        throw new IllegalArgumentException(String.format(EXMSG_IndexOutOfBounds, fieldId));
    }

    @Override
    public <T> T getValue(String fieldName, Class<T> type) throws SQLException {
        if(Strings.isNullOrEmpty(fieldName))
            throw new IllegalArgumentException(String.format(EXMSG_ParamIsNull, "fieldName"));

        Field fld = this.getField(fieldName);
        if(fld != null)
            return getValue(fld.getId(), type);
        else
            throw new IllegalArgumentException(String.format(EXMSG_FieldNotFound, fieldName));
    }

    @Override
    public Object getValue(int fieldId) {
        if((fieldId > 0) && (fieldId <= this.rowValues.size()))
            return this.rowValues.get(fieldId - 1);
        else
            throw new IllegalArgumentException(String.format(EXMSG_IndexOutOfBounds, fieldId));
    }

    @Override
    public Object getValue(String fieldName) {
        if(Strings.isNullOrEmpty(fieldName))
            throw new IllegalArgumentException(String.format(EXMSG_ParamIsNull, "fieldName"));

        Field fld = this.getField(fieldName);
        if(fld != null)
            return getValue(fld.getId());
        else
            throw new IllegalArgumentException(String.format(EXMSG_FieldNotFound, fieldName));

    }

    @Override
    public List<Object> gerValues() {
        return this.rowValues;
    }
}
