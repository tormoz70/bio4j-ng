package ru.bio4j.service.sql.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Утилиты иницаилизации {@link DataSource} субд
 * @title Утилиты иницаилизации источника данных субд
 * @author rad
 */
public final class Drivers {

    private Drivers() {
    }

    /**
     * @title Создание источника данных субд
     * @param dataSourceName
     * @return Источник данных
     */
    public static DataSource createDataSource(String dataSourceName) {
        try {
            return InitialContext.doLookup(dataSourceName);
        } catch(NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
