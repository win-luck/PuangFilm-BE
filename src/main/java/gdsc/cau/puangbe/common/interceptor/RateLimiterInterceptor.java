package gdsc.cau.puangbe.common.interceptor;

import gdsc.cau.puangbe.common.enums.RateLimitPolicy;
import gdsc.cau.puangbe.common.exception.RateLimiterException;
import gdsc.cau.puangbe.common.util.ClientIPUtil;
import gdsc.cau.puangbe.common.util.ResponseCode;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimiterInterceptor implements HandlerInterceptor {
    private final LettuceBasedProxyManager<String> proxyManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // key 생성 (IP + API 엔트포인트)
        String clientIp = ClientIPUtil.getClientIp(request);
        String servletPath = request.getServletPath();
        String key = clientIp + servletPath;

        // key에 해당하는 bucket 로드. 없으면 생성
        Bucket bucket = proxyManager.getProxy(key, () -> getRateLimitPolicy(servletPath));

        if (!bucket.tryConsume(1)) {
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
}
