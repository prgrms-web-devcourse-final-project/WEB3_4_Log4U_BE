package com.example.log4u.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.thirdparty.jackson.core.type.TypeReference;

@Configuration
public class RedisConfig {

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();

		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

		return template;
	}

	@Bean
	public RedisTemplate<String, List<DiaryClusterResponseDto>> diaryClusterRedisTemplate(
		RedisConnectionFactory connectionFactory,
		ObjectMapper objectMapper
	) {
		RedisTemplate<String, List<DiaryClusterResponseDto>> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());

		JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, DiaryClusterResponseDto.class);
		Jackson2JsonRedisSerializer<List<DiaryClusterResponseDto>> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, javaType);
		template.setValueSerializer(serializer);
		return template;
	}
}
