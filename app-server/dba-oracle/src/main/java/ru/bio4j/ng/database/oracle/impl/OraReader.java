package ru.bio4j.ng.database.oracle.impl;

import ru.bio4j.ng.commons.converter.ConvertValueException;
import ru.bio4j.ng.commons.converter.Converter;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.Field;
import ru.bio4j.ng.database.api.SQLReader;
import ru.bio4j.ng.database.commons.DbReader;
import ru.bio4j.ng.database.commons.FieldImpl;

import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayrat on 24.04.14.
 */
public class OraReader extends DbReader {
    public OraReader(ResultSet resultSet) {
        super(resultSet);
    }

}
