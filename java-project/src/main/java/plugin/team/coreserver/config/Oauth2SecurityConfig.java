package plugin.team.coreserver.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import plugin.team.coreserver.domain.SysUser;
import plugin.team.coreserver.repository.SysUserRepository;

@Configuration
@EnableWebSecurity
public class Oauth2SecurityConfig {

    @Autowired
    private SysUserRepository userRepository;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/login");
                })
        ).authorizeHttpRequests(registry -> registry
                        // 放行登录相关请求
                        .requestMatchers("/login", "/doLogin", "/css/**", "/js/**").permitAll()
                        // 其他所有请求需要认证
                        .anyRequest().authenticated()
                )
                // 表单登录配置（写法不变，6.1+ 仍支持）
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/doLogin")
                        .defaultSuccessUrl("/index", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                // 退出登录配置
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // 从数据库查询用户
            Optional<SysUser> userOpt = Optional.ofNullable(userRepository.findByUsername(username));
            SysUser sysUser = userOpt.orElseThrow(() -> new UsernameNotFoundException("用户名不存在：" + username));

            // 安全获取密码
            String password = userOpt
                    .map(SysUser::getPassword)
                    .orElseThrow(() -> new UsernameNotFoundException("用户不存在或密码为空"));

            // 处理角色（根据实际字段调整，假设 SysUser 有 getRoles() 方法返回逗号分隔的角色字符串）
            String[] roleArray = new String[]{"USER"};

            // 构建 UserDetails（修复 disabled 逻辑）
            return User.builder()
                    .username(sysUser.getUsername())
                    .password(password)
                    .roles(roleArray) // 赋予默认角色避免空角色问题
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(!sysUser.getStatus()) // 假设 status=1 为启用，!=1 则禁用
                    .build();
        };
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
        return NoOpPasswordEncoder.getInstance();
    }
}