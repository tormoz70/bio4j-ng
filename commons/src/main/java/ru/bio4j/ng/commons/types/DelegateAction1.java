package ru.bio4j.ng.commons.types;

public interface DelegateAction1<T, R> {
    R callback(T item) throws Exception;
}
