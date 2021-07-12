package pcf.crksdev.spquiz;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import pcf.crksdev.spquiz.services.user.DefaultSpquizUser;
import pcf.crksdev.spquiz.services.user.UserService;

import java.net.URI;

@SpringBootApplication
public class SpquizApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpquizApplication.class, args);
    }

    @Bean
    @Profile("dev")
    ApplicationRunner applicationRunner(
        UserService userService,
        PasswordEncoder passwordEncoder
    ) {
        return (args) -> {
            userService.register(
                new DefaultSpquizUser("criske", passwordEncoder.encode("123")
                    , "Cristoper", "criske@example.com",
                    URI.create("https://avatars.githubusercontent" +
                        ".com/u/10284893?v=4")
                ));
        };
    }

}
