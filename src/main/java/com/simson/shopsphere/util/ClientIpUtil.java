package com.simson.shopsphere.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.regex.Pattern;

public final class ClientIpUtil {

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    private static final Pattern IPV4_PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private ClientIpUtil() {}

    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "127.0.0.1";
        }

        for (String header : IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (ipList != null && !ipList.isBlank() && !"unknown".equalsIgnoreCase(ipList)) {
                // X-Forwarded-For may contain multiple comma-separated IPs. First one is the real client.
                String firstIp = ipList.split(",")[0].trim();
                return sanitizeIp(firstIp);
            }
        }

        return sanitizeIp(request.getRemoteAddr());
    }

    public static String getClientIpFromCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null && attributes.getRequest() != null) {
                return getClientIp(attributes.getRequest());
            }
        } catch (Exception ignored) {
        }
        return "127.0.0.1";
    }

    private static String sanitizeIp(String ip) {
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            return "127.0.0.1";
        }
        // Basic length & character safety check
        String cleaned = ip.replaceAll("[^a-zA-Z0-9.:%_-]", "").trim();
        if (cleaned.length() > 45) {
            cleaned = cleaned.substring(0, 45);
        }
        return cleaned.isEmpty() ? "127.0.0.1" : cleaned;
    }
}
