package am.rate.project.bestexchangerate.repo;

import am.rate.project.bestexchangerate.dom.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepo extends JpaRepository<Currency, String> {
    Currency findByCurrencyType(String s);
}
