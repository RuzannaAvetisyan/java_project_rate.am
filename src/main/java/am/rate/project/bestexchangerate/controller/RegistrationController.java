package am.rate.project.bestexchangerate.controller;

import am.rate.project.bestexchangerate.dom.Client;
import am.rate.project.bestexchangerate.repo.ClientRepo;
import am.rate.project.bestexchangerate.service.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.Callable;

@Controller
public class RegistrationController {
    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private MailSender mailSender;

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addClient(Client client, Model model) {
        Client clientFDB = clientRepo.findByUsername(client.getUsername());
        if (clientFDB != null) {
            model.addAttribute("message", "User exists!");
            return "registration";
        }
        UUID uuid = UUID.randomUUID();
        String password = uuid.toString().substring(0, 8).toUpperCase();
        mailSender.send(client.getUsername(), "Activation code", password);
        client.setPassword(password);
        client.setActive(true);
        clientRepo.save(client);
        return "redirect:/login";
    }

    @RequestMapping(value = "/editProfile", method = RequestMethod.POST)
    public String edit(@AuthenticationPrincipal Client client,
                       @RequestParam String password, @RequestParam String confirm_password){
        if(!password.equals(confirm_password))
           return  "editProfile";
        client.setPassword(password);
        clientRepo.save(client);
        return "redirect:/logout";
    }

    @GetMapping("/editProfile")
    public String editProfile(Model model){
        model.addAttribute("message", "An error occurred, try again.");
        return "editProfile";
    }
}
