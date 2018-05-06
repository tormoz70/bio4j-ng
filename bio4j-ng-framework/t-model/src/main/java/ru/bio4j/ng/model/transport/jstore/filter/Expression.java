package ru.bio4j.ng.model.transport.jstore.filter;

import java.util.List;

/**
 * Фильтр...
 * Ну так вот например:
 *      and(
 *          eq(f1, 1),
 *          or(eq(r, 1), eq(r, 2))
 *      )
 *    это значит: (f1=1) and ((r=1) or (r=2))
 */
public interface Expression {

    /**
     * Имя которое идентифицирует выражение в текстовом представлении (Например OR)
     */
    public String getName();

    /**
     * @title Получение списка аргументов
     * @return Список аргументов
     */
    List<Expression> getChildren();

    /**
     * Добавить аргумент для логических выражений (для построения в runtime)
     * @param expression
     * @return
     */
    Expression add(Expression expression);

    Expression addAll(List<Expression> expressions);

    /**
     * Не учитывать регистр при сравнении строк (для операции сравнения)
     * @return
     */
    boolean ignoreCase();

    /**
     * Имя поля в операции сравнения
     * @return
     */
    String getColumn();

    /**
     * Сравниваемое значение в операции сравнения
     * @return
     */
    Object getValue();
}
