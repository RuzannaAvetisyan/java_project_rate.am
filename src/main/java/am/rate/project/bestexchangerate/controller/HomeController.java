package am.rate.project.bestexchangerate.controller;

import am.rate.project.bestexchangerate.dom.Table;
import am.rate.project.bestexchangerate.repo.TableRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class HomeController {

    private final TableRepo tableRepo;

    @Autowired
    public HomeController(TableRepo tableRepo) {
        this.tableRepo = tableRepo;
    }

    @GetMapping("/")
    public String index( Model model) {
        return "redirect:home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        Table table = tableRepo.findByDate(LocalDate.now());
        System.out.println(table);
        model.addAttribute("table",tableRepo.findByDate(LocalDate.now()));
        return "home";
    }


}
