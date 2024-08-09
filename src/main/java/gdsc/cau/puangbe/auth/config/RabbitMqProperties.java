package gdsc.cau.puangbe.auth.config;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring.rabbitmq 의 prefix 을 가지는 값들을
 * RabbitMqProperties 클래스 필드로 바인딩 한 후 사용
 */
@ConfigurationProperties(prefix = "spring.rabbitmq")
@AllArgsConstructor
@Getter
public class RabbitMqProperties {
    private String host;
    private int port;
    private String username;
    private String password;
}
