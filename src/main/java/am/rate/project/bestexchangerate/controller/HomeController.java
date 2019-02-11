package am.rate.project.bestexchangerate.controller;

import am.rate.project.bestexchangerate.dom.*;
import am.rate.project.bestexchangerate.repo.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class HomeController {

    @Autowired
    private RequestRepo requestRepo;

    @GetMapping("/home")
    public String greeting(@RequestParam(defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "home";
    }

    @GetMapping("/request")
    public String request( Model model) {
        return "request";
    }

    @PostMapping("/request")
    public String addRequest(Model model, @AuthenticationPrincipal Client client,
                             @RequestBody RequestType requestType,
                             @RequestBody ExchangeOption exchangeOption,
                             @RequestBody CurrencyType currencyType,
                             @RequestParam Float value,
                             @RequestParam Date deadline){
        Request request = new Request(requestType, exchangeOption, currencyType, value,true, deadline, client);

        requestRepo.save(request);

        return "redirect:/home";
    }


}
