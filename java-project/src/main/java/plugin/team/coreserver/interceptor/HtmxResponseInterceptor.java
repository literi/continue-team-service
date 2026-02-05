package plugin.team.coreserver.interceptor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * HTMX 响应拦截器
 * 检测会话过期等情况，并在 HTMX 请求时返回 HX-Redirect 头
 */
@Component
public class HtmxResponseInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, 
                             HttpServletResponse response, 
                             Object handler) throws Exception {
        
        // 检查是否是 HTMX 请求
        String htmxRequest = request.getHeader("HX-Request");
        boolean isHtmxRequest = "true".equalsIgnoreCase(htmxRequest);
        
        // 检查认证状态
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 如果需要认证的路径但用户未认证，且是 HTMX 请求
        if (isHtmxRequest && 
            (authentication == null || !authentication.isAuthenticated() || 
             "anonymousUser".equals(authentication.getPrincipal().toString()))) {
            
            // 排除登录相关路径
            String requestURI = request.getRequestURI();
            if (!requestURI.startsWith("/login") && !requestURI.startsWith("/doLogin")) {
                response.setHeader("HX-Redirect", "/login");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }
        
        return true;
    }
}
