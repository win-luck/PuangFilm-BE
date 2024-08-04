package gdsc.cau.puangbe.photorequest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateImageDto {
    
    List<String> photoOriginUrls;
    int gender; // 0 남자, 1 여자
}
