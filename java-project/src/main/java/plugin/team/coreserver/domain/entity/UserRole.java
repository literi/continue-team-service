package plugin.team.coreserver.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_user_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId; // 关联sys_user.id
    
    @Column(name = "role_id", nullable = false)
    private Long roleId; // 关联sys_role.id
    
    @Column
    private String creator;
    
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
}