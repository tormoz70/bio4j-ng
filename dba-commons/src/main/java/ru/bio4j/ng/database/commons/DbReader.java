package ru.bio4j.ng.database.commons;

import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.DBField;
import ru.bio4j.ng.database.api.BioSQLException;
import ru.bio4j.ng.database.api.SQLReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ayrat on 24.04.14.
 */
public class DbReader implements SQLReader {
    public static final int FETCH_ROW_LIMIT = 10*10^6; // Максимальное кол-во записей, которое может вернуть запрос к БД (10 млн)

    protected long currentFetchedRowPosition = 0L;
    protected final List<DBField> fields = new ArrayList<>();
    protected final List<Object> rowValues = new ArrayList<>();

    protected String readClob(Clob clob) throws SQLException {
        String result = null;
        if(clob != null) {
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
        }
        return result;
    }

    protected byte[] readBlob(InputStream inputStream) throws SQLException {
        byte[] bFile = new byte[0];
        try {
            bFile = new byte[inputStream.available()];
            inputStream.read(bFile);
            inputStream.close();
        } catch (IOException e) {
            throw new SQLException(e);
        }
        return bFile;
    }

    @Override
    public boolean next(final ResultSet resultSet) {
        if(resultSet == null)
            throw new IllegalArgumentException("ResultSet must be defined!");
        try {
            if (resultSet.next()) {
                currentFetchedRowPosition++;
                fields.clear();
                rowValues.clear();
                ResultSetMetaData metadata = resultSet.getMetaData();
                for (int i = 1; i <= metadata.getColumnCount(); i++) {
                    Class<?> type = null;
                    int sqlType = metadata.getColumnType(i);
                    try {
                        String className = metadata.getColumnClassName(i);
                        if ((sqlType == Types.BLOB) || (sqlType == Types.BINARY))
                            type = Byte[].class;
                        else if (sqlType == Types.CLOB)
                            type = String.class;
                        else
                            type = getClass().getClassLoader().loadClass(className);
                    } catch (ClassNotFoundException ex) {
                        throw new SQLException(ex);
                    }
                    String fieldName = metadata.getColumnName(i);
                    DBField field = new DBFieldImpl(type, i, fieldName, sqlType);
                    fields.add(field);
                }
                for (int i = 0; i < fields.size(); i++)
                    rowValues.add(null);
                for (DBField field : fields) {
                    int valueIndex = field.getId() - 1;
                    Object value;
                    int sqlType = field.getSqlType();
                    if (sqlType == Types.CLOB) {
                        value = readClob(resultSet.getClob(field.getId()));
                    } else if (Arrays.asList(Types.BLOB, Types.BINARY).contains(sqlType)) {
                        value = readBlob(resultSet.getBinaryStream(field.getId()));
                    } else
                        value = resultSet.getObject(field.getId());
                    rowValues.set(valueIndex, value);
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw BioSQLException.create(e);
        }
    }

    @Override
    public List<DBField> getFields() {
        return this.fields;
    }

    @Override
    public long getRowPos() {
        return this.currentFetchedRowPosition;
    }

    @Override
    public boolean isFirstRow() {
        return this.currentFetchedRowPosition == 0;
    }

    @Override
    public boolean isDBNull(String fieldName) {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public boolean isDBNull(int fieldId) {
        throw new IllegalArgumentException("Not implemented");
    }

    private final String EXMSG_FieldNotFound = "Field %s not found!";
    private final String EXMSG_IndexOutOfBounds = "Index [%d] out of range!";
    private final String EXMSG_ParamIsNull = "Required parameter [%s] is null!";

    @Override
    public DBField getField(String fieldName) {
        if (Strings.isNullOrEmpty(fieldName))
            throw new IllegalArgumentException(String.format(EXMSG_ParamIsNull, fieldName));
        for(DBField f : fields)
            if(f.getName().toUpperCase().equals(fieldName.toUpperCase()))
                return f;
        return null;
    }
    @Override
    public DBField getField(int fieldId) {
        if((fieldId > 0) && (fieldId <= this.rowValues.size()))
            return this.fields.get(fieldId - 1);
        throw new IllegalArgumentException(String.format(EXMSG_IndexOutOfBounds, fieldId));
    }

    @Override
    public <T> T getValue(int fieldId, Class<T> type) {
        if((fieldId > 0) && (fieldId <= this.rowValues.size())) {
            return Converter.toType(this.rowValues.get(fieldId - 1), type);
        }
        throw new IllegalArgumentException(String.format(EXMSG_IndexOutOfBounds, fieldId));
    }

    @Override
    public <T> T getValue(String fieldName, Class<T> type) {
        if(Strings.isNullOrEmpty(fieldName))
            throw new IllegalArgumentException(String.format(EXMSG_ParamIsNull, fieldName));

        DBField fld = this.getField(fieldName);
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
            throw new IllegalArgumentException(String.format(EXMSG_ParamIsNull, fieldName));

        DBField fld = this.getField(fieldName);
        if(fld != null)
            return getValue(fld.getId());
        else
            throw new IllegalArgumentException(String.format(EXMSG_FieldNotFound, fieldName));

    }

    @Override
    public List<Object> getValues() {
        return this.rowValues;
    }

}
