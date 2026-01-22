package plugin.team.coreserver.domain.model;

import lombok.Data;

/**
 * 模型提供者信息
 */
@Data
public class ModelProviderInfo {
    private String provider; // openai, claude, codellama
    private String apiKey;
    private String baseUrl;
    private boolean enabled;
    private int priority; // 优先级，越低越优先
    private long quota; // 单位：tokens
    private long quotaUsedToday;
    
    public ModelProviderInfo() {}
    
    public ModelProviderInfo(String provider, String apiKey, String baseUrl, boolean enabled, int priority) {
        this.provider = provider;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.enabled = enabled;
        this.priority = priority;
        this.quota = Long.MAX_VALUE;
        this.quotaUsedToday = 0;
    }
}