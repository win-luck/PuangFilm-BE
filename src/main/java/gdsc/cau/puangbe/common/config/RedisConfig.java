package gdsc.cau.puangbe.common.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    String host;
    @Value("${spring.data.redis.port}")
    Integer port;

    // String key, Long value(requestId)를 다루기 위한 RedisTemplate입니다. (현재 유저의 요청의 대기열 역할을 수행하게 됩니다)
    // opsForSet().add()를 통해 requestId를 추가할 수 있으며, 이 때 key는 별도로 만든 키로 설정합니다. (RequestQueue 등)
    // 반드시 opsForSet() 연산을 해야만 Set 형태로 여러 requestId를 저장할 수 있다는 것에 유의해주세요.
    @Bean
    public RedisTemplate<String, Long> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        // GenericJackson2JsonRedisSerializer를 기본 생성자로 초기화
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(serializer);

        return template;
    }

    // String key, String value를 다루기 위한 StringRedisTemplate입니다.
    // opsForValue().set()을 통해 key-value를 저장할 수 있습니다. 필요할 때 사용하시면 됩니다.
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public RedisClient redisClient() {
        return RedisClient.create(RedisURI.builder().withHost(host).withPort(port).build());
    }
}
