package ru.smax.social.network.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.smax.social.network.post.Post;

import java.util.UUID;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, UUID> feedRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, UUID> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // serialization
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(UUID.class));

        return template;
    }

    @Bean
    public RedisTemplate<UUID, Post> postRedisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<UUID, Post> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // serialization
        template.setKeySerializer(new GenericToStringSerializer<>(UUID.class));
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, Post.class));

        return template;
    }
}
