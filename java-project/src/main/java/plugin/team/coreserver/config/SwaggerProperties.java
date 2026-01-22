package plugin.team.coreserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Swagger配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {
    /**
     * 是否启用Swagger
     */
    private boolean enable = true;
    
    /**
     * 文档标题
     */
    private String title = "Core Server API";
    
    /**
     * 文档描述
     */
    private String description = "Core Server API Documentation";
    
    /**
     * 版本号
     */
    private String version = "1.0.0";
    
    /**
     * 联系人姓名
     */
    private String contactName = "Core Server Team";
    
    /**
     * 联系人邮箱
     */
    private String contactEmail = "team@coreserver.com";
}