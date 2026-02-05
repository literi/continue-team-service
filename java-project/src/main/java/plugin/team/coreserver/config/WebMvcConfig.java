package plugin.team.coreserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import plugin.team.coreserver.interceptor.HtmxResponseInterceptor;

/**
 * Web MVC 配置
 * 注册拦截器
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private HtmxResponseInterceptor htmxResponseInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(htmxResponseInterceptor)
                .addPathPatterns("/admin/**")  // 只拦截管理后台路径
                .excludePathPatterns("/login", "/doLogin", "/css/**", "/js/**", "/favicon.ico");
    }
}
