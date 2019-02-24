package am.rate.project.bestexchangerate.controller;

import am.rate.project.bestexchangerate.dom.Client;
import am.rate.project.bestexchangerate.repo.ClientRepo;
import am.rate.project.bestexchangerate.service.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class RegistrationController {
    private final ClientRepo clientRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailSender mailSender;

    @Autowired
    public RegistrationController(ClientRepo clientRepo, MailSender mailSender, PasswordEncoder passwordEncoder) {
        this.clientRepo = clientRepo;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

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
        registrationHelper(client);
        return "redirect:/login";
    }

    @RequestMapping(value = "/editProfile", method = RequestMethod.POST)
    public String edit(@AuthenticationPrincipal Client client,
                       @RequestParam String password, @RequestParam String confirm_password, Model model){
        if(!password.equals(confirm_password)){
            model.addAttribute("message", "An error occurred, try again.");
            return  "editProfile";
        }
        client.setPassword(passwordEncoder.encode(password));
        client.setActive(true);
        clientRepo.save(client);
        return "redirect:/logout";
    }

    @GetMapping("/editProfile")
    public String editProfile(Model model){
        return "editProfile";
    }

    @GetMapping("/restore_password")
    public String restorePasswordGet(Model model){
        return "restore_password";
    }

    @PostMapping("/restore_password")
    public String restorePasswordGet(Client client, Model model){
        Client clientFDB = clientRepo.findByUsername(client.getUsername());
        registrationHelper(clientFDB);
        return "redirect:/login";
    }

    private void registrationHelper(Client client) {

        UUID uuid = UUID.randomUUID();
        String password = uuid.toString().substring(0, 8).toUpperCase();
        mailSender.send(client.getUsername(), "Activation code", password);
        client.setPassword(passwordEncoder.encode(password));
        client.setActive(true);
        clientRepo.save(client);
    }
}
