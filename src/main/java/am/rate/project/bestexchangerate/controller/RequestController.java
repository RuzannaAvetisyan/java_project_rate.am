package am.rate.project.bestexchangerate.controller;

import am.rate.project.bestexchangerate.dom.Client;
import am.rate.project.bestexchangerate.dom.ExchangeOption;
import am.rate.project.bestexchangerate.dom.Request;
import am.rate.project.bestexchangerate.dom.RequestType;
import am.rate.project.bestexchangerate.repo.CurrencyRepo;
import am.rate.project.bestexchangerate.repo.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Locale;

@Controller
public class RequestController {

    private final RequestRepo requestRepo;
    private final CurrencyRepo currencyRepo;

    @Autowired
    public RequestController(RequestRepo requestRepo, CurrencyRepo currencyRepo) {
        this.requestRepo = requestRepo;
        this.currencyRepo = currencyRepo;
    }


    @GetMapping("/request")
    public String request(Model model) {
        LocalDateTime today = LocalDateTime.now();
        model.addAttribute(new Request());
        model.addAttribute("requestTypes", RequestType.values());
        model.addAttribute("exchangeOptions", ExchangeOption.values());
        model.addAttribute("currencies", currencyRepo.findAll());
        model.addAttribute("min",
                DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.UK).format(today));
        model.addAttribute("nextWeek",
                DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.UK)
                        .format(today.plus(1, ChronoUnit.WEEKS)));

        return "request";
    }

    @PostMapping("/request")
    public String addRequest(ModelAndView model, @AuthenticationPrincipal Client client,
                             @ModelAttribute Request request,
                             @RequestParam String currency){
        if(request == null || client == null || currency == null)
            return "request";
        request.setClient(client);
        request.setCurrency(currencyRepo.findByCurrencyType(currency));
        requestRepo.save(request);

        return "redirect:/home";
    }

    @GetMapping("/requests")
    private String requests(Model model, @AuthenticationPrincipal Client client){
        model.addAttribute(new Request());
        model.addAttribute("requests",requestRepo.findByActiveTrueAndDeadlineAfterAndClient(LocalDate.now().minusDays(1),client ));
        return "requests";
    }

}
