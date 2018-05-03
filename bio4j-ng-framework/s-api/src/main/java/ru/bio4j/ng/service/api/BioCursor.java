package ru.bio4j.ng.service.api;

import ru.bio4j.ng.model.transport.jstore.Field;

import java.util.Collection;
import java.util.List;


public interface BioCursor {

    String getExportTitle();
    void setExportTitle(String exportTitle);

    Boolean getMultiSelection();
    void setMultiSelection(Boolean multiSelection);

    Boolean getReadOnly();
    void setReadOnly(Boolean readOnly);

    Field findField(final String name) throws Exception;

    Field findPk();

    String getBioCode();

    List<Field> getFields();

    void setSqlDef(SQLType sqlType, SQLDef sqlDef);

    UpdelexSQLDef getUpdateSqlDef();

    UpdelexSQLDef getDeleteSqlDef();

    UpdelexSQLDef getExecSqlDef();

    SelectSQLDef getSelectSqlDef();

    SQLDef getAfterselectSqlDef();

    Collection<SQLDef> sqlDefs();


}
