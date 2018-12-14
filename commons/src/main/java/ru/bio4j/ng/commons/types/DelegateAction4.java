package ru.bio4j.ng.commons.types;

public interface DelegateAction4<T1, T2, T3, T4, R> {
    R callback(T1 param1, T2 param2, T3 param3, T4 param4) throws Exception;
}
