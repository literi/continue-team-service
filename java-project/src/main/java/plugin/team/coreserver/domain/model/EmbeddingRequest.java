package plugin.team.coreserver.domain.model;

import java.util.Map;

/**
 * Embedding 请求
 */
public class EmbeddingRequest {
    private String text;
    private String model;
    private Map<String, String> metadata;
    
    // Getters & Setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
}