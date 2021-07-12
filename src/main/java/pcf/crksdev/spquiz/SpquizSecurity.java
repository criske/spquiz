package pcf.crksdev.spquiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import pcf.crksdev.spquiz.services.user.SpQuizUserDetailsService;
import pcf.crksdev.spquiz.services.user.SpquizOAuth2UserService;
import pcf.crksdev.spquiz.services.user.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

@Configuration
@EnableWebSecurity
class SpquizSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment environment;

    @Autowired
    private UserService userService;


    private final AuthenticationSuccessHandler successHandler = (
        request,
        response, authentication
    ) -> {
        class MutatedMethodHttpServletRequest extends HttpServletRequestWrapper {
            public MutatedMethodHttpServletRequest(HttpServletRequest request) {
                super(request);
            }

            @Override
            public String getMethod() {
                return HttpMethod.GET.name();
            }
        }
        request.getRequestDispatcher("/user")
            .forward(new MutatedMethodHttpServletRequest(request), response);
    };

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
            .successHandler(successHandler)
            .loginPage("/")
            .and()
            .oauth2Login()
            .successHandler(successHandler)
            .userInfoEndpoint(config -> config
                .oidcUserService(new SpquizOAuth2UserService(environment))
            );

    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}
