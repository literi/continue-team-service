package plugin.team.coreserver.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import plugin.team.coreserver.domain.SysTenant;
import plugin.team.coreserver.dto.PageResult;
import plugin.team.coreserver.repository.SysTenantRepository;

@Service
public class SysTenantService {

    private final SysTenantRepository tenantRepository;

    public SysTenantService(SysTenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public PageResult<SysTenant> list(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<SysTenant> tenantPage = tenantRepository.findAll(pageable);
        return new PageResult<>(tenantPage.getContent(), tenantPage.getTotalElements(), page, size);
    }

    public SysTenant getById(String id) {
        return tenantRepository.findById(id).orElse(null);
    }

    @Transactional
    public SysTenant create(SysTenant tenant) {
        if (tenant.getId() == null || tenant.getId().isEmpty()) {
            tenant.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        tenant.setCreateTime(LocalDateTime.now());
        tenant.setUpdateTime(LocalDateTime.now());
        if (tenant.getStatus() == null) {
            tenant.setStatus(true);
        }
        return tenantRepository.save(tenant);
    }

    @Transactional
    public SysTenant update(String id, SysTenant tenant) {
        SysTenant existing = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("租户不存在: " + id));
        existing.setTenantName(tenant.getTenantName());
        existing.setTenantCode(tenant.getTenantCode());
        existing.setContactPerson(tenant.getContactPerson());
        existing.setContactPhone(tenant.getContactPhone());
        existing.setStatus(tenant.getStatus());
        existing.setExpireTime(tenant.getExpireTime());
        existing.setUpdateTime(LocalDateTime.now());
        return tenantRepository.save(existing);
    }

    @Transactional
    public void delete(String id) {
        tenantRepository.deleteById(id);
    }
}
