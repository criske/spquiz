package pcf.crksdev.spquiz.services.user.context;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pcf.crksdev.spquiz.services.user.User;

import java.util.Collection;
import java.util.List;

/**
 * Used in User/Password authentication flow.
 */
public final class SpQuizUserDetails implements UserDetails {

    /**
     * Wrapped principal.
     */
    private final User user;

    public SpQuizUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "player");
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getId();
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

    public User getUser() {
        return user;
    }
}
