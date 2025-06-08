package ru.yandex.practicum.intershop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.intershop.model.ItemEntity;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

    @Query("""
            SELECT item
            FROM ItemEntity item
            WHERE :search IS NULL
            OR item.title ILIKE %:search%
            OR item.description ILIKE %:search%
            """)
    Page<ItemEntity> searchAllPagingAndSorting(@Param("search") String search, Pageable pageable);

    List<ItemEntity> findAllByIdIn(List<Long> itemIds);

    @Modifying
    @Query("""
            UPDATE ItemEntity item
            SET item.imgPath = :imgPath
            WHERE item.id = :itemId
            """)
    void updateImagePath(@Param("itemId") Long itemId, @Param("imgPath") String imgPath);

    @Modifying
    @Query("""
            UPDATE ItemEntity item
            SET item.count = item.count - :count
            WHERE item.id = :itemId
            """)
    void updateCountItem(@Param("itemId") Long itemId, @Param("count") Integer count);
}
