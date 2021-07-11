package pcf.crksdev.spquiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import pcf.crksdev.spquiz.services.user.context.PersistingOidcAuthorizationCodeAuthenticationProvider;
import pcf.crksdev.spquiz.services.user.context.SpQuizUserDetailsProvider;

@Configuration
@EnableWebSecurity
class SpquizSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private PersistingOidcAuthorizationCodeAuthenticationProvider authenticationProvider;

    @Autowired
    private SpQuizUserDetailsProvider spQuizUserDetailsProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
        throws Exception {
        auth
            .authenticationProvider(authenticationProvider)
            .authenticationProvider(spQuizUserDetailsProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/static/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf();

        http.authorizeRequests()
            .mvcMatchers("/user/**").authenticated()
            .mvcMatchers("/", "/login**").permitAll()
            .and()
            .logout()
            .logoutSuccessUrl("/")
            .and()
            .formLogin()
            .loginPage("/")
            .and()
            .oauth2Login();

    }

    @Bean
    OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> tokenResponseClient() {
        return new DefaultAuthorizationCodeTokenResponseClient();
    }

    @Bean
    OAuth2UserService<OidcUserRequest, OidcUser> oAuth2UserService() {
        return new OidcUserService();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
