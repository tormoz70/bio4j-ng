package ru.bio4j.ng.model.transport.jstore;

public enum Alignment {
	LEFT, CENTER, RIGHT, STRETCH;

    public int getCode() {
        return this.ordinal();
    }

}
