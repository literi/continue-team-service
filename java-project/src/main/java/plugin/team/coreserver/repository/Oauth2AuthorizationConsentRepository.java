package plugin.team.coreserver.repository;

import plugin.team.coreserver.domain.Oauth2AuthorizationConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Oauth2AuthorizationConsentRepository extends JpaRepository<Oauth2AuthorizationConsent, String> {
}