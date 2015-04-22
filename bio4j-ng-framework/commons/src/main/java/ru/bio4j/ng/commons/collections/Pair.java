package ru.bio4j.ng.commons.collections;

/**
 * пара
 * @title Пара
 *
 */
public interface Pair<L, R> {

    /**
     * @title Получение левой части пары
     * @return Левая часть пары
     */
    L getLeft();

    /**
     * @title Получение правой части пары
     * @return Правая часть пары
     */
    R getRight();
}