package gdsc.cau.puangbe.auth.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import gdsc.cau.puangbe.auth.config.KakaoLoginProperties;
import gdsc.cau.puangbe.auth.dto.KakaoIDTokenPayload;
import gdsc.cau.puangbe.auth.dto.KakaoIDTokenPublicKeyList;
import gdsc.cau.puangbe.auth.dto.KakaoIDTokenJWK;
import gdsc.cau.puangbe.common.exception.AuthException;
import gdsc.cau.puangbe.common.util.ResponseCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class OIDCProvider {
    private final KakaoLoginProperties kakaoLoginProperties;

    // 카카오 ID 토큰 유효성 검증
    public KakaoIDTokenPayload verify(String idToken, String iss, KakaoIDTokenPublicKeyList kakaoIdTokenPublicKeyList) {
        verifyPayload(idToken, iss);
        return verifySignature(idToken, kakaoIdTokenPublicKeyList);
    }

    // 카카오 ID 토큰 페이로드 검증
    private void verifyPayload(String token, String iss) {
        // 카카오 ID 토큰으로부터 페이로드 추출
        KakaoIDTokenPayload kakaoIDTokenPayload = extractPayloadFromTokenString(token);

        // iss: https://kauth.kakao.com와 일치해야 함
        if (!kakaoIDTokenPayload.getIss().equals(iss)) {
            throw new AuthException(ResponseCode.UNAUTHORIZED);
        }
        // aud: 서비스 앱 키와 일치해야 함
        if (!kakaoIDTokenPayload.getAud().equals(kakaoLoginProperties.getClientId())) {
            throw new AuthException(ResponseCode.UNAUTHORIZED);
        }
        // exp: 현재 UNIX 타임스탬프(Timestamp)보다 큰 값 필요(ID 토큰의 만료 여부 확인)
        if (kakaoIDTokenPayload.getExp().compareTo(System.currentTimeMillis() / 1000) < 0) {
            throw new AuthException(ResponseCode.UNAUTHORIZED);
        }
    }

    // 카카오 ID 토큰으로부터 페이로드 추출
    private KakaoIDTokenPayload extractPayloadFromTokenString(String token) {
        // 온점(.)을 기준으로 헤더, 페이로드, 서명을 분리
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new AuthException(ResponseCode.BAD_REQUEST); // Invalid JWT token
        }

        // 페이로드를 추출하여 Base64 방식으로 디코딩
        byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);

        // 페이로드를 KakaoIDTokenPayload 객체로 변환
        try {
            return new ObjectMapper().readValue(payloadBytes, KakaoIDTokenPayload.class);
        } catch (IOException e) {
            throw new AuthException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 카카오 ID 토큰 서명 검증
    private KakaoIDTokenPayload verifySignature(String token, KakaoIDTokenPublicKeyList kakaoIdTokenPublicKeyList) {
        // 카카오 ID 토큰 공개키 목록에서 헤더의 kid에 해당하는 공개키 값 검색
        KakaoIDTokenJWK kakaoIDTokenJWK = getOIDCPublicKey(extractHeaderKidFromTokenString(token), kakaoIdTokenPublicKeyList);

        // 서명 검증
        try {
            Claims payload = Jwts.parser()
                    .verifyWith(getRSAPublicKeyFromJWK(kakaoIDTokenJWK)) // JWK로 RSA Public Key 생성
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return KakaoIDTokenPayload.builder()
                    .sub(payload.getSubject())
                    .nickname(payload.get("nickname").toString())
                    .build();
        } catch (SignatureException e) {
            throw e;
        } catch (SecurityException e) {
            throw new AuthException(ResponseCode.UNAUTHORIZED);
        }
    }

    // 카카오 ID 토큰으로부터 헤더의 kid 추출
    private String extractHeaderKidFromTokenString(String token) {
        // 온점(.)을 기준으로 헤더, 페이로드, 서명을 분리
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new AuthException(ResponseCode.BAD_REQUEST); // Invalid JWT token
        }

        // 헤더을 추출하여 Base64 방식으로 디코딩
        byte[] headerBytes = Base64.getUrlDecoder().decode(parts[0]);

        // 헤더의 kid를 String으로 추출
        try {
            return new ObjectMapper().readTree(headerBytes).get("kid").asText();
        } catch (IOException e) {
            throw new AuthException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 카카오 ID 토큰 공개키 목록에서 헤더의 kid에 해당하는 공개키 값 검색
    private KakaoIDTokenJWK getOIDCPublicKey(String kid, KakaoIDTokenPublicKeyList kakaoIdTokenPublicKeyList) {
        return kakaoIdTokenPublicKeyList.getKeys().stream()
                .filter(kakaoIDTokenJWK -> kakaoIDTokenJWK.getKid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new AuthException(ResponseCode.BAD_REQUEST)); // 일치하는 PK 없음
    }

    // JWK로 RSA Public Key 생성
    private PublicKey getRSAPublicKeyFromJWK(KakaoIDTokenJWK kakaoIDTokenJWK) {
        // 모듈러스(n)와 지수(e)를 Base64 방식으로 디코딩하고 BigInteger 객체 생성
        byte[] decodeN = Base64.getUrlDecoder().decode(kakaoIDTokenJWK.getN());
        byte[] decodeE = Base64.getUrlDecoder().decode(kakaoIDTokenJWK.getE());
        BigInteger n = new BigInteger(1, decodeN);
        BigInteger e = new BigInteger(1, decodeE);

        // RSA Public Key 생성
        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);
        try {
            return KeyFactory.getInstance("RSA").generatePublic(rsaPublicKeySpec);
        } catch (GeneralSecurityException ex) {
            throw new AuthException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}
