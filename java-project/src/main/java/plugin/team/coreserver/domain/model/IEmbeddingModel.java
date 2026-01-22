package plugin.team.coreserver.domain.model;

import java.util.Map;

/**
 * 统一模型接口 - Embedding API
 */
public interface IEmbeddingModel {
    
    String getName();
    
    String getProvider();
    
    EmbeddingResponse embed(EmbeddingRequest request) throws Exception;
    
    void healthCheck() throws Exception;
}
