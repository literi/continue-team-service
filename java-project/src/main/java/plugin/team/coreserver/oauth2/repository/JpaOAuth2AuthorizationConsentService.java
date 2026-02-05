package plugin.team.coreserver.oauth2.repository;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import plugin.team.coreserver.domain.Oauth2AuthorizationConsent;
import plugin.team.coreserver.repository.Oauth2AuthorizationConsentRepository;

/**
 * JPA 实现的 OAuth2AuthorizationConsentService
 */
@Component
public class JpaOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {

    private final Oauth2AuthorizationConsentRepository consentRepository;

    public JpaOAuth2AuthorizationConsentService(Oauth2AuthorizationConsentRepository consentRepository) {
        this.consentRepository = consentRepository;
    }

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        String id = authorizationConsent.getRegisteredClientId() + ":" + authorizationConsent.getPrincipalName();
        
        Oauth2AuthorizationConsent entity = consentRepository.findById(id)
                .orElse(new Oauth2AuthorizationConsent());
        
        entity.setId(id);
        entity.setRegisteredClientId(authorizationConsent.getRegisteredClientId());
        entity.setPrincipalName(authorizationConsent.getPrincipalName());
        entity.setAuthorities(String.join(",", authorizationConsent.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet())));
        
        consentRepository.save(entity);
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        String id = authorizationConsent.getRegisteredClientId() + ":" + authorizationConsent.getPrincipalName();
        consentRepository.deleteById(id);
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        String id = registeredClientId + ":" + principalName;
        return consentRepository.findById(id)
                .map(this::toOAuth2AuthorizationConsent)
                .orElse(null);
    }

    private OAuth2AuthorizationConsent toOAuth2AuthorizationConsent(Oauth2AuthorizationConsent entity) {
        if (entity == null) {
            return null;
        }

        Set<GrantedAuthority> authorities = StringUtils.hasText(entity.getAuthorities())
                ? Stream.of(entity.getAuthorities().split(","))
                        .map(String::trim)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet())
                : Set.of();

        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(
                entity.getRegisteredClientId(), entity.getPrincipalName());
        authorities.forEach(builder::authority);
        return builder.build();
    }
}
