package gdsc.cau.puangbe.photo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UploadImageDto {
    Long photoResultId;
    String imageUrl;
}
