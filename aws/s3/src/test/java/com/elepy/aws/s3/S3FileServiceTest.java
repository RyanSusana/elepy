package com.elepy.aws.s3;

import com.adobe.testing.s3mock.junit5.S3MockExtension;
import com.elepy.Configuration;
import com.elepy.Elepy;
import com.elepy.auth.permissions.Permissions;
import com.elepy.hibernate.HibernateConfiguration;
import com.elepy.tests.basic.Resource;
import com.elepy.tests.upload.FileServiceTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Properties;


@ExtendWith(S3MockExtension.class)
public class S3FileServiceTest extends FileServiceTest {

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
    public void setUp() {
    }

    @BeforeAll
    public void setUp(S3Client s3Mock) {
        s3Mock.createBucket(builder -> builder.bucket("test-bucket"));
        this.fileService = new S3FileService("test-bucket", "prefix", s3Mock);
        this.url = String.format("http://localhost:%d", port);
        this.elepy = new Elepy()
                .addModel(Resource.class)
                .addConfiguration(H2)
                .withFileService(this.fileService)
                .withPort(port);

        this.elepy.http().before(ctx -> ctx.request().addPermissions(Permissions.AUTHENTICATED, "files.*"));
        this.elepy.start();
    }

    @Override
    @AfterAll
    public void tearDown() {
        super.tearDown();
    }
}
