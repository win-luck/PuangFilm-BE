package gdsc.cau.puangbe.photorequest.service;

import gdsc.cau.puangbe.photorequest.dto.CreateImageDto;
import gdsc.cau.puangbe.photorequest.dto.ResponseResultDto;

public interface PhotoRequestService {
    //이미지 처리 요청 생성 (RabbitMQ호출)
    void createImage(CreateImageDto dto, Long userId);

    //유저의 전체 사진 리스트 조회
    ResponseResultDto getRequestImages(Long userId);

    //최근 생성 요청한 이미지의 상태 조회
    String getRequestStatus(Long userId);


}
