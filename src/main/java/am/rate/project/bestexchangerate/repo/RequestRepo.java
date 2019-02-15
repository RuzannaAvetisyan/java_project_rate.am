package am.rate.project.bestexchangerate.repo;

import am.rate.project.bestexchangerate.dom.ExchangeOption;
import am.rate.project.bestexchangerate.dom.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDate;
import java.util.List;

@EnableJpaRepositories
public interface RequestRepo extends JpaRepository<Request, Long> {

    List<Request> findByActiveTrueAndDeadlineAfterAndExchangeOption(LocalDate deadline, ExchangeOption exchangeOption);
}
