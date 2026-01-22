package plugin.team.coreserver.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import plugin.team.coreserver.common.Result;
import plugin.team.coreserver.domain.model.ChatRequest;
import plugin.team.coreserver.domain.model.ChatResponse;
import plugin.team.coreserver.domain.model.EmbeddingRequest;
import plugin.team.coreserver.domain.model.EmbeddingResponse;
import plugin.team.coreserver.domain.model.IChatModel;
import plugin.team.coreserver.domain.model.IEmbeddingModel;
import plugin.team.coreserver.domain.model.ModelHealth;
import plugin.team.coreserver.infrastructure.model.ModelFactory;

@RestController
@RequestMapping("/api/v1/models")
@Slf4j
@Tag(name = "模型管理", description = "AI模型调用、嵌入和健康检查相关接口")
public class ModelController {
    
    @Autowired
    private ModelFactory modelFactory;
    
    @Operation(summary = "聊天模型调用", description = "调用指定的聊天模型进行对话")
    @ApiResponse(responseCode = "200", description = "调用成功",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Result.class)))
    @PostMapping("/chat")
    public ResponseEntity<Result<ChatResponse>> chat(@RequestBody Map<String, Object> request) {
        try {
            String provider = (String) request.getOrDefault("provider", "openai");
            String prompt = (String) request.get("prompt");
            
            if (prompt == null || prompt.isEmpty()) {
                return ResponseEntity.ok(Result.error("Prompt is required"));
            }
            
            IChatModel model = modelFactory.getChatModel(provider);
            ChatRequest chatRequest = new ChatRequest();
            chatRequest.setModel(provider);
            chatRequest.setPrompt(prompt);
            chatRequest.setMaxTokens(((Number) request.getOrDefault("maxTokens", 1000)).intValue());
            chatRequest.setTemperature(((Number) request.getOrDefault("temperature", 0.7)).doubleValue());
            
            ChatResponse response = model.chat(chatRequest);
            
            return ResponseEntity.ok(Result.success(response));
        } catch (Exception e) {
            log.error("Error calling chat model", e);
            return ResponseEntity.ok(Result.error(e.getMessage()));
        }
    }
    
    @Operation(summary = "嵌入模型调用", description = "调用指定的嵌入模型生成向量")
    @ApiResponse(responseCode = "200", description = "调用成功",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Result.class)))
    @PostMapping("/embed")
    public ResponseEntity<Result<EmbeddingResponse>> embed(@RequestBody Map<String, String> request) {
        try {
            String provider = request.getOrDefault("provider", "openai");
            String text = request.get("text");
            
            if (text == null || text.isEmpty()) {
                return ResponseEntity.ok(Result.error("Text is required"));
            }
            
            IEmbeddingModel model = modelFactory.getEmbeddingModel(provider);
            EmbeddingRequest embRequest = new EmbeddingRequest();
            embRequest.setText(text);
            embRequest.setModel(provider);
            
            EmbeddingResponse response = model.embed(embRequest);
            
            return ResponseEntity.ok(Result.success(response));
        } catch (Exception e) {
            log.error("Error calling embedding model", e);
            return ResponseEntity.ok(Result.error(e.getMessage()));
        }
    }
    
    @Operation(summary = "模型健康状态", description = "获取所有模型的健康状态")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Result.class)))
    @GetMapping("/health")
    public ResponseEntity<Result<Map<String, ModelHealth>>> getHealthStatus() {
        try {
            modelFactory.healthCheck();
            Map<String, ModelHealth> health = modelFactory.getHealthStatus();
            return ResponseEntity.ok(Result.success(health));
        } catch (Exception e) {
            log.error("Error getting health status", e);
            return ResponseEntity.ok(Result.error(e.getMessage()));
        }
    }
}
