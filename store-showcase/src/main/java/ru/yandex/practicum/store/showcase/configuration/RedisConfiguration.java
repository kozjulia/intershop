package ru.yandex.practicum.store.showcase.configuration;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import ru.yandex.practicum.store.showcase.dto.ItemDto;
import ru.yandex.practicum.store.showcase.dto.OrderDto;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@EnableCaching
public class RedisConfiguration {

    @Bean
    public RedisCacheManagerBuilderCustomizer itemCacheCustomizer() {
        return builder -> builder
                .withCacheConfiguration(
                        "items",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(
                                        RedisSerializationContext.SerializationPair.fromSerializer(
                                                new Jackson2JsonRedisSerializer<>(ItemDto.class)
                                        )
                                )

                )
                .withCacheConfiguration(
                        "allItems",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.of(1, ChronoUnit.MINUTES))
                                .serializeValuesWith(
                                        RedisSerializationContext.SerializationPair.fromSerializer(
                                                new GenericJackson2JsonRedisSerializer()
                                        )
                                )
                )
                .withCacheConfiguration(
                        "orders",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(
                                        RedisSerializationContext.SerializationPair.fromSerializer(
                                                new Jackson2JsonRedisSerializer<>(OrderDto.class)
                                        )
                                )

                )
                .withCacheConfiguration(
                        "allOrders",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.of(1, ChronoUnit.MINUTES))
                                .serializeValuesWith(
                                        RedisSerializationContext.SerializationPair.fromSerializer(
                                                new GenericJackson2JsonRedisSerializer()
                                        )
                                )
                );
    }
}
