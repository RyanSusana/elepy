package com.elepy.tests.basic.slow;

import com.elepy.tests.basic.BasicEndToEndTest;
import com.elepy.tests.config.DatabaseConfigurations;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MSSQLEndToEndTest extends BasicEndToEndTest {

    @Container
    private static final MSSQLServerContainer MSSQL_SERVER_CONTAINER = new MSSQLServerContainer();

    public MSSQLEndToEndTest() {
        super(DatabaseConfigurations.createTestContainerConfiguration(
                MSSQL_SERVER_CONTAINER,
                "org.hibernate.dialect.SQLServerDialect"
        ));
    }
}
