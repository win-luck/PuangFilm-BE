package gdsc.cau.puangbe.photo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UploadImageDto {
    Long photoResultId;
    String imageUrl;
}
