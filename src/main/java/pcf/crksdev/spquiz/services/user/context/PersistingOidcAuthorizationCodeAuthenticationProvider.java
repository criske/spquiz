package pcf.crksdev.spquiz.services.user.context;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import pcf.crksdev.spquiz.services.user.User;
import pcf.crksdev.spquiz.services.user.UserService;

@Component
public final class PersistingOidcAuthorizationCodeAuthenticationProvider
    extends OidcAuthorizationCodeAuthenticationProvider {

    private final UserService spQuizUserService;

    private final UserMappers userMappers;

    /**
     * Constructs an {@code OidcAuthorizationCodeAuthenticationProvider}
     * using the
     * provided parameters.
     *
     * @param accessTokenResponseClient the client used for requesting the
     * access token
     * credential from the Token Endpoint
     * @param userService the service used for obtaining the user attributes
     * of the
     * @param spQuizUserService Springing Quiz user service;
     * @param userMappers Usr factories.
     */
    public PersistingOidcAuthorizationCodeAuthenticationProvider(
        OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient,
        OAuth2UserService<OidcUserRequest, OidcUser> userService,
        UserService spQuizUserService,
        UserMappers userMappers
    ) {
        super(accessTokenResponseClient, userService);
        this.spQuizUserService = spQuizUserService;
        this.userMappers = userMappers;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        final Authentication authenticate = super.authenticate(authentication);
        final OAuth2LoginAuthenticationToken token =
            (OAuth2LoginAuthenticationToken) authenticate;
        final User user = this.userMappers.fromOAuth2Principal(
            token.getClientRegistration().getRegistrationId(),
            token.getPrincipal()
        );
        this.spQuizUserService.register(user);
        return authenticate;
    }
}
