package plugin.team.coreserver.domain.model;

/**
 * Chat 响应
 */
public class ChatResponse {
    private String content;
    private int tokensUsed;
    private String model;
    private boolean success;
    private String errorMessage;
    
    // Getters & Setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public int getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(int tokensUsed) { this.tokensUsed = tokensUsed; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}