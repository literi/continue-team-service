package plugin.team.coreserver.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "oauth2_authorization_consent")
public class Oauth2AuthorizationConsent {

    @Id
    @Column(name = "id", length = 100)
    private String id;

    @Column(name = "registered_client_id", length = 100, nullable = false)
    private String registeredClientId;

    @Column(name = "principal_name", length = 200, nullable = false)
    private String principalName;

    @Column(name = "authorities", length = 1000, nullable = false)
    private String authorities;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRegisteredClientId() {
		return registeredClientId;
	}

	public void setRegisteredClientId(String registeredClientId) {
		this.registeredClientId = registeredClientId;
	}

	public String getPrincipalName() {
		return principalName;
	}

	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}

	public String getAuthorities() {
		return authorities;
	}

	public void setAuthorities(String authorities) {
		this.authorities = authorities;
	}
    
    
    
}