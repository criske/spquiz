package pcf.crksdev.spquiz.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pcf.crksdev.spquiz.services.quiz.QuizService;
import pcf.crksdev.spquiz.services.user.User;
import pcf.crksdev.spquiz.services.user.context.AuthenticatedUser;

@Controller
public final class Dashboard {

    @AuthenticatedUser
    private final User user;

    private QuizService quizService;

    public Dashboard(User user, QuizService quizService) {
        this.user = user;
        this.quizService = quizService;
    }

    @GetMapping("/user")
    public String user(Model model) {
        model.addAttribute("user", user);

        // model.addAttribute("user",user);
        var question = quizService.startOrResumeSession().orElseThrow();
        model.addAttribute("question", question);
        return "dashboard";
    }

}
