package ru.bio4j.ng.commons.types;

public class ParamAlredyExistsException extends Exception {
	private static final long serialVersionUID = -5722681505112570307L;

	public ParamAlredyExistsException(String paramName) {
		super(String.format("Parameter with name [%s] alredy exists.", paramName));
	}
}
