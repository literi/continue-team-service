package plugin.team.coreserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * OAuth2 资源服务器配置
 * 保护需要 OAuth2 访问令牌的 API 端点
 */
@Configuration
@EnableWebSecurity
public class OAuth2ResourceServerConfig {

    /**
     * 资源服务器安全过滤器链
     * 验证 JWT 访问令牌并保护资源端点
     */
    @Bean
    public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**") // 只保护 /api/** 路径
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                            // 配置 JWT 转换器，将 JWT 中的 claims 转换为 Spring Security 权限
                            JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
                            JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                            // 从 JWT 的 scope claim 中提取权限
                            jwtGrantedAuthoritiesConverter.setAuthorityPrefix("SCOPE_");
                            jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("scope");
                            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
                            jwt.jwtAuthenticationConverter(jwtAuthenticationConverter);
                        })
                );

        return http.build();
    }
}
