package pcf.crksdev.spquiz.services.user.context;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public final class SpQuizUserDetailsProvider extends AbstractUserDetailsAuthenticationProvider {

    private final SpQuizUserDetailsService service;

    public SpQuizUserDetailsProvider(SpQuizUserDetailsService service) {
        this.service = service;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(
        String username,
        UsernamePasswordAuthenticationToken authentication
    )
        throws AuthenticationException {
        return service.loadUserByUsername(username);
    }
}
