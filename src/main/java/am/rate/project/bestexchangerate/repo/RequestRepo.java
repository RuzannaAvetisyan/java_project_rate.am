package am.rate.project.bestexchangerate.repo;

import am.rate.project.bestexchangerate.dom.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface RequestRepo extends CrudRepository<Request, Long> {

    List<Request> findByActiveTrueAndDeadlineAfterAndExchangeOption(LocalDate deadline, ExchangeOption exchangeOption);
    List<Request> findByActiveTrueAndDeadlineAfterAndClient(LocalDate deadline, Client client);
    List<Request> findByActiveTrueAndDeadlineAfterAndExchangeOptionAndClientAndCurrencyAndValueAndRequestType(LocalDate deadline, ExchangeOption exchangeOption, Client client, Currency currency, Float value, RequestType requestType);
}
