package am.rate.project.bestexchangerate.controller;

import am.rate.project.bestexchangerate.dom.*;
import am.rate.project.bestexchangerate.repo.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    public String request(ModelAndView model) {
        List<String> list = new ArrayList<String>(Arrays.asList("fghj","lkjhgf","lkjhg"));
        model.addObject("list", list);
        return "/request";
    }

    @RequestMapping(value = "/request", method = RequestMethod.POST)
    public String addRequest(ModelAndView model, @AuthenticationPrincipal Client client,
                             @RequestParam String  requestType,
                             @RequestParam String exchangeOption,
                             @RequestParam String currencyType,
                             @RequestParam Float value,
                             @RequestParam String deadline) throws ParseException {
        System.out.println(client.getUsername());

        Request request = new Request(RequestType.valueOf(requestType), ExchangeOption.valueOf(exchangeOption),
                CurrencyType.valueOf(currencyType), value,true, new SimpleDateFormat("yyyy-MM-dd").parse(deadline), client);

        requestRepo.save(request);
        model.setViewName("successful");

        return "request";
    }


}
