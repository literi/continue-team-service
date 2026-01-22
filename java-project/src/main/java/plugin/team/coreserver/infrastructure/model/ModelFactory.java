package plugin.team.coreserver.infrastructure.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import plugin.team.coreserver.domain.model.*;
import plugin.team.coreserver.domain.model.ModelProviderInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模型工厂与资源池管理
 */
@Component
@Slf4j
public class ModelFactory {
    
    private final Map<String, IChatModel> chatModelPool = new ConcurrentHashMap<>();
    private final Map<String, IEmbeddingModel> embeddingModelPool = new ConcurrentHashMap<>();
    private final Map<String, ModelProviderInfo> providerConfigs = new ConcurrentHashMap<>();
    private final Map<String, ModelHealth> modelHealthStatus = new ConcurrentHashMap<>();
    
    public ModelFactory() {
        initializeDefaultModels();
    }
    
    private void initializeDefaultModels() {
        // 初始化 OpenAI
        ModelProviderInfo openaiConfig = new ModelProviderInfo(
            "openai", 
            System.getenv().getOrDefault("OPENAI_API_KEY", "sk-default"), 
            "https://api.openai.com/v1", 
            true, 
            1
        );
        openaiConfig.setQuota(1000000); // 100万 tokens
        providerConfigs.put("openai", openaiConfig);
        
        // 初始化 Claude
        ModelProviderInfo claudeConfig = new ModelProviderInfo(
            "claude", 
            System.getenv().getOrDefault("CLAUDE_API_KEY", "sk-default"), 
            "https://api.anthropic.com/v1", 
            true, 
            2
        );
        claudeConfig.setQuota(1000000);
        providerConfigs.put("claude", claudeConfig);
        
        log.info("ModelFactory initialized with default providers");
    }
    
    /**
     * 获取 Chat 模型
     */
    public IChatModel getChatModel(String provider) throws Exception {
        if (!chatModelPool.containsKey(provider)) {
            IChatModel model = createChatModel(provider);
            chatModelPool.put(provider, model);
        }
        return chatModelPool.get(provider);
    }
    
    /**
     * 获取 Embedding 模型
     */
    public IEmbeddingModel getEmbeddingModel(String provider) throws Exception {
        if (!embeddingModelPool.containsKey(provider)) {
            IEmbeddingModel model = createEmbeddingModel(provider);
            embeddingModelPool.put(provider, model);
        }
        return embeddingModelPool.get(provider);
    }
    
    private IChatModel createChatModel(String provider) throws Exception {
        ModelProviderInfo config = providerConfigs.get(provider);
        if (config == null || !config.isEnabled()) {
            throw new Exception("Provider not configured or disabled: " + provider);
        }
        
        switch (provider.toLowerCase()) {
            case "openai":
                return new OpenAIModelAdapter(config.getApiKey(), config.getBaseUrl());
            case "claude":
                return new ClaudeModelAdapter(config.getApiKey(), config.getBaseUrl());
            default:
                throw new Exception("Unknown provider: " + provider);
        }
    }
    
    private IEmbeddingModel createEmbeddingModel(String provider) throws Exception {
        ModelProviderInfo config = providerConfigs.get(provider);
        if (config == null || !config.isEnabled()) {
            throw new Exception("Provider not configured or disabled: " + provider);
        }
        
        return new EmbeddingModelAdapter(provider, config.getApiKey(), config.getBaseUrl());
    }
    
    /**
     * 健康检查
     */
    public void healthCheck() {
        log.info("Running model health checks");
        
        chatModelPool.forEach((provider, model) -> {
            try {
                model.healthCheck();
                if (model instanceof OpenAIModelAdapter) {
                    modelHealthStatus.put(provider, ((OpenAIModelAdapter) model).getHealth());
                } else if (model instanceof ClaudeModelAdapter) {
                    modelHealthStatus.put(provider, ((ClaudeModelAdapter) model).getHealth());
                }
            } catch (Exception e) {
                log.error("Health check failed for provider: {}", provider, e);
            }
        });
        
        embeddingModelPool.forEach((provider, model) -> {
            try {
                model.healthCheck();
                if (model instanceof EmbeddingModelAdapter) {
                    modelHealthStatus.put(provider + "-embedding", ((EmbeddingModelAdapter) model).getHealth());
                }
            } catch (Exception e) {
                log.error("Health check failed for embedding provider: {}", provider, e);
            }
        });
    }
    
    /**
     * 获取所有模型健康状态
     */
    public Map<String, ModelHealth> getHealthStatus() {
        return new HashMap<>(modelHealthStatus);
    }
    
    /**
     * 获取健康的模型（按优先级排序）
     */
    public IChatModel getHealthyChatModel() throws Exception {
        List<String> healthyProviders = providerConfigs.keySet().stream()
            .filter(p -> {
                ModelHealth health = modelHealthStatus.get(p);
                return health == null || !"UNHEALTHY".equals(health.getStatus());
            })
            .sorted((a, b) -> Integer.compare(
                providerConfigs.get(a).getPriority(),
                providerConfigs.get(b).getPriority()
            ))
            .toList();
        
        if (healthyProviders.isEmpty()) {
            throw new Exception("No healthy model available");
        }
        
        return getChatModel(healthyProviders.get(0));
    }
}
