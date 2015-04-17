package ru.bio4j.ng.database.api;

public interface DBField {

	String getName();

	int getSqlType();

	Class<?> getType();

	Integer getId();
}
