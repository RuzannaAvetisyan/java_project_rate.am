package am.rate.project.bestexchangerate.repo;

import am.rate.project.bestexchangerate.dom.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface ClientRepo extends JpaRepository<Client,String> {

    Client findByUsername(String username);
}
