package plugin.team.coreserver.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import plugin.team.coreserver.domain.Oauth2RegisteredClient;
import plugin.team.coreserver.dto.PageResult;
import plugin.team.coreserver.repository.Oauth2RegisteredClientRepository;

@Service
public class Oauth2RegisteredClientService {

    private final Oauth2RegisteredClientRepository clientRepository;

    public Oauth2RegisteredClientService(Oauth2RegisteredClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public PageResult<Oauth2RegisteredClient> list(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Oauth2RegisteredClient> clientPage = clientRepository.findAll(pageable);
        return new PageResult<>(clientPage.getContent(), clientPage.getTotalElements(), page, size);
    }

    public Oauth2RegisteredClient getById(String id) {
        return clientRepository.findById(id).orElse(null);
    }

    @Transactional
    public Oauth2RegisteredClient create(Oauth2RegisteredClient client) {
        if (client.getId() == null || client.getId().isEmpty()) {
            client.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (client.getClientIdIssuedAt() == null) {
            client.setClientIdIssuedAt(LocalDateTime.now());
        }
        if (client.getStatus() == null) {
            client.setStatus(true);
        }
        if (client.getCreateTime() == null) {
            client.setCreateTime(LocalDateTime.now());
        }
        client.setUpdateTime(LocalDateTime.now());
        return clientRepository.save(client);
    }

    @Transactional
    public Oauth2RegisteredClient update(String id, Oauth2RegisteredClient client) {
        Oauth2RegisteredClient existing = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("客户端不存在: " + id));
        
        existing.setClientName(client.getClientName());
        existing.setClientSecret(client.getClientSecret());
        existing.setClientSecretExpiresAt(client.getClientSecretExpiresAt());
        existing.setClientAuthenticationMethods(client.getClientAuthenticationMethods());
        existing.setAuthorizationGrantTypes(client.getAuthorizationGrantTypes());
        existing.setRedirectUris(client.getRedirectUris());
        existing.setPostLogoutRedirectUris(client.getPostLogoutRedirectUris());
        existing.setScopes(client.getScopes());
        existing.setClientSettings(client.getClientSettings());
        existing.setTokenSettings(client.getTokenSettings());
        existing.setStatus(client.getStatus());
        existing.setUpdateTime(LocalDateTime.now());
        
        return clientRepository.save(existing);
    }

    @Transactional
    public void delete(String id) {
        clientRepository.deleteById(id);
    }
}
