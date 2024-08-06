package gdsc.cau.puangbe.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class KakaoIdTokenPublicKey {
    private List<KakaoJWK> keys;
}
