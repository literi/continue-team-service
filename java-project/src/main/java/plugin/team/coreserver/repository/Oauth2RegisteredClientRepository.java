package plugin.team.coreserver.repository;

import plugin.team.coreserver.domain.Oauth2RegisteredClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Oauth2RegisteredClientRepository extends JpaRepository<Oauth2RegisteredClient, String> {

    Optional<Oauth2RegisteredClient> findByClientId(String clientId);
}