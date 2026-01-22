package plugin.team.coreserver.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private plugin.team.coreserver.domain.service.UserDetailsServiceImpl userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (jwt != null && tokenProvider.validateToken(jwt)) {
                Long userId = tokenProvider.getUserIdFromToken(jwt);
                Long tenantId = tokenProvider.getTenantIdFromToken(jwt);
                String role = tokenProvider.getRoleFromToken(jwt);
                
                // 从UserDetailsService加载用户详细信息
                UserDetails userDetails = userDetailsService.loadUserById(userId);
                
                // 创建认证对象并设置权限
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                
                // 设置自定义详情
                Map<String, Object> details = new HashMap<>();
                details.put("userId", userId);
                details.put("tenantId", tenantId);
                details.put("role", role);
                details.put("jwt", jwt);
                authentication.setDetails(details);
                
                // 将认证信息设置到SecurityContext中
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // 将信息存放在request attribute中，供后续使用
                request.setAttribute("userId", userId);
                request.setAttribute("tenantId", tenantId);
                request.setAttribute("role", role);
                request.setAttribute("jwt", jwt);
                
                log.debug("JWT validated for userId: {}, tenantId: {}, role: {}", userId, tenantId, role);
            }
        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
