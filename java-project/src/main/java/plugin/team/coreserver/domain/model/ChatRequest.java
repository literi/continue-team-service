package plugin.team.coreserver.domain.model;

import java.util.List;
import java.util.Map;

/**
 * Chat 请求
 */
public class ChatRequest {
    private String model;
    private String prompt;
    private List<String> context; // 上下文
    private double temperature;
    private int maxTokens;
    private Map<String, String> metadata;
    
    // Getters & Setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    
    public List<String> getContext() { return context; }
    public void setContext(List<String> context) { this.context = context; }
    
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    
    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
}