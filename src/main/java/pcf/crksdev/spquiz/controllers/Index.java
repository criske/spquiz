package pcf.crksdev.spquiz.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public final class Index {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
