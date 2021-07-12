package pcf.crksdev.spquiz;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filter that redirects to /user when one of provided endpoints is matched.
 */
public final class RedirectToUserWhenAuthenticatedFilter extends OncePerRequestFilter {

    /**
     * Endpoints that will cause to redirect to /user in a authenticated
     * session.
     */
    private final List<String> endpoints;

    public RedirectToUserWhenAuthenticatedFilter(String... endpoints) {
        this.endpoints = Arrays.asList(endpoints);
    }

    @Override
    protected void doFilterInternal(
        @NotNull HttpServletRequest request,
        @NotNull HttpServletResponse response,
        @NotNull FilterChain filterChain
    )
        throws ServletException, IOException {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = authentication != null
            && !AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())
            && authentication.isAuthenticated();
        if (authenticated) {
            boolean redirectToUser = this.endpoints.stream()
                .anyMatch(p -> p.equalsIgnoreCase(urlPathHelper.getLookupPathForRequest(request)));
            if (redirectToUser) {
                response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
                response.setHeader("Location", "/user");
            } else {
                filterChain.doFilter(request, response);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
