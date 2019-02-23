package am.rate.project.bestexchangerate.repo;

import am.rate.project.bestexchangerate.dom.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface CurrencyRepo extends JpaRepository<Currency, String> {

    Currency findByCurrencyType(String s);

    boolean existsByCurrencyType(String s);
}
