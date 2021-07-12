package pcf.crksdev.spquiz.services.user;

import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class FormLoginSpquizPrincipal implements SpquizPrincipal {

    private final EmptySpquizPrincipal emptySpquizUser =
        new EmptySpquizPrincipal();

    private final SpquizUser user;

    public FormLoginSpquizPrincipal(SpquizUser user) {
        this.user = user;
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return emptySpquizUser.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return this.user.getId();
    }

    @Override
    public String getId() {
        return this.user.getId();
    }

    @Override
    public String getFullName() {
        return this.user.getFullName();
    }

    @Override
    public String getEmail() {
        return this.user.getEmail();
    }

    @Override
    public URI getAvatar() {
        return this.user.getAvatar();
    }

    @Override
    @Nullable
    public <A> A getAttribute(String name) {
        return this.emptySpquizUser.getAttribute(name);
    }

    @Override
    public Map<String, Object> getClaims() {
        return this.emptySpquizUser.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return this.emptySpquizUser.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return this.emptySpquizUser.getIdToken();
    }
}
