package plugin.team.coreserver.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tenant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_code", unique = true, nullable = false)
    private String tenantCode;
    
    @Column(name = "tenant_name", nullable = false)
    private String tenantName;
    
    @Column(nullable = false)
    private Integer status = 1; // 1-启用，0-禁用
    
    @Column(name = "expire_time")
    private LocalDateTime expireTime;
    
    @Column
    private String creator;
    
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
    
    @Column
    private String updater;
    
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
    
    @Column
    private String remark;
}
