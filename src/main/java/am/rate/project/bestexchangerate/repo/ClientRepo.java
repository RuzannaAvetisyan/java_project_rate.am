package am.rate.project.bestexchangerate.repo;

import am.rate.project.bestexchangerate.dom.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepo extends JpaRepository<Client,String> {
    Client findByEmail(String email);
}
