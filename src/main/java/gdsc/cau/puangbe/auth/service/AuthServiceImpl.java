package gdsc.cau.puangbe.auth.service;

import gdsc.cau.puangbe.auth.dto.KakaoIDTokenPayload;
import gdsc.cau.puangbe.auth.dto.KakaoToken;
import gdsc.cau.puangbe.auth.dto.LoginResponse;
import gdsc.cau.puangbe.auth.dto.ReissueResponse;
import gdsc.cau.puangbe.auth.entity.Token;
import gdsc.cau.puangbe.common.exception.AuthException;
import gdsc.cau.puangbe.auth.external.JwtProvider;
import gdsc.cau.puangbe.auth.external.KakaoProvider;
import gdsc.cau.puangbe.auth.external.OIDCProvider;
import gdsc.cau.puangbe.auth.repository.TokenRepository;
import gdsc.cau.puangbe.common.util.ResponseCode;
import gdsc.cau.puangbe.user.repository.UserRepository;
import gdsc.cau.puangbe.user.entity.User;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private final KakaoProvider kakaoProvider;
    private final OIDCProvider OIDCProvider;
    private final JwtProvider jwtProvider;

    private final String ISS = "https://kauth.kakao.com";

    @Override
    public LoginResponse loginWithKakao(String code) {
        // 카카오 토큰 발급
        KakaoToken kakaoToken = kakaoProvider.getTokenByCode(code);

        // 카카오 ID 토큰 유효성 검증
        KakaoIDTokenPayload kakaoIDTokenPayload;
        try {
            kakaoIDTokenPayload = OIDCProvider.verify(kakaoToken.getIdToken(), ISS, kakaoProvider.getOIDCPublicKeyList());
        } catch (SignatureException e) {
            kakaoIDTokenPayload = OIDCProvider.verify(kakaoToken.getIdToken(), ISS, kakaoProvider.getUpdatedOIDCPublicKeyList());
        }

        final String kakaoId = kakaoIDTokenPayload.getSub();
        final String userName = kakaoIDTokenPayload.getNickname();

        // DB User 테이블 갱신
        // 기존 회원: UPDATE request_date
        // 신규 회원: INSERT
        userRepository.findByKakaoId(kakaoId)
                .map(u -> {
                    u.updateRequestDate(LocalDateTime.now());
                    return userRepository.save(u);
                })
                .orElseGet(() -> userRepository.save(User.builder()
                        .userName(userName)
                        .createDate(LocalDateTime.now())
                        .requestDate(LocalDateTime.now())
                        .kakaoId(kakaoId)
                        .build()
                ));

        // refresh_token 발행
        String refreshToken = jwtProvider.createRefreshToken(kakaoId, userName);

        // DB Token 테이블 갱신
        Token refreshTokenRecord = tokenRepository.save(Token.builder()
                .refreshToken(refreshToken)
                .kakaoId(kakaoId)
                .expiresAt(jwtProvider.getExpirationFromToken(refreshToken).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build()
        );

        // access_token 발행
        String accessToken = jwtProvider.createAccessToken(kakaoId, refreshTokenRecord.getId());

        return new LoginResponse(accessToken);
    }

    @Override
    public ReissueResponse reissue(String authorizationHeader) {
        // Header로부터 access_token 추출
        String accessToken = jwtProvider.getTokenFromAuthorizationHeader(authorizationHeader);

        // access_token으로부터 refreshId 추출 (DB Token 테이블의 ID)
        Long refreshTokenId = jwtProvider.getRefreshIdFromExpiredToken(accessToken);

        // refresh_token 조회
        Token refreshTokenRecord = tokenRepository.findById(refreshTokenId)
                .orElseThrow(() -> new AuthException(ResponseCode.UNAUTHORIZED));

        // refresh_token 유효 기간 검증
        // 만료 시 DB Token 테이블에서 삭제
        if (!refreshTokenRecord.getExpiresAt().isAfter(LocalDateTime.now())) {
            tokenRepository.delete(refreshTokenRecord);
            throw new AuthException(ResponseCode.UNAUTHORIZED);
        }

        // refresh_token 재발급
        String refreshToken = refreshTokenRecord.getRefreshToken();
        String newRefreshToken = jwtProvider.createRefreshToken(refreshTokenRecord.getKakaoId(), jwtProvider.getUserNameFromRefreshToken(refreshToken));

        // DB Token 테이블 갱신
        refreshTokenRecord.update(newRefreshToken, jwtProvider.getExpirationFromToken(newRefreshToken).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        tokenRepository.save(refreshTokenRecord);

        // access_token 재발급
        String newAccessToken = jwtProvider.createAccessToken(refreshTokenRecord.getKakaoId(), refreshTokenId);

        return new ReissueResponse(newAccessToken);
    }

    @Override
    public Boolean validateToken(String authorizationHeader) {
        // Header로부터 access_token 추출
        String accessToken = jwtProvider.getTokenFromAuthorizationHeader(authorizationHeader);

        // access_token 검증
        try {
            jwtProvider.validateToken(accessToken);
        } catch (AuthException e) {
            return false;
        }

        return true;
    }

    @Override
    @Scheduled(cron = "0 5 5 * * ?") // 매일 새벽 5시 5분
    public void deleteExpiredRefreshTokens() {
        tokenRepository.deleteAllByExpiresAtBefore(LocalDateTime.now());
    }
}
