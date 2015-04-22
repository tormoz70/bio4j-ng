package ru.bio4j.ng.commons.collections;

import java.io.Serializable;
import java.util.AbstractList;

/**
 * Неизменяемый список, полностью аналогичен по действию
 * {@link java.util.Collections#unmodifiableList(java.util.List) } за исключением
 * того, что <i>использует переданный в конструктора массив без копирования</i>.
 * @title Неизменяемый список
 */
public final class UnmodifiableList<T> extends AbstractList<T> implements Serializable {

    private final T values[];

    /**
     * <b>Конструктор не копирует массив</b>
     * @param values массив который будет использоваться в списке, только для чтения.
     */
    public UnmodifiableList(T...values) {
        this.values = values;
    }

    /**
     * @title Получение элемента списка по индексу
     * @param index
     * @return Элемент списка
     */
    @Override
    public T get(int index) {
        return values[index];
    }

    /**
     * @title Получение размера списка
     * @return Размер списка
     */
    @Override
    public int size() {
        return values.length;
    }
}