package ru.bio4j.service.sql.util;

public interface Closeable extends java.io.Closeable {

    /**
     * @title Закрытие потока и освобождение всех системных ресурсов, ассоциированных с ним
     */
    @Override
    void close();
}