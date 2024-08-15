package gdsc.cau.puangbe.photorequest.service;

import gdsc.cau.puangbe.common.config.RabbitMq.RabbitMqInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqServiceImpl implements RabbitMqService{

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqInfo rabbitMqInfo;

    /**
     * 1. Queue 로 메세지를 발행
     * 2. Producer 역할 -> Direct Exchange (메시지의 routing key와 정확히 일치하는 binding된 Queue로 routing)
     **/
    public void sendMessage(String message) {
        this.rabbitTemplate.convertAndSend(rabbitMqInfo.getExchangeName(), rabbitMqInfo.getRoutingKey(), message);
        log.info("**Message Send**: {}", message);
        log.info("messagge queue: {}", rabbitMqInfo.getQueueName());
        log.info("messagge exchange: {}", rabbitMqInfo.getExchangeName());
        log.info("messagge routingKey: {}", rabbitMqInfo.getRoutingKey());
    }
}
