package plugin.team.coreserver.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_permission")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId = 0L; // 0-系统公共权限，>0-租户独有权限
    
    @Column(name = "parent_id", nullable = false)
    private Long parentId = 0L; // 0-顶级权限
    
    @Column(name = "perm_code", nullable = false, unique = true)
    private String permCode; // 权限编码（全局唯一，如：sys:user:add）
    
    @Column(name = "perm_name", nullable = false)
    private String permName; // 权限名称（如：用户新增）
    
    @Column(name = "perm_type", nullable = false)
    private Integer permType; // 1-菜单，2-按钮/接口，3-数据权限
    
    @Column(name = "resource_path")
    private String resourcePath; // 资源路径（如菜单URL、接口路径）
    
    @Column
    private String icon; // 菜单图标（仅菜单类型）
    
    @Column
    private Integer sort = 0; // 排序值
    
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