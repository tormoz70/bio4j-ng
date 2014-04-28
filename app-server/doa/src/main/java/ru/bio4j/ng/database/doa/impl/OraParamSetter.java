package ru.bio4j.ng.database.doa.impl;

import ru.bio4j.ng.model.transport.Param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ayrat
 * Date: 17.12.13
 * Time: 20:19
 * To change this template use File | Settings | File Templates.
 */
public interface OraParamSetter {
    void setParamsToStatement(PreparedStatement statement, List<Param> params) throws SQLException;
}
