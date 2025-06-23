package com.boot.ktn.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    /*
    @Bean
    public RedisTemplate<String, LoginEntity> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, LoginEntity> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(LoginEntity.class));
        return template;
    }
    */
}