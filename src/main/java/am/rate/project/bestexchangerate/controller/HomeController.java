package am.rate.project.bestexchangerate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String greeting(@RequestParam(defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "home";
    }
    @GetMapping("/request")
    public String request(@RequestParam(defaultValue="World") String name, Model model) {

        return "request";
    }


}
