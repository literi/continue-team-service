package plugin.team.coreserver.repository;

import plugin.team.coreserver.domain.Oauth2AuthorizationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Oauth2AuthorizationCodeRepository extends JpaRepository<Oauth2AuthorizationCode, String> {
}