package plugin.team.coreserver.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import plugin.team.coreserver.domain.SysPermission;
import plugin.team.coreserver.dto.PageResult;
import plugin.team.coreserver.repository.SysPermissionRepository;

@Service
public class SysPermissionService {

    private final SysPermissionRepository permissionRepository;

    public SysPermissionService(SysPermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public PageResult<SysPermission> list(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "sort", "createTime"));
        Page<SysPermission> permPage = permissionRepository.findAll(pageable);
        return new PageResult<>(permPage.getContent(), permPage.getTotalElements(), page, size);
    }

    public SysPermission getById(String id) {
        return permissionRepository.findById(id).orElse(null);
    }

    @Transactional
    public SysPermission create(SysPermission permission) {
        if (permission.getId() == null || permission.getId().isEmpty()) {
            permission.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        permission.setCreateTime(LocalDateTime.now());
        permission.setUpdateTime(LocalDateTime.now());
        if (permission.getStatus() == null) {
            permission.setStatus(true);
        }
        if (permission.getSort() == null) {
            permission.setSort(0);
        }
        return permissionRepository.save(permission);
    }

    @Transactional
    public SysPermission update(String id, SysPermission permission) {
        SysPermission existing = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("权限不存在: " + id));
        existing.setPermName(permission.getPermName());
        existing.setPermCode(permission.getPermCode());
        existing.setPermType(permission.getPermType());
        existing.setResourcePath(permission.getResourcePath());
        existing.setMethod(permission.getMethod());
        existing.setParentId(permission.getParentId());
        existing.setSort(permission.getSort());
        existing.setStatus(permission.getStatus());
        existing.setUpdateTime(LocalDateTime.now());
        return permissionRepository.save(existing);
    }

    @Transactional
    public void delete(String id) {
        permissionRepository.deleteById(id);
    }
}
