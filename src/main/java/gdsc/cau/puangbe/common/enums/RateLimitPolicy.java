package gdsc.cau.puangbe.common.enums;

import gdsc.cau.puangbe.common.exception.RateLimiterException;
import gdsc.cau.puangbe.common.util.ResponseCode;
import io.github.bucket4j.Bandwidth;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RateLimitPolicy {
    GENERAL("general") {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(50)
                    .refillIntervally(50, Duration.ofSeconds(1))
                    .build();
        }
        // 버킷 용량: 토큰 50개
        // 1초에 토큰 50개씩 리필 (버킷 용량 내에서)
        // 1시간마다 버킷 전체 리필(RateLimiterConfig에 설정됨)
    },

    HEAVY("heavy") {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(1)
                    .refillIntervally(1, Duration.ofSeconds(1))
                    .build();
        }
        // 버킷 용량: 토큰 1개
        // 1초에 토큰 1개씩 리필 (버킷 용량 내에서)
        // 1시간마다 버킷 전체 리필(RateLimiterConfig에 설정됨)
    },

    ;

    public abstract Bandwidth getLimit();

    private final String planName;

    public static Bandwidth resolvePlan(final String targetPlan) {
        return Arrays.stream(RateLimitPolicy.values())
                .filter(policy -> policy.getPlanName().equals(targetPlan))
                .map(RateLimitPolicy::getLimit)
                .findFirst()
                .orElseThrow(() -> new RateLimiterException(ResponseCode.RATE_LIMITER_POLICY_ERROR));
    }
}