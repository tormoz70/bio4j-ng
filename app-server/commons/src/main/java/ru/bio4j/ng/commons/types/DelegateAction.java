package ru.bio4j.ng.commons.types;

public interface DelegateAction<T, R> {
    R callback(T item);
}
