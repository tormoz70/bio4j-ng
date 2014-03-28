package ru.bio4j.service.sql.db;

import ru.bio4j.func.Function;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Реализация интерфейса схемы субд
 * @title Реализация интерфейса схемы субд
 * @author rad
 */
public class SchemaReal implements Schema {

    private final DB db;
    private final String name;
    private final Function<ResultSet, Procedure> procedureCreator =
            new Function<ResultSet, Procedure>() {

                @Override
                public Procedure apply(ResultSet rs) {
                    try {
                        final ProcedureReal procedureReal = new ProcedureReal(SchemaReal.this);
                        procedureReal.setName(rs.getString("PROCEDURE_NAME"));
                        return procedureReal;
                    } catch (SQLException e) {
                        throw new RuntimeException("Can't get procedure info", e);
                    }
                }
            };

    public SchemaReal(DB db, String name) {
        this.db = db;
        this.name = name;
    }

    /**
     * @title Получение метаданных базы данных
     * @return Метаданные базы данных
     */
    protected DatabaseMetaData dbmd() {
        return db.getDatabaseMetaData();
    }

    /**
     * База данных которой принадлежит схема
     * @title Получение базы данных, которой принадлежит схема
     * @return База данных, которой принадлежит схема
     */
    @Override
    public DB getDB() {
        return db;
    }

    /**
     * Имя объекта
     * @title Получение имени схемы
     * @return Имя схемы
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Возаращает процедуру
     * @title Получение процедуру
     * @param name
     * @return Процедура
     */
    @Override
    public Procedure getProcedure(String name) {
        ResultSet rs = null;
        try {
            rs = dbmd().getProcedures(null, getName(), db.convertIdentifier(name));
            return DBUtils.objectFromResultSet(
                    rs,
                    procedureCreator);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            DBUtils.close(rs);
        }
    }

    /**
     * Список процедур
     * @title Получение списка процедур
     * @return Список процедур
     */
    @Override
    public List<Procedure> getProcedures() {
        ResultSet rs = null;
        try {
            rs = dbmd().getProcedures(null, getName(), "");
            return DBUtils.listFromResultSet(
                    rs,
                    procedureCreator);
        } catch(SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            DBUtils.close(rs);
        }
    }
}