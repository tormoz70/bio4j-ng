package ru.bio4j.service.sql.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBTools {

    private DBTools() {}

    /**
     * @title Освобождение ресурсов, связанных с выражением объекта базы данный
     * @param c
     * @return Ошибка, если она возникла при освобождении ресурсов, или null
     */
    public static Throwable close(Statement c) {
        if(c != null) {
            try {
                c.close();
            } catch(Throwable t) {
                return t;
            }
        }
        return null;
    }

    /**
     * @title Освобождение ресурсов, связанных с результирующим набором данных
     * @param c
     * @return Ошибка, если она возникла при освобождении ресурсов, или null
     */
    public static Throwable close(ResultSet c) {
        if(c != null) {
            try {
                c.close();
            } catch(Throwable t) {
                return t;
            }
        }
        return null;
    }

    /**
     * @title Закрытие соединения и освобождение всех системных ресурсов связанных с ним
     * @param c
     * @return Ошибка, если она возникла при освобождении ресурсов, или null
     */
    public static Throwable close(Connection c) {
        if(c != null) {
            try {
                c.close();
            } catch(Throwable t) {
                return t;
            }
        }
        return null;
    }

    /**
     * Убирается все после первого пробела и приводит название к нижнему регистру
     * @title Удаление из строки всего содержимого после первого пробела и приведение оставшегося в нижний регистр
     * @param productName
     * @return Отредактированная строка
     */
    public static String escapeProductName(String productName) {
        if(productName == null) {
            return productName;
        }
        int i = productName.indexOf(' ');
        if(i > 0) {
            //отрезаем все просле пробела
            productName = productName.substring(0, i);
        }
        return productName.toLowerCase();
    }
}
