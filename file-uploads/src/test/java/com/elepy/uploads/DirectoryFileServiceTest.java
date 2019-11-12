package com.elepy.uploads;

import com.elepy.Configuration;
import com.elepy.hibernate.HibernateConfiguration;
import com.elepy.tests.upload.FileServiceTest;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class DirectoryFileServiceTest extends FileServiceTest {

    private static final String UPLOAD_DIR = "src/test/resources/uploads";

    public static Configuration H2 = createHibernateConfig(
            "org.h2.Driver",
            "jdbc:h2:mem:testDB;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
            "org.hibernate.dialect.H2Dialect",
            "SA",
            ""
    );

    public static HibernateConfiguration createHibernateConfig(String driverClassName, String url, String dialect, String username, String password) {
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.driver_class", driverClassName);
        properties.setProperty("hibernate.connection.url", url);
        properties.setProperty("hibernate.connection.username", username);
        properties.setProperty("hibernate.connection.password", password);
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("hibernate.dialect", dialect);
        properties.setProperty("hibernate.hbm2ddl.auto", "create");


        return HibernateConfiguration.of(new org.hibernate.cfg.Configuration().setProperties(properties));

    }

    @Override
    public FileService fileService() {
        return new DirectoryFileService(UPLOAD_DIR);
    }

    @Override
    public Configuration databaseConfiguration() {
        return H2;
    }

}
