package ru.bio4j.service.sql.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.*;

/**
 * Обработчик BLOB полей. Значение возвращенное им допустимо использовать
 * только в пределах транзакции.
 * @title Обработчик BLOB полей
 * @author rad
 */
@HandledTypes(
    metaType ="blob",
    java={byte[].class},
    sql={BLOB, BINARY, OTHER}
)
public final class BytesHandler extends AbstractTypeHandler<byte[]> {

    /**
     * @title Чтение значения BLOB типа
     * @param resultSet
     * @param column с 1, как приянто в jdbc
     * @param type
     * @return Значение BLOB типа
     * @throws Exception
     */
    @Override
    public byte[] read(ResultSet resultSet,
                       int column,
                       Class<byte[]> type,
                       String sqlType) {
        try {
      /*Blob BLOB = resultSet.getBlob(column);
      InputStream is = BLOB.getBinaryStream();
      try{
      return IOUtils.toByteArray(is);
      } finally {
      IOUtils.closeQuietly(is);
      }*/
            return resultSet.getBytes(column);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @title Запись значения BLOB типа
     * @param resultSet
     * @param value
     * @param column с 1, как приянто в jdbc
     * @param type
     * @throws Exception
     */
    @Override
    public void write(ResultSet resultSet,
                      Object value,
                      int column,
                      Class<byte[]> type,
                      String sqlType) {
        byte[] b = toBytes(value);
        try {
            resultSet.updateBytes(column, b);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @title Запись значения BLOB типа
     * @param statement
     * @param value
     * @param column с 1, как приянто в jdbc
     * @param type
     * @throws Exception
     */
    @Override
    public void write(PreparedStatement statement,
                      Object value,
                      int column,
                      Class<byte[]> type,
                      String sqlType) {
        byte[] b = toBytes(value);
        try {
            statement.setBytes(column, b);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @title Привдение значения к массиву байтов
     * @param value
     * @return Массив байтов
     */
    private byte[] toBytes(Object value) {
        //TODO from string & etc.
        return (byte[])value;
    }
}
