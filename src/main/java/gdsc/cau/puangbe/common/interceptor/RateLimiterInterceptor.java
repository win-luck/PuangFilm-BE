package gdsc.cau.puangbe.common.interceptor;

import gdsc.cau.puangbe.auth.external.JwtProvider;
import gdsc.cau.puangbe.common.enums.RateLimitPolicy;
import gdsc.cau.puangbe.common.exception.RateLimiterException;
import gdsc.cau.puangbe.common.util.ResponseCode;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RateLimiterInterceptor implements HandlerInterceptor {
    private final LettuceBasedProxyManager<String> proxyManager;
    private final RedisTemplate<String, Long> redisTemplate;
    private final JwtProvider jwtProvider;

    private final String LIMITER_PREFIX = "rate-limiter-count:";
    private final String BLOCKED_PREFIX = "rate-limiter-blocked:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // key 생성 (kakaoId + API 엔트포인트)
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null) {
            return true;
        }

        String accessToken = jwtProvider.getTokenFromAuthorizationHeader(authorizationHeader);
        String kakaoId = jwtProvider.getKakaoIdFromExpiredToken(accessToken);
        String servletPath = request.getServletPath();
        String key = kakaoId + servletPath;

        if (isBlocked(key)) {
            throw new RateLimiterException(ResponseCode.RATE_LIMITER_TOO_MANY_REQUESTS);
        }

        // key에 해당하는 bucket 로드. 없으면 생성
        Bucket bucket = proxyManager.getProxy(LIMITER_PREFIX + key, () -> getRateLimitPolicy(servletPath));

        if (!bucket.tryConsume(1)) {
            blockClient(key);
            throw new RateLimiterException(ResponseCode.RATE_LIMITER_TOO_MANY_REQUESTS);
        }
        return true;
    }

    private BucketConfiguration bucketConfiguration(String bucketPlan) {
        return BucketConfiguration.builder()
                .addLimit(RateLimitPolicy.resolvePlan(bucketPlan))
                .build();
    }

    private BucketConfiguration getRateLimitPolicy(String contextPath) {
        switch (contextPath) {
            case "/api/photo-request":
                return bucketConfiguration("heavy");

            default:
                return bucketConfiguration("general");
        }
    }

    private void blockClient(String key) {
        redisTemplate.opsForValue().set(BLOCKED_PREFIX + key, 0L, Duration.ofMinutes(5));
    }

    private boolean isBlocked(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLOCKED_PREFIX + key));
    }
}
