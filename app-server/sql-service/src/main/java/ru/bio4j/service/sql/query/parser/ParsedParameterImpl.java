package ru.bio4j.service.sql.query.parser;

import ru.bio4j.collections.Parameter;

/**
 * @title Обработанный параметр
 */
public class ParsedParameterImpl implements ParsedParameter {

  /**
   * @title Строитель обработанного параметра
   */  
  public static class Builder {
    private String name;
    private boolean input;
    private boolean output;
    //TODO: удалить значения для возможности кешировать
    private Parameter parameter;
    private int position;
    private Integer sqlType;
    private String sqlTypeName;
    private String metaType;

    /**
     * @title Установка 
     * @param input
     * @return Ссылка на экземпляр строителя обработанного параметра
     */
    public Builder input(boolean input) {
      this.input = input;
      return this;
    }

    /**
     * @title Установка метатипа и получение экземпляра строителя
     * @param metaType
     * @return Ссылка на экземпляр строителя обработанного параметра
     */
    public Builder metaType(String metaType) {
      this.metaType = metaType;
      return this;
    }

    /**
     * @title Установка имени и получение экземпляра строителя
     * @param name
     * @return Ссылка на экземпляр строителя обработанного параметра
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * @title
     * @param output
     * @return Ссылка на экземпляр строителя обработанного параметра
     */
    public Builder output(boolean output) {
      this.output = output;
      return this;
    }

    /**
     * @title Установка параметра запроса и получение экземпляра строителя
     * @param parameter
     * @return Ссылка на экземпляр строителя обработанного параметра
     */
    public Builder parameter(Parameter parameter) {
      this.parameter = parameter;
      return this;
    }

    /**
     * @title Установка позиции в запросе и получение экземпляра строителя
     * @param position
     * @return Ссылка на экземпляр строителя обработанного параметра
     */
    public Builder position(int position) {
      this.position = position;
      return this;
    }

    /**
     * @title Установка sql-типа и получение экземпляра строителя
     * @param sqlType
     * @return Ссылка на экземпляр строителя обработанного параметра
     */
    public Builder sqlType(Integer sqlType) {
      this.sqlType = sqlType;
      return this;
    }

    /**
     * @title Установка имени sql-типа и получение экземпляра строителя
     * @param sqlTypeName
     * @return Ссылка на экземпляр строителя обработанного параметра
     */
    public Builder sqlTypeName(String sqlTypeName) {
      this.sqlTypeName = sqlTypeName;
      return this;
    }
     
    /**
     * @title Построение обработанного параметра
     * @return Обработанный параметр
     */
    public ParsedParameterImpl build() {
      return new ParsedParameterImpl(name, parameter, position, input, output, sqlType, sqlTypeName, metaType);
    }
  }
  
  private final String name;
  private final boolean input;
  private final boolean output;
  private final Parameter parameter;
  private final int position;
  private final Integer sqlType;
  private final String sqlTypeName;
  private final String metaType;

  /**
   * @title Получение строителя обработанного параметра
   * @return Экземпляр строителя обработанного параметра
   */
  public static Builder builder() {
    return new Builder();
  }
  
  public ParsedParameterImpl(
      String name,
      Parameter parameter,
      int position,
      boolean input,
      boolean output,
      Integer sqlType,
      String sqlTypeName, 
      String metaType) {
    this.name = name;
    this.input = input;
    this.output = output;
    this.parameter = parameter;
    this.position = position;
    this.sqlType = sqlType;
    this.sqlTypeName = sqlTypeName;
    this.metaType = metaType;
  }

  /**
   * @title Получение имени обработанного параметра
   * @return Имя
   */  
  @Override
  public String getName() {
    return name;
  }

  /**
   * @title Получение параметра запроса
   * @return Параметр запроса
   */
  @Override
  public Parameter getParameter() {
    return parameter;
  }

  /**
   * Положение в запросе, индекс как он подставляется в PreparedStatement 
   * (первый аргумент {@link java.sql.PreparedStatement#setObject(int, java.lang.Object) } и подобных методов ) 
   * @title Получение положения в запросе
   * @return Положение в запросе
   */
  @Override
  public int getPosition() {
    return position;
  }

  /**
   * @title
   * @return 
   */
  @Override
  public boolean isInput() {
    return input;
  }

  /**
   * @title
   * @return 
   */
  @Override
  public boolean isOutput() {
    return output;
  }

  /**
   * @title Получение sql-типа
   * @return SQL-тип
   */
  @Override
  public Integer getSqlType() {
    return sqlType;
  }

  /**
   * @title Получение имени sql-типа
   * @return Имя SQL-типа
   */
  @Override
  public String getSqlTypeName() {
    return sqlTypeName;
  }

  /**
   * @title Получение метатипа
   * @return Метатип
   */
  @Override
  public String getMetaType() {
    return metaType;
  }
  
  /**
   * @title Преобразование к строке
   * @return Текстовое представление обработанного параметра
   */
  @Override
  public String toString() {
    return "ParsedParameterImpl{" + "name=" + name + ", input=" + input + ", output=" + output +
            ", parameter=" + parameter + ", position=" + position + ", sqlType=" + sqlType + ", sqlTypeName=" + sqlTypeName + '}';
  }
}
