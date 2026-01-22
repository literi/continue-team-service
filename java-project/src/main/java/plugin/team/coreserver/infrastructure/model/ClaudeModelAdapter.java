package plugin.team.coreserver.infrastructure.model;

import lombok.extern.slf4j.Slf4j;
import plugin.team.coreserver.domain.model.ChatRequest;
import plugin.team.coreserver.domain.model.ChatResponse;
import plugin.team.coreserver.domain.model.IChatModel;
import plugin.team.coreserver.domain.model.ModelHealth;

/**
 * Claude 模型适配器
 */
@Slf4j
public class ClaudeModelAdapter implements IChatModel {
    
    private String apiKey;
    private String baseUrl;
    private ModelHealth health;
    
    public ClaudeModelAdapter(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl != null ? baseUrl : "https://api.anthropic.com/v1";
        this.health = new ModelHealth("Claude-3-Opus");
    }
    
    @Override
    public String getName() {
        return "Claude-3-Opus";
    }
    
    @Override
    public String getProvider() {
        return "claude";
    }
    
    @Override
    public ChatResponse chat(ChatRequest request) throws Exception {
        long startTime = System.currentTimeMillis();
        ChatResponse response = new ChatResponse();
        
        try {
            log.info("Calling Claude API for model: {}", request.getModel());
            
            response.setContent("This is a simulated response from Claude 3 Opus");
            response.setModel("claude-3-opus");
            response.setTokensUsed(80);
            response.setSuccess(true);
            
            long duration = System.currentTimeMillis() - startTime;
            updateHealth(true, duration);
            
            return response;
        } catch (Exception e) {
            log.error("Error calling Claude API", e);
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
            log.info("Health check for Claude");
            updateHealth(true, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.error("Health check failed for Claude", e);
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
