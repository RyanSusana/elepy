package com.elepy.hibernate;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import com.elepy.configuration.Configuration;
import com.elepy.exceptions.ElepyConfigException;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.elepy.hibernate.HibernateConfiguration.createInMemoryHibernateConfig;

@Testcontainers
public class DatabaseConfigurations {


    public static Configuration H2 = HibernateConfiguration.inMemory();

    public static Configuration HSQL = createInMemoryHibernateConfig(
            "org.hsqldb.jdbc.JDBCDriver",
            "jdbc:hsqldb:mem:myDb",
            "org.hibernate.dialect.HSQLDialect",
            "sa",
            "sa"
    );
    public static Configuration ApacheDerby = createInMemoryHibernateConfig(
            "org.apache.derby.jdbc.EmbeddedDriver",
            "jdbc:derby:memory:myDb;create=true",
            "org.hibernate.dialect.DerbyDialect",
            "sa",
            "sa"
    );

    //    TODO figure out why SQLite is not being found.
    public static Configuration SQLite = createInMemoryHibernateConfig(
            "org.sqlite.JDBC",
            "jdbc:sqlite:memory:myDb",
            " org.hibernate.dialect.SQLiteDialect",
            "sa",
            "sa"
    );


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
            createInMemoryHibernateConfig(
                    "com.mysql.jdbc.Driver",
                    "jdbc:mysql://localhost:3307/test?serverTimezone=UTC",
                    "org.hibernate.dialect.MySQL5Dialect",
                    "root",
                    "")
    );

    private DatabaseConfigurations() {
    }


    public static Configuration createTestContainerConfiguration(JdbcDatabaseContainer container, String dialect) {
        return new WithSetupConfiguration(
                container::start,
                createInMemoryHibernateConfig(
                        container.getDriverClassName(),
                        container.getJdbcUrl(),
                        dialect,
                        container.getUsername(),
                        container.getPassword()
                ),
                container::stop);
    }
} 
