package gdsc.cau.puangbe.photo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailInfoDto {
    String email;
    String name;
    String photoUrl;
    String framePageUrl;
}
