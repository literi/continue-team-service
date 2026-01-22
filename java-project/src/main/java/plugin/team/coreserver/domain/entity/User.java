package plugin.team.coreserver.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "real_name")
    private String realName;
    
    @Column
    private String mobile;
    
    @Column
    private String email;
    
    @Column(nullable = false)
    private Integer status = 1; // 1-启用，0-禁用
    
    @Column
    private String creator;
    
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
    
    @Column
    private String updater;
    
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
}
