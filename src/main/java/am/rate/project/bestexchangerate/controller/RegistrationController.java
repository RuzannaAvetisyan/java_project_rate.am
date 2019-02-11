package am.rate.project.bestexchangerate.controller;

import am.rate.project.bestexchangerate.dom.Client;
import am.rate.project.bestexchangerate.repo.ClientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {
    @Autowired
    private ClientRepo clientRepo;

    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    public String addClient(Client client, Model model){
        Client clientFDB = clientRepo.findByUsername(client.getUsername());
        if(clientFDB != null){
            model.addAttribute("message", "User exists!");
            return "registration";
        }
        client.setActive(true);
        clientRepo.save(client);
        return "redirect:/login";
    }
}
