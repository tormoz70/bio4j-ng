package ru.bio4j.ng.database.api;

public enum WrapMode {
    NONE((byte)0), FILTER((byte)1), SORT((byte)2), PAGINATION((byte)4), ALL((byte)7);
    private byte code;
    private WrapMode(byte code) {
        this.code = code;
    }
    public byte code() {
        return code;
    }
}
