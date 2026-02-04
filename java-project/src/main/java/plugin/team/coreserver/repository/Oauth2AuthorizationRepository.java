package plugin.team.coreserver.repository;

import plugin.team.coreserver.domain.Oauth2Authorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Oauth2AuthorizationRepository extends JpaRepository<Oauth2Authorization, String> {
}