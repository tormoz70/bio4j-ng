package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.Param;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Вытаскивает OUT параметры из statement и засовывает их в params
 */
public interface SQLParamGetter {
    void getParamsFromStatement(SQLNamedParametersStatement statment, List<Param> params) throws SQLException ;
}
