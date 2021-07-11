package pcf.crksdev.spquiz.services.user.context;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import pcf.crksdev.spquiz.services.user.User;

import java.net.URI;

@Component
final class UserMappers {

    private final Environment env;

    private static final String USER_DATA_PROPERTY_TEMPLATE = "pcf.crksdev" +
        ".spquiz" +
        ".services.user.%s.%s";

    UserMappers(Environment env) {
        this.env = env;
    }

    User fromOAuth2Principal(
        final String provider,
        final OAuth2User principal
    ) {
        return new User(
            this.getAttribute(principal, provider, "id"),
            "",
            this.getAttribute(principal, provider, "full-name"),
            this.getAttribute(principal, provider, "email"),
            URI.create(this.getAttribute(principal, provider, "avatar"))
        );
    }

    User fromAuthentication(Authentication authentication) {
        final User user;
        if (authentication instanceof OAuth2AuthenticationToken) {
            var oAuthentication = (OAuth2AuthenticationToken) authentication;
            user = this.fromOAuth2Principal(
                oAuthentication.getAuthorizedClientRegistrationId(),
                oAuthentication.getPrincipal()
            );
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
            var uPassAuthentication =
                (UsernamePasswordAuthenticationToken) authentication;
            var principal = uPassAuthentication.getPrincipal();
            if (principal instanceof SpQuizUserDetails) {
                user = ((SpQuizUserDetails) principal).getUser();
            } else {
                throw new UsernameNotFoundException("Unsupported " +
                    "authentication "
                    + authentication.getClass().getSimpleName());
            }
        } else {
            throw new UsernameNotFoundException("Unsupported " +
                "authentication "
                + authentication.getClass().getSimpleName());
        }
        return user;
    }

    private String getAttribute(
        final OAuth2User principal,
        final String provider,
        final String key
    ) {
        String attribute = this.env.getProperty(
            String.format(
                USER_DATA_PROPERTY_TEMPLATE,
                provider,
                key
            )
        );
        return principal.getAttribute(attribute);
    }
}
