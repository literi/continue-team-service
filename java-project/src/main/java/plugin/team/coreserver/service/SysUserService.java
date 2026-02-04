package plugin.team.coreserver.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import plugin.team.coreserver.domain.SysUser;
import plugin.team.coreserver.dto.PageResult;
import plugin.team.coreserver.dto.SysUserDTO;
import plugin.team.coreserver.repository.SysUserRepository;

@Service
public class SysUserService {

    private final SysUserRepository userRepository;

    public SysUserService(SysUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 分页查询用户（不包含密码）
     */
    public PageResult<SysUserDTO> list(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<SysUser> userPage = userRepository.findAll(pageable);
        
        List<SysUserDTO> dtoList = userPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(dtoList, userPage.getTotalElements(), page, size);
    }

    /**
     * 根据ID查询用户（不包含密码）
     */
    public SysUserDTO getById(String id) {
        return userRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    /**
     * 创建用户
     */
    @Transactional
    public SysUserDTO create(SysUser user) {
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        if (user.getStatus() == null) {
            user.setStatus(true);
        }
        SysUser saved = userRepository.save(user);
        return toDTO(saved);
    }

    /**
     * 更新用户
     */
    @Transactional
    public SysUserDTO update(String id, SysUser user) {
        SysUser existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + id));
        
        // 保留密码字段，如果新数据没有密码则不更新
        String password = existing.getPassword();
        
        BeanUtils.copyProperties(user, existing, "id", "createTime", "password");
        
        // 如果提供了新密码则更新，否则保留原密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existing.setPassword(user.getPassword());
        } else {
            existing.setPassword(password);
        }
        
        existing.setUpdateTime(LocalDateTime.now());
        SysUser saved = userRepository.save(existing);
        return toDTO(saved);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void delete(String id) {
        userRepository.deleteById(id);
    }

    /**
     * 转换为DTO（隐藏密码）
     */
    private SysUserDTO toDTO(SysUser user) {
        SysUserDTO dto = new SysUserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
}
