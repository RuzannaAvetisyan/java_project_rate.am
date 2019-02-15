package am.rate.project.bestexchangerate.service;

import am.rate.project.bestexchangerate.dom.Currency;
import am.rate.project.bestexchangerate.dom.CurrencyTable;
import am.rate.project.bestexchangerate.dom.ExchangeOption;
import am.rate.project.bestexchangerate.dom.Request;
import am.rate.project.bestexchangerate.repo.CurrencyRepo;
import am.rate.project.bestexchangerate.repo.RequestRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SchedulerNotificationService {

    @Autowired
    private RequestRepo requestRepo;

    @Autowired
    private CurrencyRepo currencyRepo;

    @Scheduled(fixedDelay = 10000)
    public void sendMailToUsers() {
        List<Request> requestCash = requestRepo.findByActiveTrueAndDeadlineAfterAndExchangeOption(LocalDate.now().minusDays(1), ExchangeOption.CASH);
        List<Request> requestNonCash = requestRepo.findByActiveTrueAndDeadlineAfterAndExchangeOption(LocalDate.now().minusDays(1), ExchangeOption.NON_CASH);
        List<Request> requestCard = requestRepo.findByActiveTrueAndDeadlineAfterAndExchangeOption(LocalDate.now().minusDays(1), ExchangeOption.CARD);

        if (!requestCash.isEmpty()) {
            String banksCash = "http://www.rate.am/en/armenian-dram-exchange-rates/banks/cash";
            String exchangePointsCash = "http://www.rate.am/en/armenian-dram-exchange-rates/exchange-points/cash/retail";
            String creditOrganizationsCash = "http://www.rate.am/en/armenian-dram-exchange-rates/credit-organizations/cash";
            List<CurrencyTable> currencyTablesCash = creatCurrencyTable(banksCash, exchangePointsCash, creditOrganizationsCash);
            List<CurrencyTable> c = min(currencyTablesCash, currencyRepo.findByCurrencyType("1 USD"));
            List<CurrencyTable> c2 = max(currencyTablesCash, currencyRepo.findByCurrencyType("1 USD"));
            c.forEach(System.out::println);
            c2.forEach(System.out::println);
            System.out.println("Cash" + LocalDateTime.now());
        }
        if (!requestNonCash.isEmpty()) {
            String banksNonCash = "http://www.rate.am/en/armenian-dram-exchange-rates/banks/non-cash";
            String creditOrganizationsNonCash = "http://www.rate.am/en/armenian-dram-exchange-rates/credit-organizations/non-cash";
            String investmentOrganizationsNonCash = "http://www.rate.am/en/armenian-dram-exchange-rates/investment-organizations/non-cash";
            List<CurrencyTable> currencyTablesNonCash = creatCurrencyTable(banksNonCash, creditOrganizationsNonCash, investmentOrganizationsNonCash);
            System.out.println("NonCash" + LocalDateTime.now());
        }
        if (!requestCard.isEmpty()) {
            String banksCard = "http://www.rate.am/en/armenian-dram-exchange-rates/banks/card-transaction";
            List<CurrencyTable> currencyTablesCard = new ParserService(currencyRepo).parser(banksCard);
            System.out.println("Card" + LocalDateTime.now());
        }
    }

    private List<CurrencyTable> creatCurrencyTable(String banksNonCash, String creditOrganizationsNonCash, String investmentOrganizationsNonCash) {
        List<CurrencyTable> currencyTableNonCash = new ParserService(currencyRepo).parser(banksNonCash);
        currencyTableNonCash.addAll(new ParserService(currencyRepo).parser(creditOrganizationsNonCash));
        currencyTableNonCash.addAll(new ParserService(currencyRepo).parser(investmentOrganizationsNonCash));

        return currencyTableNonCash;
    }

    private List<CurrencyTable> max(List<CurrencyTable> currencyTables, Currency currency) {
        List<CurrencyTable> currencyTable = new ArrayList<>();
        Float max = 0F;
        for (CurrencyTable currencyTable2 : currencyTables) {
            Float temp = currencyTable2.getBuy().get(currency);
            if(temp != null && temp >= max){
                if (temp.equals(max)){
                    currencyTable.add(currencyTable2);
                }else {
                    max = temp;
                    currencyTable.clear();
                    currencyTable.add(currencyTable2);
                }

            }
        }
        return currencyTable;
    }
    private List<CurrencyTable> min(List<CurrencyTable> currencyTables, Currency currency) {
        List<CurrencyTable> currencyTable = new ArrayList<>();
        Float min = 1000000000F;
        for (CurrencyTable currencyTable2 : currencyTables) {
            Float temp = currencyTable2.getSell().get(currency);
            if(temp != null && temp <= min){
                if(temp < min){
                    min = temp;
                    currencyTable.clear();
                    currencyTable.add(currencyTable2);
                }else{
                    currencyTable.add(currencyTable2);
                }
            }
        }
        return currencyTable;
    }
}