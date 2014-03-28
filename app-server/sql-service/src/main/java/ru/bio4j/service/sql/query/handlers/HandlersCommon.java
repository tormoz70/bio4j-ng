package ru.bio4j.service.sql.query.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.collections.Parameter;
import ru.bio4j.service.sql.QueryContext;
import ru.bio4j.service.sql.query.parser.ParsedParameter;
import ru.bio4j.service.sql.types.MetaTypeResolver;
import ru.bio4j.service.sql.types.TypeHandler;
import ru.bio4j.service.sql.types.TypeMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Общий код для хендлеров
 * @title Общий код для обработчиков
 * @author rad
 */
public class HandlersCommon {

    private static final Logger LOG = LoggerFactory.getLogger(HandlersCommon.class);

    /**
     * @title
     * @param ps
     * @param params
     * @param context
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public static void subtituteParams(PreparedStatement ps, List<ParsedParameter> params, QueryContext context) throws SQLException {
        final boolean infoEnabled = LOG.isDebugEnabled();
        final TypeMapper typeMapper = context.getDB().getTypeMapper();
        final MetaTypeResolver mtr = context.get(MetaTypeResolver.class);
        if(mtr == null) {
            throw new NullPointerException("'MetaTypeResolver' in context not found.");
        }
        //устанавливаем параметры
        for(ParsedParameter pp : params){
            if(pp == null) {
                continue;
            }
            Parameter param = pp.getParameter();
            if (param == null) {
                throw new IllegalArgumentException("Can't find parameter with specification: " + pp.toString());
            }
            String metaType = param.getType();
            if(metaType == null) {
                metaType = pp.getMetaType();
            }
            Object o = param.getValue();
            if(infoEnabled){
                LOG.debug("\tPARAM(" + pp.getName() + " " + pp.getPosition() + "):" + o + " of type " + metaType);
            }
            Class<?> javaType = Object.class;
            String sqlType = pp.getSqlTypeName();
            if(metaType != null) {
                javaType = mtr.toJavaType(metaType);
                sqlType = mtr.toSqlType(metaType);
            } else if(o != null) {
                javaType = o.getClass();
                sqlType = typeMapper.getSqlTypeForClass(javaType);
            }
            TypeHandler<?> th = typeMapper.findHandler(TypeMapper.Mode.WRITE, sqlType, javaType, metaType);
            th.write(ps, o, pp.getPosition(), (Class)javaType, sqlType);
        }
    }
}