package pcf.crksdev.spquiz;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootTest(
    properties = {"QUIZ_API=api_key_123"}
)
class SpquizApplicationTests {

    @Test
    void contextLoads() {
    }

}
