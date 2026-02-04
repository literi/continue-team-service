package plugin.team.coreserver.repository;

import plugin.team.coreserver.domain.SysTenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysTenantRepository extends JpaRepository<SysTenant, String> {
}