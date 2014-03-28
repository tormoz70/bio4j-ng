package ru.bio4j.service.sql.query;

import ru.bio4j.service.sql.QueryContext;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Фабрика соединений, вызвается из метода {@link ru.bio4j.service.sql.QueryContext#open() },
 * при этом можно также инициализировать аттрибуты контекста
 * @title Интерфейс фабрики соединений
 * @author rad
 */
public interface ConnectionFactory {

    /**
     * @title Создание нового соединения с базой данных
     * @param qc
     * @return Ссылка на новый экземпляр класса соединения
     * @throws SQLException
     */
    Connection newConnection(QueryContext qc) throws SQLException;

    /**
     * Вызывается перед закрытием содеинения,
     * предназначенн для обработки аттрибутов контекста, не для обработки соединения.
     * @title Закрытие соединения
     * @param qc
     */
    public void close(QueryContext qc);


    public void closeFactory();

    /**
     * Вызывается при откате транзакции в основном соединени,
     * предназначенн для обработки аттрибутов контекста, не для обработки соединения.
     * @title Откат транзакции в основном соединении
     * @param qc
     */
    public void rollback(QueryContext qc);

    /**
     * Вызывается при коммите транзакции в основном соединени,
     * предназначенн для обработки аттрибутов контекста, не для обработки соединения.
     * @title Коммит транзакции в основном соединении
     * @param qc
     */
    public void commit(QueryContext qc);
}