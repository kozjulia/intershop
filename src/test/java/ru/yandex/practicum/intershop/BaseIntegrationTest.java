package ru.yandex.practicum.intershop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(
        classes = IntershopApplication.class,
        initializers = PostgresInitializer.class
)
@Sql(scripts = {"/truncate-tables.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;
}
