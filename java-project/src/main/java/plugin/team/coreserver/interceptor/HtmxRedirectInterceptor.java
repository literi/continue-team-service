package plugin.team.coreserver.interceptor;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * HTMX 认证失败重定向拦截器
 * 当 HTMX 请求遇到认证失败时，返回 HX-Redirect 头而不是普通重定向
 */
@Component
public class HtmxRedirectInterceptor implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, 
                         HttpServletResponse response, 
                         AuthenticationException authException) throws IOException {
        
        // 检查是否是 HTMX 请求
        String htmxRequest = request.getHeader("HX-Request");
        boolean isHtmxRequest = "true".equalsIgnoreCase(htmxRequest);
        
        if (isHtmxRequest) {
            // HTMX 请求：返回 HX-Redirect 头
            response.setHeader("HX-Redirect", "/login");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            // 普通请求：使用标准重定向
            response.sendRedirect("/login");
        }
    }
}
