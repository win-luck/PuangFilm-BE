package gdsc.cau.puangbe.common.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

public class ClientIPUtil {
    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
    };

    public static String getClientIp(HttpServletRequest request) {
        return Arrays.stream(IP_HEADER_CANDIDATES)
                .map(request::getHeader)

                .filter(ipAddress -> ipAddress != null && !ipAddress.isEmpty() && !"unknown".equalsIgnoreCase(ipAddress))
                .map(ipAddress -> ipAddress.split(",")[0])
                .findFirst()

                .orElseGet(request::getRemoteAddr);
    }
}
