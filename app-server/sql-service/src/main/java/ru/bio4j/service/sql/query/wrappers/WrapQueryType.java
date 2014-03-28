package ru.bio4j.service.sql.query.wrappers;

public enum WrapQueryType {
    FILTERING("filtering"),
    SORTING("sorting"),
    PAGING("paging");

    private final String typeName;
    private WrapQueryType(String typeName) {
        this.typeName = typeName;
    }
    public String getTypeName() {
        return typeName;
    }

    public static WrapQueryType forName(String name) {
        for (WrapQueryType type : WrapQueryType.values()) {
              if(type.getTypeName().equals(name)) {
                  return type;
              }
        }
        throw new IllegalArgumentException("Can't parse WrapQueryType" + name);
    }

}
