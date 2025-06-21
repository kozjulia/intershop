package ru.yandex.practicum.intershop.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.intershop.model.ItemEntity;

import java.util.List;

@Repository
public interface ItemRepository extends R2dbcRepository<ItemEntity, Long> {

    @Query("""
            SELECT item
            FROM ItemEntity item
            WHERE :search IS NULL
            OR item.title ILIKE %:search%
            OR item.description ILIKE %:search%
            """)
    Flux<ItemEntity> searchAllPagingAndSorting(String search, Pageable pageable);

    Flux<ItemEntity> findAllByIdIn(List<Long> itemIds);

    @Modifying //(clearAutomatically = true)
    @Query("""
            UPDATE ItemEntity item
            SET item.imgPath = :imgPath
            WHERE item.id = :itemId
            """)
    void updateImagePath(Long itemId, String imgPath);

    @Modifying //(clearAutomatically = true)
    @Query("""
            UPDATE ItemEntity item
            SET item.count = item.count - :count
            WHERE item.id = :itemId
            """)
    void updateCountItem(Long itemId, Integer count);
}
