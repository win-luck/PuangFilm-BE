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
                    .capacity(30)
                    .refillIntervally(10, Duration.ofMinutes(1))
                    .build();
        }
        // 버킷 용량: 토큰 30개
        // 1분에 토큰 30개씩 리필 (버킷 용량 내에서)
        // 1시간마다 버킷 전체 리필(RateLimiterConfig에 설정됨)
    },

    HEAVY("heavy") {
        @Override
        public Bandwidth getLimit() {
            return Bandwidth.builder()
                    .capacity(5)
                    .refillIntervally(3, Duration.ofMinutes(1))
                    .build();
        }
        // 버킷 용량: 토큰 5개
        // 1분에 토큰 3개씩 리필 (버킷 용량 내에서)
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
