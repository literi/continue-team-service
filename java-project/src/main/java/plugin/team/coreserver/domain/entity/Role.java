package plugin.team.coreserver.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    @Column(name = "role_code", nullable = false)
    private String roleCode;
    
    @Column(name = "role_name", nullable = false)
    private String roleName;
    
    @Column(nullable = false)
    private Integer status = 1; // 1-启用，0-禁用
    
    @Column
    private Integer sort = 0;
    
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