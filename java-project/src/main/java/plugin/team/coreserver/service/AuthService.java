package plugin.team.coreserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import plugin.team.coreserver.domain.entity.User;
import plugin.team.coreserver.domain.repository.UserRepository;
import plugin.team.coreserver.infrastructure.security.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public String authenticate(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            log.warn("User not found: {}", username);
            return null;
        }
        
        User user = userOptional.get();
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Invalid password for user: {}", username);
            return null;
        }
        
        if (!user.getStatus().equals(1)) { // 1-启用，0-禁用
            log.warn("User account is not active: {}", username);
            return null;
        }
        
        return tokenProvider.generateToken(user.getId(), user.getUsername(), user.getTenantId(), "USER");
    }
    
    public void registerUser(Long tenantId, String username, String password, String email, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        
        User user = new User();
        user.setTenantId(tenantId);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setStatus(1); // 1-启用，0-禁用
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userRepository.save(user);
        log.info("User registered: {}", username);
    }
}
