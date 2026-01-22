package plugin.team.coreserver.domain.model;

/**
 * 统一模型接口 - Chat API
 */
public interface IChatModel {
    
    String getName();
    
    String getProvider(); // openai, claude, codellama
    
    ChatResponse chat(ChatRequest request) throws Exception;
    
    void healthCheck() throws Exception;
}
