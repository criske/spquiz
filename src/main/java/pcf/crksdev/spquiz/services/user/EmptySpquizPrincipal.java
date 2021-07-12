package pcf.crksdev.spquiz.services.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

public final class EmptySpquizPrincipal implements SpquizPrincipal {

    @Override
    public String getPassword() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUsername() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAccountNonExpired() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAccountNonLocked() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getAttributes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFullName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getEmail() {
        throw new UnsupportedOperationException();
    }

    @Override
    public URI getAvatar() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getClaims() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OidcIdToken getIdToken() {
        throw new UnsupportedOperationException();
    }
}
