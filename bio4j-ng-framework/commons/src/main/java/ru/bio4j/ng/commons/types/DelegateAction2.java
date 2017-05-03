package ru.bio4j.ng.commons.types;

public interface DelegateAction2<T1, T2, R> {
    R callback(T1 param1, T2 param2) throws Exception;
}
