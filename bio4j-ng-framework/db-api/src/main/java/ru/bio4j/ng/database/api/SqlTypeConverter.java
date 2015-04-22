package ru.bio4j.ng.database.api;

import ru.bio4j.ng.model.transport.MetaType;

/**
 * Created by ayrat on 28.08.2014.
 */
public interface SqlTypeConverter {
    Class<?> write (int sqlType, int charSize);
    int read (Class<?> type, int stringSize, boolean isCallableStatment);
    int read (MetaType type, int stringSize, boolean isCallableStatment);
}
