package plugin.team.coreserver.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "oauth2_authorization")
public class Oauth2Authorization {

    @Id
    @Column(name = "id", length = 100)
    private String id;

    @Column(name = "tenant_id", length = 64, nullable = false)
    private String tenantId;

    @Column(name = "registered_client_id", length = 100, nullable = false)
    private String registeredClientId;

    @Column(name = "principal_name", length = 200, nullable = false)
    private String principalName;

    @Column(name = "authorization_grant_type", length = 100, nullable = false)
    private String authorizationGrantType;

    @Column(name = "authorized_scopes", length = 1000)
    private String authorizedScopes;

    @Column(name = "attributes", length = 4000)
    private String attributes;

    @Column(name = "state", length = 500)
    private String state;

    @Column(name = "authorization_code_value", length = 4000)
    private String authorizationCodeValue;

    @Column(name = "authorization_code_issued_at")
    private LocalDateTime authorizationCodeIssuedAt;

    @Column(name = "authorization_code_expires_at")
    private LocalDateTime authorizationCodeExpiresAt;

    @Column(name = "authorization_code_metadata", length = 2000)
    private String authorizationCodeMetadata;

    @Column(name = "access_token_value", length = 4000)
    private String accessTokenValue;

    @Column(name = "access_token_issued_at")
    private LocalDateTime accessTokenIssuedAt;

    @Column(name = "access_token_expires_at")
    private LocalDateTime accessTokenExpiresAt;

    @Column(name = "access_token_metadata", length = 2000)
    private String accessTokenMetadata;

    @Column(name = "access_token_type", length = 100)
    private String accessTokenType;

    @Column(name = "access_token_scopes", length = 1000)
    private String accessTokenScopes;

    @Column(name = "refresh_token_value", length = 4000)
    private String refreshTokenValue;

    @Column(name = "refresh_token_issued_at")
    private LocalDateTime refreshTokenIssuedAt;

    @Column(name = "refresh_token_expires_at")
    private LocalDateTime refreshTokenExpiresAt;

    @Column(name = "refresh_token_metadata", length = 2000)
    private String refreshTokenMetadata;

    @Column(name = "oidc_id_token_value", length = 4000)
    private String oidcIdTokenValue;

    @Column(name = "oidc_id_token_issued_at")
    private LocalDateTime oidcIdTokenIssuedAt;

    @Column(name = "oidc_id_token_expires_at")
    private LocalDateTime oidcIdTokenExpiresAt;

    @Column(name = "oidc_id_token_metadata", length = 2000)
    private String oidcIdTokenMetadata;

    @Column(name = "oidc_id_token_claims", length = 2000)
    private String oidcIdTokenClaims;

    @Column(name = "user_code_value", length = 4000)
    private String userCodeValue;

    @Column(name = "user_code_issued_at")
    private LocalDateTime userCodeIssuedAt;

    @Column(name = "user_code_expires_at")
    private LocalDateTime userCodeExpiresAt;

    @Column(name = "user_code_metadata", length = 2000)
    private String userCodeMetadata;

    @Column(name = "device_code_value", length = 4000)
    private String deviceCodeValue;

    @Column(name = "device_code_issued_at")
    private LocalDateTime deviceCodeIssuedAt;

    @Column(name = "device_code_expires_at")
    private LocalDateTime deviceCodeExpiresAt;

    @Column(name = "device_code_metadata", length = 2000)
    private String deviceCodeMetadata;

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

	public String getAuthorizationGrantType() {
		return authorizationGrantType;
	}

	public void setAuthorizationGrantType(String authorizationGrantType) {
		this.authorizationGrantType = authorizationGrantType;
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

	public String getAuthorizationCodeValue() {
		return authorizationCodeValue;
	}

	public void setAuthorizationCodeValue(String authorizationCodeValue) {
		this.authorizationCodeValue = authorizationCodeValue;
	}

	public LocalDateTime getAuthorizationCodeIssuedAt() {
		return authorizationCodeIssuedAt;
	}

	public void setAuthorizationCodeIssuedAt(LocalDateTime authorizationCodeIssuedAt) {
		this.authorizationCodeIssuedAt = authorizationCodeIssuedAt;
	}

	public LocalDateTime getAuthorizationCodeExpiresAt() {
		return authorizationCodeExpiresAt;
	}

	public void setAuthorizationCodeExpiresAt(LocalDateTime authorizationCodeExpiresAt) {
		this.authorizationCodeExpiresAt = authorizationCodeExpiresAt;
	}

	public String getAuthorizationCodeMetadata() {
		return authorizationCodeMetadata;
	}

	public void setAuthorizationCodeMetadata(String authorizationCodeMetadata) {
		this.authorizationCodeMetadata = authorizationCodeMetadata;
	}

	public String getAccessTokenValue() {
		return accessTokenValue;
	}

	public void setAccessTokenValue(String accessTokenValue) {
		this.accessTokenValue = accessTokenValue;
	}

	public LocalDateTime getAccessTokenIssuedAt() {
		return accessTokenIssuedAt;
	}

	public void setAccessTokenIssuedAt(LocalDateTime accessTokenIssuedAt) {
		this.accessTokenIssuedAt = accessTokenIssuedAt;
	}

	public LocalDateTime getAccessTokenExpiresAt() {
		return accessTokenExpiresAt;
	}

	public void setAccessTokenExpiresAt(LocalDateTime accessTokenExpiresAt) {
		this.accessTokenExpiresAt = accessTokenExpiresAt;
	}

	public String getAccessTokenMetadata() {
		return accessTokenMetadata;
	}

	public void setAccessTokenMetadata(String accessTokenMetadata) {
		this.accessTokenMetadata = accessTokenMetadata;
	}

	public String getAccessTokenType() {
		return accessTokenType;
	}

	public void setAccessTokenType(String accessTokenType) {
		this.accessTokenType = accessTokenType;
	}

	public String getAccessTokenScopes() {
		return accessTokenScopes;
	}

	public void setAccessTokenScopes(String accessTokenScopes) {
		this.accessTokenScopes = accessTokenScopes;
	}

	public String getRefreshTokenValue() {
		return refreshTokenValue;
	}

	public void setRefreshTokenValue(String refreshTokenValue) {
		this.refreshTokenValue = refreshTokenValue;
	}

	public LocalDateTime getRefreshTokenIssuedAt() {
		return refreshTokenIssuedAt;
	}

	public void setRefreshTokenIssuedAt(LocalDateTime refreshTokenIssuedAt) {
		this.refreshTokenIssuedAt = refreshTokenIssuedAt;
	}

	public LocalDateTime getRefreshTokenExpiresAt() {
		return refreshTokenExpiresAt;
	}

	public void setRefreshTokenExpiresAt(LocalDateTime refreshTokenExpiresAt) {
		this.refreshTokenExpiresAt = refreshTokenExpiresAt;
	}

	public String getRefreshTokenMetadata() {
		return refreshTokenMetadata;
	}

	public void setRefreshTokenMetadata(String refreshTokenMetadata) {
		this.refreshTokenMetadata = refreshTokenMetadata;
	}

	public String getOidcIdTokenValue() {
		return oidcIdTokenValue;
	}

	public void setOidcIdTokenValue(String oidcIdTokenValue) {
		this.oidcIdTokenValue = oidcIdTokenValue;
	}

	public LocalDateTime getOidcIdTokenIssuedAt() {
		return oidcIdTokenIssuedAt;
	}

	public void setOidcIdTokenIssuedAt(LocalDateTime oidcIdTokenIssuedAt) {
		this.oidcIdTokenIssuedAt = oidcIdTokenIssuedAt;
	}

	public LocalDateTime getOidcIdTokenExpiresAt() {
		return oidcIdTokenExpiresAt;
	}

	public void setOidcIdTokenExpiresAt(LocalDateTime oidcIdTokenExpiresAt) {
		this.oidcIdTokenExpiresAt = oidcIdTokenExpiresAt;
	}

	public String getOidcIdTokenMetadata() {
		return oidcIdTokenMetadata;
	}

	public void setOidcIdTokenMetadata(String oidcIdTokenMetadata) {
		this.oidcIdTokenMetadata = oidcIdTokenMetadata;
	}

	public String getOidcIdTokenClaims() {
		return oidcIdTokenClaims;
	}

	public void setOidcIdTokenClaims(String oidcIdTokenClaims) {
		this.oidcIdTokenClaims = oidcIdTokenClaims;
	}

	public String getUserCodeValue() {
		return userCodeValue;
	}

	public void setUserCodeValue(String userCodeValue) {
		this.userCodeValue = userCodeValue;
	}

	public LocalDateTime getUserCodeIssuedAt() {
		return userCodeIssuedAt;
	}

	public void setUserCodeIssuedAt(LocalDateTime userCodeIssuedAt) {
		this.userCodeIssuedAt = userCodeIssuedAt;
	}

	public LocalDateTime getUserCodeExpiresAt() {
		return userCodeExpiresAt;
	}

	public void setUserCodeExpiresAt(LocalDateTime userCodeExpiresAt) {
		this.userCodeExpiresAt = userCodeExpiresAt;
	}

	public String getUserCodeMetadata() {
		return userCodeMetadata;
	}

	public void setUserCodeMetadata(String userCodeMetadata) {
		this.userCodeMetadata = userCodeMetadata;
	}

	public String getDeviceCodeValue() {
		return deviceCodeValue;
	}

	public void setDeviceCodeValue(String deviceCodeValue) {
		this.deviceCodeValue = deviceCodeValue;
	}

	public LocalDateTime getDeviceCodeIssuedAt() {
		return deviceCodeIssuedAt;
	}

	public void setDeviceCodeIssuedAt(LocalDateTime deviceCodeIssuedAt) {
		this.deviceCodeIssuedAt = deviceCodeIssuedAt;
	}

	public LocalDateTime getDeviceCodeExpiresAt() {
		return deviceCodeExpiresAt;
	}

	public void setDeviceCodeExpiresAt(LocalDateTime deviceCodeExpiresAt) {
		this.deviceCodeExpiresAt = deviceCodeExpiresAt;
	}

	public String getDeviceCodeMetadata() {
		return deviceCodeMetadata;
	}

	public void setDeviceCodeMetadata(String deviceCodeMetadata) {
		this.deviceCodeMetadata = deviceCodeMetadata;
	}
    
    
    
    
    
}