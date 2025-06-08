package ru.yandex.practicum.intershop.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.TestcontainersConfiguration;
import ru.yandex.practicum.intershop.model.ItemEntity;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.yandex.practicum.intershop.TestConstants.ITEM_ENTITY;

@DataJpaTest
@Testcontainers
@Import(TestcontainersConfiguration.class)
@Sql(scripts = {"/truncate-tables.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Test
    void searchAllPagingAndSortingSuccessfulTest() {
        ItemEntity itemEntity = ITEM_ENTITY;
        itemEntity.setId(null);
        itemRepository.save(itemEntity);
        int pageNumber = 1;
        int pageSize = 10;
        Pageable page = PageRequest.of(pageNumber - 1, pageSize);

        Page<ItemEntity> items = itemRepository.searchAllPagingAndSorting(EMPTY, page);

        assertThat(items.getContent().size(), equalTo(1));
        assertThat(items.getContent().getFirst(), equalTo(ITEM_ENTITY));
    }

    @Test
    void findAllByIdInSuccessfulTest() {
    }

    @Test
    void updateImagePathSuccessfulTest() {
    }

    @Test
    void updateCountItemSuccessfulTest() {
    }
}