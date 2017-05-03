package ru.bio4j.ng.commons.types;


public interface DelegateCheck<T> extends DelegateAction1<T, Boolean> {
	Boolean callback(T item) throws Exception;
}
