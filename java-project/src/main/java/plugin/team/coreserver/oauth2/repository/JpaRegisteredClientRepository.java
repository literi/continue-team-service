package plugin.team.coreserver.oauth2.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import plugin.team.coreserver.domain.Oauth2RegisteredClient;
import plugin.team.coreserver.repository.Oauth2RegisteredClientRepository;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JPA 实现的 RegisteredClientRepository
 */
@Component
public class JpaRegisteredClientRepository implements RegisteredClientRepository {

    private final Oauth2RegisteredClientRepository clientRepository;
    private final ObjectMapper objectMapper;

    public JpaRegisteredClientRepository(Oauth2RegisteredClientRepository clientRepository,
                                        ObjectMapper objectMapper) {
        this.clientRepository = clientRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        Oauth2RegisteredClient entity = clientRepository.findByClientId(registeredClient.getClientId())
                .orElse(new Oauth2RegisteredClient());
        
        entity.setId(registeredClient.getId());
        entity.setClientId(registeredClient.getClientId());
        entity.setClientIdIssuedAt(registeredClient.getClientIdIssuedAt() != null 
                ? registeredClient.getClientIdIssuedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                : java.time.LocalDateTime.now());
        entity.setClientSecret(registeredClient.getClientSecret());
        entity.setClientSecretExpiresAt(registeredClient.getClientSecretExpiresAt() != null
                ? registeredClient.getClientSecretExpiresAt().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                : null);
        entity.setClientName(registeredClient.getClientName());
        entity.setClientAuthenticationMethods(String.join(",", registeredClient.getClientAuthenticationMethods().stream()
                .map(ClientAuthenticationMethod::getValue)
                .collect(Collectors.toSet())));
        entity.setAuthorizationGrantTypes(String.join(",", registeredClient.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantType::getValue)
                .collect(Collectors.toSet())));
        entity.setRedirectUris(String.join(",", registeredClient.getRedirectUris()));
        entity.setPostLogoutRedirectUris(String.join(",", registeredClient.getPostLogoutRedirectUris()));
        entity.setScopes(String.join(",", registeredClient.getScopes()));
        
        try {
            entity.setClientSettings(objectMapper.writeValueAsString(registeredClient.getClientSettings().getSettings()));
            entity.setTokenSettings(objectMapper.writeValueAsString(registeredClient.getTokenSettings().getSettings()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize settings", e);
        }
        
        if (entity.getStatus() == null) {
            entity.setStatus(true);
        }
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(java.time.LocalDateTime.now());
        }
        entity.setUpdateTime(java.time.LocalDateTime.now());
        
        // 如果没有 tenantId，设置默认值
        if (entity.getTenantId() == null || entity.getTenantId().isEmpty()) {
            entity.setTenantId("default");
        }
        
        clientRepository.save(entity);
    }

    @Override
    public RegisteredClient findById(String id) {
        return clientRepository.findById(id)
                .map(this::toRegisteredClient)
                .orElse(null);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return clientRepository.findByClientId(clientId)
                .map(this::toRegisteredClient)
                .orElse(null);
    }

    private RegisteredClient toRegisteredClient(Oauth2RegisteredClient entity) {
        if (entity == null || !Boolean.TRUE.equals(entity.getStatus())) {
            return null;
        }
        
        RegisteredClient.Builder builder = RegisteredClient.withId(entity.getId())
                .clientId(entity.getClientId())
                .clientIdIssuedAt(entity.getClientIdIssuedAt() != null
                        ? entity.getClientIdIssuedAt().atZone(java.time.ZoneId.systemDefault()).toInstant()
                        : Instant.now())
                .clientSecret(entity.getClientSecret())
                .clientSecretExpiresAt(entity.getClientSecretExpiresAt() != null
                        ? entity.getClientSecretExpiresAt().atZone(java.time.ZoneId.systemDefault()).toInstant()
                        : null)
                .clientName(entity.getClientName());

        // 解析客户端认证方法
        if (StringUtils.hasText(entity.getClientAuthenticationMethods())) {
            Set<ClientAuthenticationMethod> methods = Stream.of(entity.getClientAuthenticationMethods().split(","))
                    .map(String::trim)
                    .map(ClientAuthenticationMethod::new)
                    .collect(Collectors.toSet());
            builder.clientAuthenticationMethods(m -> m.addAll(methods));
        }

        // 解析授权类型
        if (StringUtils.hasText(entity.getAuthorizationGrantTypes())) {
            Set<AuthorizationGrantType> grantTypes = Stream.of(entity.getAuthorizationGrantTypes().split(","))
                    .map(String::trim)
                    .map(AuthorizationGrantType::new)
                    .collect(Collectors.toSet());
            builder.authorizationGrantTypes(g -> g.addAll(grantTypes));
        }

        // 解析重定向 URI
        if (StringUtils.hasText(entity.getRedirectUris())) {
            builder.redirectUris(uris -> uris.addAll(Stream.of(entity.getRedirectUris().split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet())));
        }

        // 解析登出重定向 URI
        if (StringUtils.hasText(entity.getPostLogoutRedirectUris())) {
            builder.postLogoutRedirectUris(uris -> uris.addAll(Stream.of(entity.getPostLogoutRedirectUris().split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet())));
        }

        // 解析作用域
        if (StringUtils.hasText(entity.getScopes())) {
            builder.scopes(scopes -> scopes.addAll(Stream.of(entity.getScopes().split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet())));
        }

        // 解析客户端设置
        try {
            if (StringUtils.hasText(entity.getClientSettings())) {
                java.util.Map<String, Object> settings = objectMapper.readValue(
                        entity.getClientSettings(),
                        new TypeReference<java.util.Map<String, Object>>() {});
                builder.clientSettings(ClientSettings.withSettings(settings).build());
            } else {
                builder.clientSettings(ClientSettings.builder().build());
            }
        } catch (Exception e) {
            builder.clientSettings(ClientSettings.builder().build());
        }

        // 解析令牌设置
        try {
            if (StringUtils.hasText(entity.getTokenSettings())) {
                java.util.Map<String, Object> settings = objectMapper.readValue(
                        entity.getTokenSettings(),
                        new TypeReference<java.util.Map<String, Object>>() {});
                builder.tokenSettings(TokenSettings.withSettings(settings).build());
            } else {
                builder.tokenSettings(TokenSettings.builder().build());
            }
        } catch (Exception e) {
            builder.tokenSettings(TokenSettings.builder().build());
        }

        return builder.build();
    }
}
