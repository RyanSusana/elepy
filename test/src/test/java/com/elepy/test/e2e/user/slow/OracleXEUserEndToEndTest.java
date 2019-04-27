package com.elepy.test.e2e.user.slow;

import com.elepy.Configuration;
import com.elepy.database.DatabaseConfigurations;
import com.elepy.test.e2e.user.ElepyUserEndToEndTest;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class OracleXEUserEndToEndTest extends ElepyUserEndToEndTest {

    @Container
    private static final OracleContainer ORACLE_CONTAINER = new OracleContainer();

    @Override
    public Configuration configuration() {
        return DatabaseConfigurations.createTestContainerConfiguration(
                ORACLE_CONTAINER,
                "org.hibernate.dialect.Oracle10gDialect"
        );
    }
}
