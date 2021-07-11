package pcf.crksdev.spquiz.services.user;

import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public final class SpquizOAuth2UserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcUserService delegate;

    private final Environment environment;

    public SpquizOAuth2UserService(Environment environment) {
        this.environment = environment;
        this.delegate = new OidcUserService();
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest)
        throws OAuth2AuthenticationException {
        final String provider =
            userRequest.getClientRegistration().getRegistrationId();
        final OidcUser oAuth2User = this.delegate.loadUser(userRequest);
        return new OidcSpquizPrincipal(provider, oAuth2User,
            this.environment
        );
    }
}
