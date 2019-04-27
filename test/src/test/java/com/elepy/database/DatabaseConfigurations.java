package com.elepy.database;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import com.elepy.Configuration;
import com.elepy.exceptions.ElepyConfigException;
import com.elepy.hibernate.HibernateConfiguration;
import com.elepy.mongo.MongoConfiguration;
import com.github.fakemongo.Fongo;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;

import java.io.IOException;
import java.util.Properties;

import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.V9_6;

public class DatabaseConfigurations {


    public static Configuration H2 = createHibernateConfig(
            "org.h2.Driver",
            "jdbc:h2:mem:testDB;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
            "org.hibernate.dialect.H2Dialect",
            "SA",
            ""
    );

    public static Configuration HSQL = createHibernateConfig(
            "org.hsqldb.jdbc.JDBCDriver",
            "jdbc:hsqldb:mem:myDb",
            "org.hibernate.dialect.HSQLDialect",
            "sa",
            "sa"
    );
    public static Configuration ApacheDerby = createHibernateConfig(
            "org.apache.derby.jdbc.EmbeddedDriver",
            "jdbc:derby:memory:myDb;create=true",
            "org.hibernate.dialect.DerbyDialect",
            "sa",
            "sa"
    );

    //    TODO figure out why SQLite is not being found.
    public static Configuration SQLite = createHibernateConfig(
            "org.sqlite.JDBC",
            "jdbc:sqlite:memory:myDb",
            " org.hibernate.dialect.SQLiteDialect",
            "sa",
            "sa"
    );

    public static Configuration MongoDB = MongoConfiguration.of(new Fongo("test").getDB("test"));

    public static WithSetupConfiguration MySQL5 = new WithSetupConfiguration(
            () -> {
                try {
                    DB db = DB.newEmbeddedDB(3307);
                    db.start();
                } catch (ManagedProcessException e) {
                    e.printStackTrace();
                    throw new ElepyConfigException("Failed to start MariaDB");
                }
            },
            createHibernateConfig(
                    "com.mysql.jdbc.Driver",
                    "jdbc:mysql://localhost:3307/test?serverTimezone=UTC",
                    "org.hibernate.dialect.MySQL5Dialect",
                    "root",
                    "")
    );

    public static WithSetupConfiguration PostgreSQL = new WithSetupConfiguration(
            () -> {
                final EmbeddedPostgres postgres = new EmbeddedPostgres(V9_6);
                try {
                    postgres.start("localhost", 3308, "test", "root", "root");
                } catch (IOException e) {
                    throw new ElepyConfigException("Failed to initialize PostgreSQL");
                }
            },
            createHibernateConfig(
                    "org.postgresql.Driver",
                    "jdbc:postgresql://localhost:3308/test",
                    "org.hibernate.dialect.PostgreSQLDialect",
                    "root",
                    "root")
    );

    public static Configuration MSSQL = createTestContainerConfiguration(new MSSQLServerContainer(), "org.hibernate.dialect.MSSQLDialect");

    private DatabaseConfigurations() {
    }

    public static Configuration createTestContainerConfiguration(JdbcDatabaseContainer container, String dialect) {
        container.start();

        return new WithSetupConfiguration(
                () -> {
                },
                createHibernateConfig(
                        container.getDriverClassName(),
                        container.getJdbcUrl(),
                        dialect,
                        container.getUsername(),
                        container.getPassword()
                ),
                container::stop);
    }

    public static HibernateConfiguration createHibernateConfig(String driverClassName, String url, String dialect, String username, String password) {
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.driver_class", driverClassName);
        properties.setProperty("hibernate.connection.url", url);
        properties.setProperty("hibernate.connection.username", username);
        properties.setProperty("hibernate.connection.password", password);
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.dialect", dialect);
        properties.setProperty("hibernate.hbm2ddl.auto", "create");


        return HibernateConfiguration.of(new org.hibernate.cfg.Configuration().setProperties(properties));

    }
} 
