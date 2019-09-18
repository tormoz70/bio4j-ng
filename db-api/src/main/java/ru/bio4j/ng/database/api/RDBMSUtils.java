package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface RDBMSUtils {
    StoredProgMetadata detectStoredProcParamsAuto(String storedProcName, Connection conn, List<Param> fixedParamsOverride);
    BioSQLApplicationError extractStoredProcAppError(Exception c);
}
