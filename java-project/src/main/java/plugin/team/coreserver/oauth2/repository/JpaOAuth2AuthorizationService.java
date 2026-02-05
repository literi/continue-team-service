package plugin.team.coreserver.oauth2.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import plugin.team.coreserver.domain.Oauth2Authorization;
import plugin.team.coreserver.repository.Oauth2AuthorizationRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JPA 实现的 OAuth2AuthorizationService
 */
@Component
public class JpaOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final Oauth2AuthorizationRepository authorizationRepository;
    private final RegisteredClientRepository registeredClientRepository;
    private final ObjectMapper objectMapper;

    public JpaOAuth2AuthorizationService(Oauth2AuthorizationRepository authorizationRepository,
                                         RegisteredClientRepository registeredClientRepository,
                                         ObjectMapper objectMapper) {
        this.authorizationRepository = authorizationRepository;
        this.registeredClientRepository = registeredClientRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        Oauth2Authorization entity = authorizationRepository.findById(authorization.getId())
                .orElse(new Oauth2Authorization());
        
        entity.setId(authorization.getId());
        entity.setRegisteredClientId(authorization.getRegisteredClientId());
        entity.setPrincipalName(authorization.getPrincipalName());
        entity.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());
        entity.setAuthorizedScopes(String.join(",", authorization.getAuthorizedScopes()));
        
        try {
            entity.setAttributes(objectMapper.writeValueAsString(authorization.getAttributes()));
        } catch (Exception e) {
            // 忽略序列化错误
        }
        
        entity.setState(authorization.getAttribute(OAuth2ParameterNames.STATE));

        // 授权码
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCodeToken = 
                authorization.getToken(OAuth2AuthorizationCode.class);
        if (authorizationCodeToken != null) {
            OAuth2AuthorizationCode authorizationCode = authorizationCodeToken.getToken();
            entity.setAuthorizationCodeValue(authorizationCode.getTokenValue());
            entity.setAuthorizationCodeIssuedAt(toLocalDateTime(authorizationCode.getIssuedAt()));
            entity.setAuthorizationCodeExpiresAt(toLocalDateTime(authorizationCode.getExpiresAt()));
        }

        // 访问令牌
        OAuth2Authorization.Token<OAuth2AccessToken> accessTokenToken = 
                authorization.getToken(OAuth2AccessToken.class);
        if (accessTokenToken != null) {
            OAuth2AccessToken accessToken = accessTokenToken.getToken();
            entity.setAccessTokenValue(accessToken.getTokenValue());
            entity.setAccessTokenIssuedAt(toLocalDateTime(accessToken.getIssuedAt()));
            entity.setAccessTokenExpiresAt(toLocalDateTime(accessToken.getExpiresAt()));
            entity.setAccessTokenType(accessToken.getTokenType().getValue());
            entity.setAccessTokenScopes(String.join(",", accessToken.getScopes()));
        }

        // 刷新令牌
        OAuth2Authorization.Token<OAuth2RefreshToken> refreshTokenToken = 
                authorization.getToken(OAuth2RefreshToken.class);
        if (refreshTokenToken != null) {
            OAuth2RefreshToken refreshToken = refreshTokenToken.getToken();
            entity.setRefreshTokenValue(refreshToken.getTokenValue());
            entity.setRefreshTokenIssuedAt(toLocalDateTime(refreshToken.getIssuedAt()));
            entity.setRefreshTokenExpiresAt(toLocalDateTime(refreshToken.getExpiresAt()));
        }

        // 如果没有 tenantId，设置默认值
        if (entity.getTenantId() == null || entity.getTenantId().isEmpty()) {
            entity.setTenantId("default");
        }

        authorizationRepository.save(entity);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        authorizationRepository.deleteById(authorization.getId());
    }

    @Override
    public OAuth2Authorization findById(String id) {
        return authorizationRepository.findById(id)
                .map(this::toOAuth2Authorization)
                .orElse(null);
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        if (tokenType == null) {
            return authorizationRepository.findAll().stream()
                    .filter(entity -> token.equals(entity.getAuthorizationCodeValue()) ||
                            token.equals(entity.getAccessTokenValue()) ||
                            token.equals(entity.getRefreshTokenValue()))
                    .findFirst()
                    .map(this::toOAuth2Authorization)
                    .orElse(null);
        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            return authorizationRepository.findAll().stream()
                    .filter(entity -> token.equals(entity.getState()))
                    .findFirst()
                    .map(this::toOAuth2Authorization)
                    .orElse(null);
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            return authorizationRepository.findAll().stream()
                    .filter(entity -> token.equals(entity.getAuthorizationCodeValue()))
                    .findFirst()
                    .map(this::toOAuth2Authorization)
                    .orElse(null);
        } else if (OAuth2TokenType.ACCESS_TOKEN.getValue().equals(tokenType.getValue())) {
            return authorizationRepository.findAll().stream()
                    .filter(entity -> token.equals(entity.getAccessTokenValue()))
                    .findFirst()
                    .map(this::toOAuth2Authorization)
                    .orElse(null);
        } else if (OAuth2TokenType.REFRESH_TOKEN.getValue().equals(tokenType.getValue())) {
            return authorizationRepository.findAll().stream()
                    .filter(entity -> token.equals(entity.getRefreshTokenValue()))
                    .findFirst()
                    .map(this::toOAuth2Authorization)
                    .orElse(null);
        }
        return null;
    }

    private OAuth2Authorization toOAuth2Authorization(Oauth2Authorization entity) {
        if (entity == null) {
            return null;
        }

        RegisteredClient registeredClient = registeredClientRepository.findById(entity.getRegisteredClientId());
        if (registeredClient == null) {
            return null;
        }

        org.springframework.security.oauth2.core.AuthorizationGrantType grantType;
        try {
            grantType = new org.springframework.security.oauth2.core.AuthorizationGrantType(entity.getAuthorizationGrantType());
        } catch (Exception e) {
            grantType = org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;
        }
        
        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .id(entity.getId())
                .principalName(entity.getPrincipalName())
                .authorizationGrantType(grantType)
                .authorizedScopes(StringUtils.hasText(entity.getAuthorizedScopes())
                        ? Stream.of(entity.getAuthorizedScopes().split(","))
                                .map(String::trim)
                                .collect(Collectors.toSet())
                        : Set.of());

        // 属性
        try {
            if (StringUtils.hasText(entity.getAttributes())) {
                Map<String, Object> attributes = objectMapper.readValue(
                        entity.getAttributes(),
                        new TypeReference<Map<String, Object>>() {});
                attributes.forEach(builder::attribute);
            }
        } catch (Exception e) {
            // 忽略
        }

        // State
        if (StringUtils.hasText(entity.getState())) {
            builder.attribute(OAuth2ParameterNames.STATE, entity.getState());
        }

        // 授权码
        if (StringUtils.hasText(entity.getAuthorizationCodeValue())) {
            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(
                    entity.getAuthorizationCodeValue(),
                    toInstant(entity.getAuthorizationCodeIssuedAt()),
                    toInstant(entity.getAuthorizationCodeExpiresAt()));
            builder.token(authorizationCode);
        }

        // 访问令牌
        if (StringUtils.hasText(entity.getAccessTokenValue())) {
            OAuth2AccessToken.TokenType tokenType = OAuth2AccessToken.TokenType.BEARER;
            if (StringUtils.hasText(entity.getAccessTokenType())) {
                if ("Bearer".equalsIgnoreCase(entity.getAccessTokenType())) {
                    tokenType = OAuth2AccessToken.TokenType.BEARER;
                }
            }
            Set<String> scopes = StringUtils.hasText(entity.getAccessTokenScopes())
                    ? Stream.of(entity.getAccessTokenScopes().split(","))
                            .map(String::trim)
                            .collect(Collectors.toSet())
                    : Set.of();
            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    tokenType,
                    entity.getAccessTokenValue(),
                    toInstant(entity.getAccessTokenIssuedAt()),
                    toInstant(entity.getAccessTokenExpiresAt()),
                    scopes);
            builder.token(accessToken);
        }

        // 刷新令牌
        if (StringUtils.hasText(entity.getRefreshTokenValue())) {
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
                    entity.getRefreshTokenValue(),
                    toInstant(entity.getRefreshTokenIssuedAt()),
                    toInstant(entity.getRefreshTokenExpiresAt()));
            builder.token(refreshToken);
        }

        return builder.build();
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant != null ? instant.atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }

    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atZone(ZoneId.systemDefault()).toInstant() : null;
    }
}
