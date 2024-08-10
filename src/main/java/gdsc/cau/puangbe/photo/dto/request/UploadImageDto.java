package gdsc.cau.puangbe.photo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UploadImageDto {

    @NotNull(message = "photoRequestId 필수입니다.")
    Long photoRequestId;

    @NotNull(message = "imageUrl는 필수입니다.")
    String imageUrl;
}
