package pcf.crksdev.spquiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import pcf.crksdev.spquiz.services.user.SpQuizUserDetailsService;
import pcf.crksdev.spquiz.services.user.SpquizOAuth2UserService;
import pcf.crksdev.spquiz.services.user.UserService;

@Configuration
@EnableWebSecurity
class SpquizSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment environment;

    @Autowired
    private UserService userService;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().mvcMatchers("/static/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
        throws Exception {
        auth.userDetailsService(new SpQuizUserDetailsService(userService));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf();

        http.authorizeRequests()
            .mvcMatchers("/user/**").authenticated()
            .mvcMatchers("/", "/login").permitAll()
            .and()
            .logout()
            .logoutSuccessUrl("/")
            .and()
            .formLogin()
            .loginPage("/login")
            .and()
            .oauth2Login()
            .userInfoEndpoint(config -> config
                .oidcUserService(new SpquizOAuth2UserService(environment))
            )
            .and()
            .addFilterAfter(new RedirectToUserWhenAuthenticatedFilter("/", "/login"),
                LogoutFilter.class);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
