package pcf.crksdev.spquiz.services.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import pcf.crksdev.spquiz.services.user.FormLoginSpquizPrincipal;
import pcf.crksdev.spquiz.services.user.SpquizPrincipal;
import pcf.crksdev.spquiz.services.user.UserService;

@Component
public final class SpQuizUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public SpQuizUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException {
        return userService
            .getById(username)
            .map(FormLoginSpquizPrincipal::new)
            .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
