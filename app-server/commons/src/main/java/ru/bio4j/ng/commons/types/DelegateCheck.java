package ru.bio4j.ng.commons.types;


public interface DelegateCheck<T> extends DelegateAction<T, Boolean> {
	Boolean callback(T item);
}
