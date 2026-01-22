package plugin.team.coreserver.domain.model;

import lombok.Data;

/**
 * 模型健康度信息
 */
@Data
public class ModelHealth {
    private String modelName;
    private String status; // HEALTHY, DEGRADED, UNHEALTHY
    private long lastHealthCheckTime;
    private double successRate;
    private long avgResponseTime; // ms
    private int consecutiveFailures;
    
    public ModelHealth(String modelName) {
        this.modelName = modelName;
        this.status = "HEALTHY";
        this.lastHealthCheckTime = System.currentTimeMillis();
        this.successRate = 1.0;
        this.avgResponseTime = 0;
        this.consecutiveFailures = 0;
    }
}
