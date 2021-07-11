package pcf.crksdev.spquiz.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public final class Index {

    @GetMapping("/")
    public String index(Principal principal) {
        final String page;
        if (principal != null) {
            page = "redirect:/user";
        } else {
            page = "index";
        }
        return page;
    }
}
