package plugin.team.coreserver.repository;

import plugin.team.coreserver.domain.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, String> {

    SysUser findByUsername(String username);
}