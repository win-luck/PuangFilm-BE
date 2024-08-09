package gdsc.cau.puangbe.photorequest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateImageDto {

    @Size(min = 8, max = 16, message = "사진은 8장부터 16장까지 가능합니다.")
    List<String> photoOriginUrls;

    // 0 남자, 1 여자
    @Min(value = 0, message = "성별은 0 또는 1이어야 합니다.")
    @Max(value = 1, message = "성별은 0 또는 1이어야 합니다.")
    int gender; // 0 남자, 1 여자

    @Email(message = "이메일 형식이 아닙니다.")
    String email;
}
