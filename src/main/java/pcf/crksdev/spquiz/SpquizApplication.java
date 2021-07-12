package pcf.crksdev.spquiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import pcf.crksdev.spquiz.services.user.DefaultSpquizUser;
import pcf.crksdev.spquiz.services.user.UserService;

import java.net.URI;

@SpringBootApplication
public class SpquizApplication {

    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(SpquizApplication.class, args);
    }

    @Bean
    @Profile("dev")
    ApplicationRunner applicationRunner() {
        return (args) -> {
            userService.register(
                new DefaultSpquizUser("criske", "123", "Cristoper", "criske@example.com",
                    URI.create("https://avatars.githubusercontent" +
                        ".com/u/10284893?v=4")
                ));
        };
    }

}
