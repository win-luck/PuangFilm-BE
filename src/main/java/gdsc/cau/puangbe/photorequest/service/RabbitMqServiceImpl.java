package gdsc.cau.puangbe.photorequest.service;

import gdsc.cau.puangbe.photorequest.dto.ImageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqServiceImpl implements RabbitMqService{

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    /**
     * 1. Queue 로 메세지를 발행
     * 2. Producer 역할 -> Direct Exchange (메시지의 routing key와 정확히 일치하는 binding된 Queue로 routing)
     **/
    public void sendMessage(String message) {
        log.info("**Message Send**: {}",message);
        log.info("messagge queue: {}", queueName);
        log.info("messagge exchange: {}", exchangeName);
        log.info("messagge routingKey: {}", routingKey);
        this.rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
    }

}
