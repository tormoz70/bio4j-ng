package ru.bio4j.ng.database.commons;

import ru.bio4j.ng.commons.converter.ConvertValueException;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.DBField;
import ru.bio4j.ng.database.api.SQLReader;
import ru.bio4j.ng.model.transport.Param;

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

    private long currentFetchedRowPosition = 0L;
    private ResultSet resultSet;
    private List<DBField> fields;
    private List<Object> rowValues;

    public DbReader(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    private static String readClob(Clob clob) throws Exception {
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

    private static byte[] readBlob(InputStream inputStream) throws Exception {
        byte[] bFile = new byte[inputStream.available()];
        try {
            inputStream.read(bFile);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bFile;
    }

    @Override
    public boolean next() throws Exception {
        if(resultSet == null)
            throw new IllegalArgumentException("ResultSet must be defined!");
        if(resultSet.next()){
            if(this.fields == null) {
                ResultSetMetaData metadata = resultSet.getMetaData();

                this.fields = new ArrayList<>();
                for (int i = 1; i <= metadata.getColumnCount(); i++) {
                    Class<?> type = null;
                    int sqlType = metadata.getColumnType(i);
                    String sqlTypeName = DbUtils.getInstance().getSqlTypeName(sqlType);
                    try {
                        String className = metadata.getColumnClassName(i);
                        if((sqlType == Types.BLOB) || (sqlType == Types.BINARY))
                            type = Byte[].class;
                        else
                            type = getClass().getClassLoader().loadClass(className);
                    } catch (ClassNotFoundException ex) {
                        throw new SQLException(ex);
                    }
                    String fieldName =  metadata.getColumnName(i);
                    DBField field = new DBFieldImpl(type, i, fieldName, sqlType);
                    this.fields.add(field);
                }
            }
            this.rowValues = new ArrayList<>(this.fields.size());
            for (int i = 0; i < this.fields.size(); i++)
                this.rowValues.add(null);
            for (DBField field : this.fields) {
                int valueIndex = field.getId() - 1;
                Object value;
                int sqlType = field.getSqlType();
                String sqlTypeName = DbUtils.getInstance().getSqlTypeName(sqlType);
                if(sqlType == Types.CLOB) {
                    value = readClob(resultSet.getClob(field.getId()));
                } else if(Arrays.asList(Types.BLOB, Types.BINARY).contains(sqlType)){
                    value = readBlob(resultSet.getBinaryStream(field.getId()));
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
    public List<DBField> getFields() {
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
    public DBField getField(String fieldName) {
        if (Strings.isNullOrEmpty(fieldName))
            throw new IllegalArgumentException(String.format(EXMSG_ParamIsNull, "fieldName"));
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
            throw new IllegalArgumentException(String.format(EXMSG_ParamIsNull, "fieldName"));

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
