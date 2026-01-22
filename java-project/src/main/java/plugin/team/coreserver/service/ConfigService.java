package plugin.team.coreserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import plugin.team.coreserver.domain.entity.ConfigItem;
import plugin.team.coreserver.domain.repository.ConfigItemRepository;

import java.util.List;

@Service
public class ConfigService {

    @Autowired
    private ConfigItemRepository configItemRepository;

    public Page<ConfigItem> findAll(Pageable pageable) {
        return configItemRepository.findAll(pageable);
    }

    public ConfigItem findById(Long id) {
        return configItemRepository.findById(id).orElse(null);
    }

    public ConfigItem save(ConfigItem configItem) {
        return configItemRepository.save(configItem);
    }

    public void deleteById(Long id) {
        configItemRepository.deleteById(id);
    }

    public List<ConfigItem> findByTenantId(Long tenantId) {
        return configItemRepository.findByTenantId(tenantId);
    }

    public List<ConfigItem> findByProjectId(Long projectId) {
        return configItemRepository.findByProjectId(projectId);
    }

    public ConfigItem findByConfigKey(String configKey) {
        return configItemRepository.findByConfigKey(configKey).orElse(null);
    }

    public List<ConfigItem> findByScope(String scope) {
        return configItemRepository.findByScope(scope);
    }
}