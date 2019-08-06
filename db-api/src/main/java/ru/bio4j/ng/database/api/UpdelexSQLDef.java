package ru.bio4j.ng.database.api;

import ru.bio4j.ng.database.api.SQLDef;

public interface UpdelexSQLDef extends SQLDef {
    String getSignature();
    void setSignature(String signature);
}
