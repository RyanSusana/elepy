package com.elepy.hibernate.slow;

import com.elepy.Elepy;
import com.elepy.hibernate.DatabaseConfigurations;
import com.elepy.tests.basic.BasicFunctionalityTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@Tag("slow")
@Disabled("Docker pull problems")
public class MSSQLBasicFunctionalityTest extends BasicFunctionalityTest {

    @Container
    private static final MSSQLServerContainer MSSQL_SERVER_CONTAINER = new MSSQLServerContainer();

    @Override
    public void configureElepy(Elepy elepy) {
        elepy.addConfiguration(DatabaseConfigurations.createTestContainerConfiguration(
                MSSQL_SERVER_CONTAINER,
                "org.hibernate.dialect.SQLServerDialect"
        ));
    }
}
