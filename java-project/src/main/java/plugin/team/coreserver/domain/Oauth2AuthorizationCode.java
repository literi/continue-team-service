package plugin.team.coreserver.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "oauth2_authorization_code")
public class Oauth2AuthorizationCode {

    @Id
    @Column(name = "id", length = 100)
    private String id;

    @Column(name = "tenant_id", length = 64, nullable = false)
    private String tenantId;

    @Column(name = "code", length = 100, nullable = false, unique = true)
    private String code;

    @Column(name = "client_id", length = 100, nullable = false)
    private String clientId;

    @Column(name = "principal_name", length = 200, nullable = false)
    private String principalName;

    @Column(name = "authorization_scopes", length = 1000)
    private String authorizationScopes;

    @Column(name = "authorized_scopes", length = 1000)
    private String authorizedScopes;

    @Column(name = "attributes", length = 4000)
    private String attributes;

    @Column(name = "state", length = 500)
    private String state;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "consumed_at")
    private LocalDateTime consumedAt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getPrincipalName() {
		return principalName;
	}

	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}

	public String getAuthorizationScopes() {
		return authorizationScopes;
	}

	public void setAuthorizationScopes(String authorizationScopes) {
		this.authorizationScopes = authorizationScopes;
	}

	public String getAuthorizedScopes() {
		return authorizedScopes;
	}

	public void setAuthorizedScopes(String authorizedScopes) {
		this.authorizedScopes = authorizedScopes;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public LocalDateTime getConsumedAt() {
		return consumedAt;
	}

	public void setConsumedAt(LocalDateTime consumedAt) {
		this.consumedAt = consumedAt;
	}
    
    
}