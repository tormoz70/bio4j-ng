package ru.bio4j.service.sql.query.parser;

import ru.bio4j.collections.Parameter;

/**
 * Обработанный параметр
 * @title Интерфейс обработанного параметра
 * @author rad
 */
public interface ParsedParameter {
    
  /**
   * @title Получение имени параметра
   * @return Имя параметра
   */  
  String getName();
  
  /**
   * @title Получение параметра запроса
   * @return Параметр запроса
   */
  //TODO: удалить значения для возможности кешировать
  Parameter getParameter();
  /**
   * Положение в запросе, индекс как он подставляется в PreparedStatement 
   * (первый аргумент {@link java.sql.PreparedStatement#setObject(int, java.lang.Object) } и подобных методов ) 
   * @title Получение положения в запросе
   * @return Положение в запросе
   */
  int getPosition();
  
  /**
   * @title
   * @return 
   */
  boolean isInput();
  
  /**
   * @title
   * @return 
   */
  boolean isOutput();
  
  /**
   * @title Получение sql-типа
   * @return SQL-тип
   */
  Integer getSqlType();
  
  /**
   * @title Получение имени sql-типа
   * @return Имя SQL-типа
   */
  String getSqlTypeName();

  /**
   * @title Получение метатипа
   * @return Метатип
   */
  public String getMetaType();
}
