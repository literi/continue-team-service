package plugin.team.coreserver.infrastructure.model;

import lombok.extern.slf4j.Slf4j;
import plugin.team.coreserver.domain.model.EmbeddingRequest;
import plugin.team.coreserver.domain.model.EmbeddingResponse;
import plugin.team.coreserver.domain.model.IEmbeddingModel;
import plugin.team.coreserver.domain.model.ModelHealth;

/**
 * 通用 Embedding 模型适配器
 */
@Slf4j
public class EmbeddingModelAdapter implements IEmbeddingModel {
    
    private String provider;
    private String apiKey;
    private String baseUrl;
    private ModelHealth health;
    
    public EmbeddingModelAdapter(String provider, String apiKey, String baseUrl) {
        this.provider = provider;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.health = new ModelHealth(provider + "-embedding");
    }
    
    @Override
    public String getName() {
        return provider + "-embedding";
    }
    
    @Override
    public String getProvider() {
        return provider;
    }
    
    @Override
    public EmbeddingResponse embed(EmbeddingRequest request) throws Exception {
        long startTime = System.currentTimeMillis();
        EmbeddingResponse response = new EmbeddingResponse();
        
        try {
            log.info("Embedding text using {}", provider);
            
            // 模拟向量生成（1536 维）
            java.util.List<Double> embedding = new java.util.ArrayList<>();
            for (int i = 0; i < 1536; i++) {
                embedding.add(Math.random());
            }
            
            response.setEmbedding(embedding);
            response.setModel(provider + "-embedding");
            response.setTokensUsed(request.getText().split("\\s+").length);
            response.setSuccess(true);
            
            long duration = System.currentTimeMillis() - startTime;
            updateHealth(true, duration);
            
            return response;
        } catch (Exception e) {
            log.error("Error embedding text", e);
            response.setSuccess(false);
            response.setErrorMessage(e.getMessage());
            
            updateHealth(false, System.currentTimeMillis() - startTime);
            return response;
        }
    }
    
    @Override
    public void healthCheck() throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            log.info("Health check for {} embedding", provider);
            updateHealth(true, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("Health check failed for {}", provider, e);
            updateHealth(false, System.currentTimeMillis() - startTime);
            throw e;
        }
    }
    
    private void updateHealth(boolean success, long duration) {
        health.setLastHealthCheckTime(System.currentTimeMillis());
        health.setAvgResponseTime((health.getAvgResponseTime() + duration) / 2);
        
        if (success) {
            health.setConsecutiveFailures(0);
            health.setStatus("HEALTHY");
        } else {
            health.setConsecutiveFailures(health.getConsecutiveFailures() + 1);
            if (health.getConsecutiveFailures() >= 3) {
                health.setStatus("UNHEALTHY");
            } else {
                health.setStatus("DEGRADED");
            }
        }
    }
    
    public ModelHealth getHealth() {
        return health;
    }
}
