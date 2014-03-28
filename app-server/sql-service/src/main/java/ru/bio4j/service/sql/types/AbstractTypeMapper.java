package ru.bio4j.service.sql.types;

import ru.bio4j.collections.Sugar;
import ru.bio4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTypeMapper implements TypeMapper {

    /**
     * @title Класс, содержащий обработчики одного конкретного типа
     */
    protected static class ClassHandlers {

        private List<TypeHandler<?>> handlers = null;
        private TypeHandler<?> oneHandler;
        private final Class<?> type;

        public ClassHandlers(Class<?> type) {
            this.type = type;
        }

        /**
         * @title Добавление обработчика типа
         * @param handler
         */
        public void add(TypeHandler<?> handler) {
            if(oneHandler == null) {
                oneHandler = handler;
            } else {
                if(handlers == null) {
                    handlers = new ArrayList<>();
                }
                handlers.add(handler);
            }
        }

        /**
         * @title Получение обработчика типа
         * @param sqlTypeName
         * @param sqlType
         * @param metaType
         * @return Обработчик типа
         */
        public TypeHandler<?> getHandler(String sqlTypeName, int sqlType, String metaType) {
            if(handlers != null) {
                for(int i = handlers.size() - 1; i >= 0; --i) {
                    TypeHandler<?> th = handlers.get(i);
                    if(th != null && checkHandler(th, sqlTypeName, sqlType, metaType)) {
                        return th;
                    }
                }
            }
            if(oneHandler != null && checkHandler(oneHandler, sqlTypeName, sqlType, metaType)) {
                return oneHandler;
            }
            return null;
        }

        /**
         * @title Проверка существования обработчика для типа
         * @param th
         * @param sqlTypeName
         * @param sqlType
         * @param metaType
         * @return true, если обработчик переданного типа существует
         */
        private boolean checkHandler(TypeHandler<?> th, String sqlTypeName, int sqlType, String metaType) {
            HandledTypes ht = th.getClass().getAnnotation(HandledTypes.class);
            String[] sqlNames = ht.sqlNames();
            boolean ok;
            if(!Sugar.empty(sqlNames)) {
                ok = checkString(sqlNames, sqlTypeName);
            } else {
                ok = checkSqlType(ht.sql(), sqlType);
            }
            String handlerMetaType = ht.metaType();
            if(ok && !Strings.empty(handlerMetaType) && !Strings.empty(metaType)) {
                ok = handlerMetaType.equals(metaType.toLowerCase());
            }
            return ok;
        }

        /**
         * @title Проверка существования переданного типа в переданном массиве типов
         * @param types
         * @param type
         * @return
         */
        private boolean checkSqlType(int[] types, int type) {
            if(types.length == 0) {
                //пустой набор типов - означает 'все'
                return true;
            }
            for(int i: types) {
                if(i == type || i == java.sql.Types.OTHER) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @title Проверка существования переданной строки имени типа в переданном массиве имен типов
         * @param names
         * @param name
         * @return
         */
        private boolean checkString(String[] names, String name) {
            if(Strings.empty(name)) {
                return false;
            }
            for(String s : names) {
                if(name.equals(s.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @title Получение типа
         * @return Тип
         */
        private Class<?> getType() {
            return type;
        }
    }

    private final List<ClassHandlers> handlers = new ArrayList<>();

    /**
     * @title Получение типов СУБД
     * @return Типы СУБД
     */
    protected abstract SqlTypes getSqlTypes();

    /**
     * Метод позволяющий вывести соответсвие класса к sql типу
     * @title Получение имени sql-типа соответствующего переданному Java-типу
     * @param clazz
     * @return Имя sql-типа
     */
    @Override
    public String getSqlTypeForClass(final Class<?> clazz) {
        Integer i = 0;
        Class<?> c = clazz;
        while(c != null) {
            i = JDBCTypeMapping.getTypeByClass(c);
            if(i == null) {
                Class<?>[] ifaces = c.getInterfaces();
                for(Class<?> iface: ifaces) {
                    i = JDBCTypeMapping.getTypeByClass(iface);
                    if(i != null) {
                        break;
                    }
                }
                c = c.getSuperclass();
            } else {
                break;
            }
        }
        if(i == null) {
            throw new RuntimeException("Can not found sql type for class: " + clazz);
        }
        return getSqlTypes().toName(i);
    }

    /**
     *  Метод для маппинга sql типов на Java классы
     * @title Получение Java-типа, соответствующего переданному sql-типу
     * @param sqlType
     * @return Java-тип, соответствующий переданному sql-типу
     */
    @Override
    public Class<?> getClassForSqlType(String sqlType) {
        int typeInt = getSqlTypes().toInt(sqlType);
        return JDBCTypeMapping.getClassByType(typeInt);
    }

    /**
     * Регистрирует класс для указанных типов
     * @title Регистрация обработчика типов
     * @param h
     */
    @Override
    public final void register(AbstractTypeHandler<?> h) {
        HandledTypes handle = h.getClass().getAnnotation(HandledTypes.class);

        Class<?>[] types = handle.java();
        final String metaType = handle.metaType();
        h.setMetaType(metaType);
        //регистрируем как хендлер типов
        for(Class<?> type: types) {
            registerFor(h, type, null);
        }
    }

    /**
     * @title Регистрация обработчика типов для указанного Java-типа
     * @param h
     * @param javaType
     * @param sqlType
     */
    private void registerFor(TypeHandler<?> h, Class<?> javaType, String sqlType) {
        ClassHandlers handler = null;
        for(ClassHandlers ch: handlers) {
            if(ch.getType() == javaType) {
                if(handler != null) {
                    throw new RuntimeException("Too many handlers with equivalent types.");
                }
                handler = ch;
            }
        }
        if(handler == null) {
            handler = new ClassHandlers(javaType);
            handlers.add(handler);
        }
        handler.add(h);
    }

    /**
     * @title Получение обработчика типа
     * @param mode
     * @param sqlType
     * @param type
     * @param metaType
     * @return
     */
    private TypeHandler<?> getHandlerClassForType(Mode mode, String sqlType, Class<?> type, String metaType) {
        int sqlTypeInt = getSqlTypes().toInt(sqlType);
        String lcSqlType = sqlType.toLowerCase();
        ClassHandlers finded = null;
        TypeHandler<?> handler = null;
        for(ClassHandlers current: handlers) {
            if(!consume(mode, current.getType(), type)) {
                continue;
            }
            TypeHandler<?> potentialHandler;
            if(finded == null || consume(mode, finded.getType(), current.getType())) {//если найден более подходящий хендлер
                potentialHandler = current.getHandler(lcSqlType, sqlTypeInt, metaType);
            } else {
                continue;
            }
            if(potentialHandler != null) {
                handler = potentialHandler;
                finded = current;
            }
        }
        return handler;
    }

    /**
     * @title Проверка того, что класс типа значения совместим с обработчиком типа
     * @param mode
     * @param handlerClass
     * @param valueClass
     * @return true, если класс типа значения совместим с обработчиком типа
     */
    private boolean consume(Mode mode, Class<?> handlerClass, Class<?> valueClass) {
        if(mode == Mode.WRITE) {
            return handlerClass.isAssignableFrom(valueClass);
        }
        return valueClass.isAssignableFrom(handlerClass);
    }

    /**
     * Ищет обработчик для заданных типов
     * @title Поиск обработчика для заданных типов
     * @param <T>
     * @param mode режим использования хендлера, от него зависит будет ли выполнятся поиск супер-типа или подтипа.
     * @param sqlType
     * @param javaType тип
     * @param metaType
     * @return Экземпляр обработчика типов
     */
    @Override
    public <T> TypeHandler<T> findHandler(Mode mode, String sqlType, Class<T> javaType, String metaType) {
        return (TypeHandler<T>) getHandlerClassForType(mode, sqlType, javaType, metaType);
    }
}
