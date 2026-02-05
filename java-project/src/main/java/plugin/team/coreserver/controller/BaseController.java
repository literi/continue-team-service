package plugin.team.coreserver.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controller 基类
 * 提供通用的 HTMX 重定向方法
 */
public abstract class BaseController {

    /**
     * 检查是否是 HTMX 请求
     */
    protected boolean isHtmxRequest(HttpServletRequest request) {
        String htmxRequest = request.getHeader("HX-Request");
        return "true".equalsIgnoreCase(htmxRequest);
    }

    /**
     * 检查用户是否已认证
     */
    protected boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getPrincipal().toString());
    }

    /**
     * 如果是 HTMX 请求且未认证，设置重定向头
     * @return true 如果已设置重定向，false 如果继续处理
     */
    protected boolean checkAuthAndRedirect(HttpServletRequest request, HttpServletResponse response) {
        if (isHtmxRequest(request) && !isAuthenticated()) {
            response.setHeader("HX-Redirect", "/login");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return true;
        }
        return false;
    }

    /**
     * 在方法执行前检查认证（可选，通过 @ModelAttribute 使用）
     */
    @ModelAttribute
    public void checkAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // 这个方法会在每个请求处理方法执行前被调用
        // 但为了不影响正常流程，我们只在需要的地方手动调用 checkAuthAndRedirect
    }
}
