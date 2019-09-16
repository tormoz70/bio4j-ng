package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.jstore.Field;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


public interface SQLDefinition extends Serializable {

    String getExportTitle();
    void setExportTitle(String exportTitle);

    Boolean getMultiSelection();
    void setMultiSelection(Boolean multiSelection);

    Boolean getReadOnly();
    void setReadOnly(Boolean readOnly);

    Field findField(final String name);

    Field findPk();

    String getBioCode();

    List<Field> getFields();

    void setSqlDef(SQLType sqlType, SQLDef sqlDef);

    UpdelexSQLDef getUpdateSqlDef();

    UpdelexSQLDef getDeleteSqlDef();

    UpdelexSQLDef getExecSqlDef();

    SelectSQLDef getSelectSqlDef();

    SQLDef getAfterSelectSqlDef();

    Collection<SQLDef> sqlDefs();


}
