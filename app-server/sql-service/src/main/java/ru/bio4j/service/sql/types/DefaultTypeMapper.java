package ru.bio4j.service.sql.types;

import ru.bio4j.service.sql.db.DB;

/**
 * Дефолтная реализация маппера типов
 * @title Реализация маппера типов по умолчанию
 * @author rad
 */
public final class DefaultTypeMapper extends AbstractTypeMapper {

    private final DB db;

    public DefaultTypeMapper(DB db) {
        this.db = db;
        register(new BooleanHandler());
        register(new BytesHandler());
        register(new DateHandler(db));
        register(new FloatHandler());
        register(new IntegerHandler());
        register(new StringHandler());
        register(new ObjectHandler());
    }

    /**
     * @title Получение типов СУБД
     * @return Типы СУБД
     */
    @Override
    protected SqlTypes getSqlTypes() {
        return db.getTypes();
    }
}