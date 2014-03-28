package ru.bio4j.service.sql.query;

import org.apache.commons.lang3.ClassUtils;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import ru.bio4j.service.sql.QueryContext;
import ru.bio4j.service.sql.types.MetaTypeResolver;
import ru.bio4j.service.sql.types.Type;
import ru.bio4j.service.sql.types.TypesAliases;
import ru.bio4j.service.sql.util.Drivers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @title Фабрика соединений
 */
public class ConnectionFactoryImpl implements ConnectionFactory {

    private final DataSource dataSource;

    public ConnectionFactoryImpl(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Название jndi ресурса вида 'java:comp/env/jdbc/jeraDS'
     * @param dataSourceJndiName
     */
    public ConnectionFactoryImpl(final String dataSourceJndiName) {
        dataSource = Drivers.createDataSource(dataSourceJndiName);
    }

    /**
     * Создание собственного pooling dataSource  https://github.com/brettwooldridge/HikariCP
     * @param connectURI
     * @param user
     * @param pass
     */
    public ConnectionFactoryImpl(final String driverClassName, final String connectURI,
                                 final String user, final String pass, final int maxPoolSize) {

        final PoolProperties properties = new PoolProperties();

        properties.setMaxActive(maxPoolSize);
        properties.setDriverClassName(driverClassName);
        properties.setUrl(connectURI);
        properties.setUsername(user);
        properties.setPassword(pass);
        properties.setCommitOnReturn(false);
        dataSource = new org.apache.tomcat.jdbc.pool.DataSource(properties);
    }

    /**
     * @title Создание нового соединения с базой данных
     * @param qc
     * @return Ссылка на новый экземпляр класса соединения
     * @throws SQLException
     */
    @Override
    public Connection newConnection(final QueryContext qc) throws SQLException {
        Connection connection = dataSource.getConnection();
        qc.set(MetaTypeResolver.class, new MetaTypeResolver() {

            @Override
            public String toSqlType(String string) {
                return string;
            }

            @Override
            public int toSqlTypeSize(String string, int i) {
                return 1;
            }

            @Override
            public Class<?> toJavaType(String string) {
                try {
                    final Type<?> objectType = TypesAliases.getInstance().forName(string);
                    if (objectType == null) {
                        return ClassUtils.getClass(string);
                    } else {
                        return objectType.getJavaType();
                    }
                } catch (ClassNotFoundException e) {
                    return Object.class;
                }
            }
        });
        return connection;
    }

    /**
     * Вызывается перед закрытием содеинения,
     * @title Обработка закрытия в аттрибутах
     * @param qc
     */
    @Override
    public void close(final QueryContext qc) {
    }


    public void closeFactory() {
        if (dataSource != null && dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
            ((org.apache.tomcat.jdbc.pool.DataSource)dataSource).close();
        }
    }


    /**
     * Вызывается при откате транзакции в основном соединени,
     * предназначенн для обработки аттрибутов контекста, не для обработки соединения.
     * @title Откат транзакции в основном соединении
     * @param qc
     */
    @Override
    public void rollback(final QueryContext qc) {
    }

    /**
     * Вызывается при коммите транзакции в основном соединени,
     * предназначенн для обработки аттрибутов контекста, не для обработки соединения.
     * @title Коммит транзакции в основном соединении
     * @param qc
     */
    @Override
    public void commit(final QueryContext qc) {
    }
}
