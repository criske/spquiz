package pcf.crksdev.spquiz.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import pcf.crksdev.spquiz.services.quiz.QuizService;
import pcf.crksdev.spquiz.services.user.SpquizUser;

@Controller
public final class Dashboard {

    private final QuizService quizService;

    public Dashboard(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping ("/user")
    public String userSuccess(Model model, @AuthenticationPrincipal SpquizUser principal) {
        model.addAttribute("user", principal);

        // model.addAttribute("user",user);
        var question = quizService.startOrResumeSession().orElseThrow();
        model.addAttribute("question", question);
        return "dashboard";
    }

    @GetMapping("/user")
    public String user(Model model, @AuthenticationPrincipal SpquizUser principal) {
        model.addAttribute("user", principal);

        // model.addAttribute("user",user);
        var question = quizService.startOrResumeSession().orElseThrow();
        model.addAttribute("question", question);
        return "dashboard";
    }

}
