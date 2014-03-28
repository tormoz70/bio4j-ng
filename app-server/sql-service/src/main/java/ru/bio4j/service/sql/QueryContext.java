package ru.bio4j.service.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.func.UnsafeFunction;
import ru.bio4j.service.sql.db.DB;
import ru.bio4j.service.sql.db.DBRegistry;
import ru.bio4j.service.sql.db.DBUtils;
import ru.bio4j.service.sql.query.ConnectionFactory;
import ru.bio4j.service.sql.query.QueryListener;
import ru.bio4j.service.sql.util.Closeable;

import java.lang.reflect.UndeclaredThrowableException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Контекст запроса, содержит фабрику соединений. В методе {@link #call(UnsafeFunction)  }
 * или UnsafeFunction происходит отрытие соединения и помещение его в контекст,
 * соответсвенно если далее по стеку вызово опять встретится указанный метод то
 * будет использовано соединение из контекста.
 * @title Контекст запроса
 * @author rad
 */
public class QueryContext implements Closeable {
    private static final ThreadLocal<QueryContext> tlc = new ThreadLocal<>();

    private final static Logger LOG = LoggerFactory.getLogger(QueryContext.class);
    private final ConnectionFactory cf;
    private Connection conn;
    private Map<Class<?>, Object> attrs;
    private DB db;
    private final Collection<QueryListener> listeners = new ArrayList<>();

    public QueryContext(ConnectionFactory qcf) {
        this.cf = qcf;
    }

    /**
     * Создает контекст текущего потока. В силу того что реализация MDB полагает
     * повтороное использование потоков, то при выходе из метода создавшего контекст
     * стоит его удалять.
     * @title Создание контекста текущего потока
     * @param cf
     * @return Новый контекст текущего потока
     * @see #remove()
     * @see #get()
     */
    public static QueryContext create(ConnectionFactory cf) {
        QueryContext ctx = tlc.get();
        if(ctx != null){
            LOG.warn("Context already created.");
            return tlc.get();
        }
        ctx = new QueryContext(cf);
        tlc.set(ctx);
        return ctx;
    }

    /**
     * Удалаяет контекст текущего потока
     * @title Удаление контекста текущего потока
     * @see #get()
     */
    public static void remove(){
        try{
            QueryContext sc = tlc.get();
            if(sc != null){
                boolean isOpened = false;
                try{
                    isOpened = sc.isOpened();
                } catch(Throwable th) {
                    LOG.error("Oooopss... can't remove connection", th);
                }
                if(isOpened){
                    LOG.warn("In " + sc + " isOpened() has returned 'true'. Closing...");
                    sc.close();
                }
            }
        } finally {
            tlc.remove();
        }
    }

    /**
     * В отличии от {@link #get() } не выбрасывает исключение если контекст не
     * создан, а возвращает <code>null</code>
     * @title Получение контекста текущего потока
     * @return Контекст текущего потока
     */
    public static QueryContext quietGet(){
        return tlc.get();
    }

    /**
     * Возвращает контекст текущего потока
     * @title Получение контекста текущего потока
     * @return Контекст текущего потока
     * @see #remove()
     */
    public static QueryContext get() {
        QueryContext context = tlc.get();
        if(context == null){
            throw  new IllegalStateException("QueryContext is not created." +
                    " You can create it by calling 'QueryContext.create()' before call this.");
        }
        return context;
    }

    /**
     * Открывает соединение
     * @title Открытие соединения
     * @throws SQLException
     */
    private void open() {
        try {
            conn = cf.newConnection(this);
            if(conn == null) {
                throw new NullPointerException("Connection is null");
            }
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @title Проверка того, что соединение открыто
     * @return true, если соединение открыто
     */
    private boolean isOpened() {
        if(conn != null) {
            try {
                return !conn.isClosed();
            } catch(SQLException ex) {
                LOG.error("isOpened", ex);
            }
        }
        return false;
    }

    /**
     * @title Коммит текущей транзакции
     * @throws SQLException
     */
    public void commit() throws SQLException {
        cf.commit(this);
        if(conn != null && !conn.getAutoCommit()){
            conn.commit();
        }
    }

    /**
     * @title Откат текущей транзакции
     * @throws SQLException
     */
    public void rollback() throws SQLException {
        cf.rollback(this);
        if(conn != null){
            conn.rollback();
        }
    }

    /**
     * Закрывает соединение и сессию
     * не выбрасывая исключения
     * @title Закрытие соединения и сессии
     */
    @Override
    public void close() {
        try {
            cf.close(this);
        } catch(Throwable t) {
            LOG.error("", t);
        }
        DBUtils.close(conn);
        conn = null;
    }

    /**
     * @title Получение соединения
     * @return Соединение
     */
    public Connection getConnection() {
        return conn;
    }

    /**
     * Открывает соединение к базе, если оно еще не было открыто (открыто оно
     * будет если это вложенный вызов) выполняет в ней переданный
     * Function, делает коммит если все прошло удачно, иначе rollback и
     * закрывает соединение если самже его и окрыл (в случае ошибки закрывает всегда)
     * /**
     * @title Открытие соединения, выполнение переданной функции, коммит или откат транзакции и закрытие соединения
     * @param <T>
     * @param callable
     * @return Результат выполнения переданной функции
     */
    public<T> T run(UnsafeFunction<QueryContext, T, ? extends Throwable> callable) {
        T result = null;
        boolean weOpen = !isOpened();
        if(weOpen) {
            open();
        }
        try {
            result = callable.apply(this);
            if(weOpen) {
                commit();
            }
        } catch(Throwable th) {
            if(weOpen) {
                try{
                    rollback();
                } catch(Throwable _th) {
                    LOG.error("can not run callable", _th);
                }
            }
            throw new UndeclaredThrowableException(th);
        } finally {
            if(weOpen) {
                close();
            }
        }
        return result;
    }

    /**
     * @title Открытие соединения, выполнение переданной функции, коммит или откат транцакии и закрытие
     * @param callable
     * @return Результат выполнения переданной функции
     */
    public static<T> T call(UnsafeFunction<QueryContext, T, ? extends Throwable> callable) {
        QueryContext context = get();
        return context.run(callable);
    }

    /**
     * @title Получение базы данных
     * @return База данных
     * @throws SQLException
     */
    public DB getDB() throws SQLException {
        checkIsOpened();
        if(db == null) {
            db = DBRegistry.getDB(getConnection().getMetaData());
        }
        return db;
    }

    /**
     * @title Получение атрибута
     * @param <T>
     * @param clazz
     * @return Атрибут контекста
     */
    public<T> T get(Class<? extends T> clazz) {
        if(clazz == null) {
            throw new NullPointerException("Class is null.");
        }
        if(attrs == null) {
            return null;
        }
        return (T)attrs.get(clazz);
    }

    /**
     * @title Установка атрибута
     * @param <T>
     * @param clazz
     * @param value
     */
    public<T> void set(Class<? super T> clazz, T value) {
        if(clazz == null) {
            throw new NullPointerException("Class is null.");
        }
        if(attrs == null) {
            attrs = new HashMap<>();
        }
        attrs.put(clazz, value);
    }

    /**
     * @title Выброс исключения в случае закрытого соединения
     */
    private void checkIsOpened() {
        if(!isOpened()) {
            throw new IllegalStateException("Connection is not opened!");
        }
    }

    /**
     * @title Регистрация слушателя SQL-запросов, выполняемых через QueryHelper
     * @param l
     */
    public void addListener(QueryListener l){
        listeners.add(l);
    }

    /**
     * @title Удаление слушателя SQL-запросов, выполняемых через QueryHelper
     * @param l
     */
    public void removeListener(QueryListener l){
        listeners.remove(l);
    }

    /**
     * @title Получение списка слушателей SQL-запросов, выполняемых через QueryHelper
     * @return Коллекция слушателей SQL-запросов
     */
    public Collection<QueryListener> getListeners(){
        return listeners;
    }
}
