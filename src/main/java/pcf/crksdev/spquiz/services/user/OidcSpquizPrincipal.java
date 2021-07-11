package pcf.crksdev.spquiz.services.user;

import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

public final class OidcSpquizPrincipal implements SpquizPrincipal {
    private final EmptySpquizPrincipal emptySpquizUser =
        new EmptySpquizPrincipal();

    private final OidcUser user;

    private final Environment env;

    private final String provider;

    private static final String USER_DATA_PROPERTY_TEMPLATE = "pcf.crksdev" +
        ".spquiz" +
        ".services.user.%s.%s";

    public OidcSpquizPrincipal(
        String provider,
        OidcUser user,
        Environment env
    ) {
        this.provider = provider;
        this.user = user;
        this.env = env;
    }

    @Override
    public <A> A getAttribute(String name) {
        return this.user.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.user.getAuthorities();
    }

    @Override
    public String getName() {
        return this.getId();
    }

    @Override
    public String getPassword() {
        return emptySpquizUser.getPassword();
    }

    @Override
    public String getUsername() {
        return this.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return emptySpquizUser.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return emptySpquizUser.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return emptySpquizUser.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return emptySpquizUser.isEnabled();
    }

    private String getAttributeName(final String key) {
        return this.env.getProperty(
            String.format(
                USER_DATA_PROPERTY_TEMPLATE,
                provider,
                key
            )
        );
    }

    @Override
    public Map<String, Object> getClaims() {
        return user.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return user.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return user.getIdToken();
    }

    @Override
    public String getId() {
        return this.user.getAttribute(this.getAttributeName("id"));
    }

    @Override
    public String getFullName() {
        return this.user.getAttribute(this.getAttributeName("full-name"));
    }

    @Override
    public String getEmail() {
        return this.user.getAttribute(this.getAttributeName("email"));
    }

    @Override
    public URI getAvatar() {
        return URI.create(this.user.getAttribute(this.getAttributeName("avatar")));
    }
}
