package pcf.crksdev.spquiz.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pcf.crksdev.spquiz.services.quiz.QuizService;
import pcf.crksdev.spquiz.services.user.SpquizUser;

@Controller
public final class Dashboard {

    private final QuizService quizService;

    public Dashboard(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/user")
    public String user(Model model, @AuthenticationPrincipal SpquizUser principal) {
        var question = quizService.startOrResumeSession().orElseThrow();
        model.addAttribute("question", question);
        model.addAttribute("user", principal);
        return "dashboard";
    }

}
