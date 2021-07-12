package pcf.crksdev.spquiz;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

@Configuration
public class SpquizWebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatterForFieldType(URI.class, new Formatter<URI>() {
            @NotNull
            @Override
            public URI parse(@NotNull String text, @NotNull Locale locale)
                throws ParseException {
                return URI.create(text);
            }

            @NotNull
            @Override
            public String print(@NotNull URI object, @NotNull Locale locale) {
                return object.toString();
            }
        });
    }
}
