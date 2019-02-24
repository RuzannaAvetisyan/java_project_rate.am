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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
    public String addRequest(Model model, @AuthenticationPrincipal Client client, @ModelAttribute Request request, @RequestParam String currency, RedirectAttributes redirectAttrs){
        if(request == null || client == null || currency == null){
            model.addAttribute("message", "Something went wrong!");
            return "request";
        }
        List<Request> like = requestRepo.findByActiveTrueAndDeadlineAfterAndExchangeOptionAndClientAndCurrencyAndValueAndRequestType(LocalDate.now(), request.getExchangeOption(), client,currencyRepo.findByCurrencyType(currency), request.getValue(), request.getRequestType());
        if(like.isEmpty()){
            request.setClient(client);
            request.setCurrency(currencyRepo.findByCurrencyType(currency));
            requestRepo.save(request);
        }else {
            redirectAttrs.addFlashAttribute("warning", "You already have this request.");
        }
        return "redirect:/requests";
    }

    @GetMapping("/requests")
    public String requests(Model model, @AuthenticationPrincipal Client client){
        List<Request> requestList = requestRepo.findByActiveTrueAndDeadlineAfterAndClient(LocalDate.now().minusDays(1), client);
        if(!requestList.isEmpty()){
            model.addAttribute(new Request());
            model.addAttribute("requests", requestList);
        }else {
            model.addAttribute("message", "You have no requests.");
        }
        return "requests";
    }

    @RequestMapping("/requests/delete/{id}")
    public String delete(RedirectAttributes redirectAttrs, @PathVariable Long id){
        if(id != null && requestRepo.findById(id).isPresent()){
            Request request = requestRepo.findById(id).get();
            request.setActive(false);
            requestRepo.save(request);
        }else {
            redirectAttrs.addFlashAttribute("message", "Something went wrong!");
        }
        return "redirect:/requests";
    }
}
