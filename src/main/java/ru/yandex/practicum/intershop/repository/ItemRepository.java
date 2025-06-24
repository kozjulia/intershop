package ru.yandex.practicum.intershop.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.intershop.model.Items;
import reactor.core.publisher.Flux;

@Repository
public interface ItemRepository extends ReactiveCrudRepository<Items, Long> {
	Flux<Items> findAllById(Iterable<Long> itemIds);
}
