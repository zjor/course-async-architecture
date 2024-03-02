package aa.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Entity
@Table(name = "auth_users")
public class User {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @CreationTimestamp
    @Column(name = "client_id_issued_at", nullable = false)
    private Instant clientIdIssuedAt;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "client_secret_expires_at")
    private Instant clientSecretExpiresAt;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "client_authentication_methods", length = 1024, nullable = false)
    private String clientAuthenticationMethods;

    @Column(name = "authorization_grant_types", length = 1024, nullable = false)
    private String authorizationGrantTypes;

    @Column(name = "redirect_uris", length = 4096)
    private String redirectUris;

    @Column(name = "post_logout_redirect_uris", length = 4096)
    private String postLogoutRedirectUris;

    @Column(name = "scopes", length = 1024, nullable = false)
    private String scopes;
}