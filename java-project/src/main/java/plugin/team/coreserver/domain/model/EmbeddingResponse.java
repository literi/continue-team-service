package plugin.team.coreserver.domain.model;

import java.util.List;

/**
 * Embedding 响应
 */
public class EmbeddingResponse {
    private List<Double> embedding;
    private String model;
    private int tokensUsed;
    private boolean success;
    private String errorMessage;
    
    // Getters & Setters
    public List<Double> getEmbedding() { return embedding; }
    public void setEmbedding(List<Double> embedding) { this.embedding = embedding; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public int getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(int tokensUsed) { this.tokensUsed = tokensUsed; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}