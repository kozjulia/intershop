package ru.yandex.practicum.intershop;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String IMAGE = "postgres:15";
    private static final String DATABASE_NAME = "testdb";
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "testpass";
    private static final String ISOLATION_LEVEL = "default_transaction_isolation=repeatable read";

    private static final PostgreSQLContainer<?> POSTGRES_CONTEINER =
            new PostgreSQLContainer<>(DockerImageName.parse(IMAGE).asCompatibleSubstituteFor("postgres"))
                    .withReuse(true)
                    .withDatabaseName(DATABASE_NAME)
                    .withUsername(USERNAME)
                    .withPassword(PASSWORD)
                    .withCommand("-c", ISOLATION_LEVEL);

    static {
        POSTGRES_CONTEINER.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of("spring.datasource.url=" + POSTGRES_CONTEINER.getJdbcUrl())
                .applyTo(applicationContext.getEnvironment());
    }
}
