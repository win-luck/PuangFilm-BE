package gdsc.cau.puangbe.photorequest.dto;

import gdsc.cau.puangbe.common.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 프로필 사진 생성 요청 시 RabbitMQ의 큐에 넣어줄 데이터
 * 이를 통해 다시 파이썬에서 완성 API 호출 시 requestId를 받음으로써 DB에서 상태를 수정하고 Redis에서 삭제 가능
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageInfo {

    List<String> photoOriginUrls;
    Gender gender;
    Long requestId;
}
