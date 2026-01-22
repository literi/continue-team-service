package plugin.team.coreserver.infrastructure.model;

import lombok.extern.slf4j.Slf4j;
import plugin.team.coreserver.domain.model.ChatRequest;
import plugin.team.coreserver.domain.model.ChatResponse;
import plugin.team.coreserver.domain.model.IChatModel;
import plugin.team.coreserver.domain.model.ModelHealth;

/**
 * OpenAI 模型适配器
 */
@Slf4j
public class OpenAIModelAdapter implements IChatModel {
    
    private String apiKey;
    private String baseUrl;
    private ModelHealth health;
    
    public OpenAIModelAdapter(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl != null ? baseUrl : "https://api.openai.com/v1";
        this.health = new ModelHealth("GPT-4o");
    }
    
    @Override
    public String getName() {
        return "GPT-4o";
    }
    
    @Override
    public String getProvider() {
        return "openai";
    }
    
    @Override
    public ChatResponse chat(ChatRequest request) throws Exception {
        long startTime = System.currentTimeMillis();
        ChatResponse response = new ChatResponse();
        
        try {
            // 模拟 OpenAI API 调用
            log.info("Calling OpenAI GPT-4o for model: {}", request.getModel());
            
            // 实际实现需要调用 OpenAI API，这里仅示意
            response.setContent("This is a simulated response from OpenAI GPT-4o");
            response.setModel("gpt-4o");
            response.setTokensUsed(100);
            response.setSuccess(true);
            
            long duration = System.currentTimeMillis() - startTime;
            updateHealth(true, duration);
            
            return response;
        } catch (Exception e) {
            log.error("Error calling OpenAI API", e);
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
            // 模拟健康检查
            log.info("Health check for OpenAI");
            updateHealth(true, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("Health check failed for OpenAI", e);
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
