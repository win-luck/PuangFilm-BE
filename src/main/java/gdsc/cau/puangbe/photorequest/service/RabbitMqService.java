package gdsc.cau.puangbe.photorequest.service;

import gdsc.cau.puangbe.photorequest.dto.ImageInfo;

public interface RabbitMqService {
    public void sendMessage(String message);
}
