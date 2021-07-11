package pcf.crksdev.spquiz.services.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface SpquizPrincipal extends SpquizUser, OidcUser, UserDetails {

    @Override
    default String getFullName() {
        return OidcUser.super.getFullName();
    }

    @Override
    default String getEmail() {
        return OidcUser.super.getEmail();
    }
}
