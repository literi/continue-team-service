package plugin.team.coreserver.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 验证码请求频率限制过滤器
 */
@Component
@Slf4j
public class CaptchaRateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String CAPTCHA_RATE_LIMIT_PREFIX = "captcha_rate_limit:";
    private static final int MAX_REQUEST_PER_MINUTE = 5; // 每分钟最多5次请求
    private static final int TIME_WINDOW_IN_SECONDS = 60; // 时间窗口60秒

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // String requestURI = request.getRequestURI();
        
        // 只对验证码请求进行频率限制
        // if ("/api/v1/auth/captcha".equals(requestURI)) {
        //     String clientIP = getClientIpAddress(request);
        //     String rateLimitKey = CAPTCHA_RATE_LIMIT_PREFIX + clientIP;

        //     // 获取当前计数
        //     String currentCountStr = redisTemplate.opsForValue().get(rateLimitKey);
        //     int currentCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;

        //     if (currentCount >= MAX_REQUEST_PER_MINUTE) {
        //         // 超出限制，返回错误
        //         response.setStatus(429); // Too Many Requests
        //         response.setContentType("application/json;charset=UTF-8");
                
        //         ObjectMapper objectMapper = new ObjectMapper();
        //         String jsonResponse = objectMapper.writeValueAsString(
        //             plugin.team.coreserver.common.Result.error(429, "Too many captcha requests, please try again later")
        //         );
                
        //         response.getWriter().write(jsonResponse);
        //         return;
        //     }

        //     // 增加计数，设置过期时间
        //     redisTemplate.opsForValue().set(rateLimitKey, String.valueOf(currentCount + 1), TIME_WINDOW_IN_SECONDS, TimeUnit.SECONDS);
        // }

        filterChain.doFilter(request, response);
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // 处理多个IP的情况，取第一个非unknown的有效IP
            int index = xForwardedFor.indexOf(",");
            if (index != -1) {
                return xForwardedFor.substring(0, index);
            } else {
                return xForwardedFor;
            }
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}