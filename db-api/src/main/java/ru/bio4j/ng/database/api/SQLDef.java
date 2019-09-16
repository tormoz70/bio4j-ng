package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;
import ru.bio4j.ng.model.transport.jstore.Field;

import java.util.List;

public interface SQLDef {
        List<Field> getFields();
        Field findPk();
        String getBioCode();
        void setParamDeclaration(List<Param> paramDeclaration);
        List<Param> getParamDeclaration();
        String getSql();
        String getPreparedSql();
        void setPreparedSql(String preparedSql);
        void setOwner(SQLDefinition bioSQLDefinition);
}
