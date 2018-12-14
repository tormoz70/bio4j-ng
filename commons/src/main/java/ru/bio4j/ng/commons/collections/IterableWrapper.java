package ru.bio4j.ng.commons.collections;

import java.util.Iterator;

/**
 * Обертка над итератором для использования его вместо реализации iterable,
 * очевидно, что обертка допускает только одну итерацию.
 *
 */
public final class IterableWrapper<T> implements Iterable<T> {
    private final Iterator<T> iterator;

    public IterableWrapper(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public Iterator<T> iterator() {
        return iterator;
    }
}