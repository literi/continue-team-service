package plugin.team.coreserver.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import plugin.team.coreserver.domain.SysRole;
import plugin.team.coreserver.dto.PageResult;
import plugin.team.coreserver.repository.SysRoleRepository;

@Service
public class SysRoleService {

    private final SysRoleRepository roleRepository;

    public SysRoleService(SysRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public PageResult<SysRole> list(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<SysRole> rolePage = roleRepository.findAll(pageable);
        return new PageResult<>(rolePage.getContent(), rolePage.getTotalElements(), page, size);
    }

    public SysRole getById(String id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Transactional
    public SysRole create(SysRole role) {
        if (role.getId() == null || role.getId().isEmpty()) {
            role.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        if (role.getStatus() == null) {
            role.setStatus(true);
        }
        return roleRepository.save(role);
    }

    @Transactional
    public SysRole update(String id, SysRole role) {
        SysRole existing = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + id));
        existing.setRoleName(role.getRoleName());
        existing.setRoleCode(role.getRoleCode());
        existing.setDescription(role.getDescription());
        existing.setStatus(role.getStatus());
        existing.setUpdateTime(LocalDateTime.now());
        return roleRepository.save(existing);
    }

    @Transactional
    public void delete(String id) {
        roleRepository.deleteById(id);
    }
}
