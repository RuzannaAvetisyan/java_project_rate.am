package am.rate.project.bestexchangerate.service;

import am.rate.project.bestexchangerate.dom.*;
import am.rate.project.bestexchangerate.repo.AverageRepo;
import am.rate.project.bestexchangerate.repo.CurrencyRepo;
import am.rate.project.bestexchangerate.repo.RequestRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SchedulerNotificationService {

    private final RequestRepo requestRepo;

    private final CurrencyRepo currencyRepo;

    private final MailSender mailSender;

    private final AverageRepo averageRepo;

    @Autowired
    public SchedulerNotificationService(RequestRepo requestRepo, CurrencyRepo currencyRepo, MailSender mailSender, AverageRepo averageRepo) {
        this.requestRepo = requestRepo;
        this.currencyRepo = currencyRepo;
        this.mailSender = mailSender;
        this.averageRepo = averageRepo;
    }

    @Scheduled(fixedDelay = 10000)
    public void sendMailToClients() {
        List<Request> requestCash = requestRepo.findByActiveTrueAndDeadlineAfterAndExchangeOption(LocalDate.now().minusDays(1), ExchangeOption.CASH);
        List<Request> requestNonCash = requestRepo.findByActiveTrueAndDeadlineAfterAndExchangeOption(LocalDate.now().minusDays(1), ExchangeOption.NON_CASH);
        List<Request> requestCard = requestRepo.findByActiveTrueAndDeadlineAfterAndExchangeOption(LocalDate.now().minusDays(1), ExchangeOption.CARD);
        List<Request> requestIgnoreOption = requestRepo.findByActiveTrueAndDeadlineAfterAndExchangeOption(LocalDate.now().minusDays(1), ExchangeOption.IGNORE_OPTION);

        List<CurrencyTable> currencyTablesCash = new ArrayList<>();
        List<CurrencyTable> currencyTablesNonCash = new ArrayList<>();
        List<CurrencyTable> currencyTablesCard = new ArrayList<>();
        List<CurrencyTable> currencyTablesIgnoreOption = new ArrayList<>();

        if (!requestCash.isEmpty()) {
            String banksCash = "http://www.rate.am/en/armenian-dram-exchange-rates/banks/cash";
            String exchangePointsCash = "http://www.rate.am/en/armenian-dram-exchange-rates/exchange-points/cash/retail";
            String creditOrganizationsCash = "http://www.rate.am/en/armenian-dram-exchange-rates/credit-organizations/cash";
            currencyTablesCash = creatCurrencyTable(banksCash, exchangePointsCash, creditOrganizationsCash);
            checkAndMailing(currencyTablesCash,requestCash);
        }
        if (!requestNonCash.isEmpty()) {
            String banksNonCash = "http://www.rate.am/en/armenian-dram-exchange-rates/banks/non-cash";
            String creditOrganizationsNonCash = "http://www.rate.am/en/armenian-dram-exchange-rates/credit-organizations/non-cash";
            String investmentOrganizationsNonCash = "http://www.rate.am/en/armenian-dram-exchange-rates/investment-organizations/non-cash";
            currencyTablesNonCash = creatCurrencyTable(banksNonCash, creditOrganizationsNonCash, investmentOrganizationsNonCash);
            checkAndMailing(currencyTablesNonCash,requestNonCash);
        }
        if (!requestCard.isEmpty()) {
            String banksCard = "http://www.rate.am/en/armenian-dram-exchange-rates/banks/card-transaction";
            currencyTablesCard = new ParserService(currencyRepo, averageRepo).parser(banksCard);
            checkAndMailing(currencyTablesCard,requestCard);
        }
        if(!requestIgnoreOption.isEmpty()){
            if(currencyTablesCash.isEmpty()){
                String banksCash = "http://www.rate.am/en/armenian-dram-exchange-rates/banks/cash";
                String exchangePointsCash = "http://www.rate.am/en/armenian-dram-exchange-rates/exchange-points/cash/retail";
                String creditOrganizationsCash = "http://www.rate.am/en/armenian-dram-exchange-rates/credit-organizations/cash";
                currencyTablesCash = creatCurrencyTable(banksCash, exchangePointsCash, creditOrganizationsCash);
            }
            if(currencyTablesNonCash.isEmpty()){
                String banksNonCash = "http://www.rate.am/en/armenian-dram-exchange-rates/banks/non-cash";
                String creditOrganizationsNonCash = "http://www.rate.am/en/armenian-dram-exchange-rates/credit-organizations/non-cash";
                String investmentOrganizationsNonCash = "http://www.rate.am/en/armenian-dram-exchange-rates/investment-organizations/non-cash";
                currencyTablesNonCash = creatCurrencyTable(banksNonCash, creditOrganizationsNonCash, investmentOrganizationsNonCash);
            }
            if(currencyTablesCard.isEmpty()){
                String banksCard = "http://www.rate.am/en/armenian-dram-exchange-rates/banks/card-transaction";
                currencyTablesCard = new ParserService(currencyRepo, averageRepo).parser(banksCard);
            }
            currencyTablesIgnoreOption = currencyTablesCash;
            currencyTablesIgnoreOption.addAll(currencyTablesNonCash);
            currencyTablesIgnoreOption.addAll(currencyTablesCard);
            checkAndMailing(currencyTablesIgnoreOption,requestIgnoreOption);
        }

    }

    private void checkAndMailing(List<CurrencyTable> currencyTables, List<Request> request) {
        List<Request> requestSell = fitlerByType(request, RequestType.SELL);
        List<Request> requestBuy = fitlerByType(request, RequestType.BUY);
        List<Currency> currencies = currencyRepo.findAll();
        if(!requestSell.isEmpty()){
            currencies.forEach(currency ->{
                List<CurrencyTable> currencyTable = min(currencyTables,currency);
                if(!currencyTable.isEmpty()){
                    StringBuilder companyName = new StringBuilder();
                    currencyTable.forEach((ct) -> companyName.append(ct.getCompanyName()).append("\n"));
                    Float value = currencyTable.get(0).getSell().get(currency);
                    requestSell.forEach(s ->{
                        if (value <= s.getValue() && s.isActive() && s.getCurrency().equals(currency)) {
                            messageFormatter(companyName, value, s);
                        }
                    });
                }
            });
        }
        if(!requestBuy.isEmpty()){
            currencies.forEach(currency -> {
                List<CurrencyTable> currencyTable = max(currencyTables,currency);
                if(!currencyTable.isEmpty()){
                    StringBuilder companyName = new StringBuilder();
                    currencyTable.forEach((ct) -> companyName.append(ct.getCompanyName()).append("\n"));
                    Float value = currencyTable.get(0).getBuy().get(currency);
                    requestBuy.forEach(b -> {
                        if( value >= b.getValue()&& b.isActive() && b.getCurrency().equals(currency)){
                            messageFormatter(companyName, value, b);
                        }
                    });
                }
            });
        }
    }

    private void messageFormatter(StringBuilder companyName, Float value, Request request) {
        mailSender.send(request.getClient().getUsername(), "Best exchange rate.",
                String.format(
                        "   Dear %s !\n" +
                        "   The best rate for your request %s equals %s."  +
                        " Below is a list of companies where this exchange rate was.\n%s \n\n" +
                        "----------------------------------------- \n" +
                        " With kind regards,\n Best Exchange Rate." +
                        " link to http://localhost:8080/home",
                        request.getClient().getName(),
                        request.toString(),
                        value,
                        companyName
                )
        );
        request.setActive(false);
        requestRepo.save(request);
    }

    private List<CurrencyTable> creatCurrencyTable(String banks, String creditOrganizations, String investmentOrganizations) {
        List<CurrencyTable> currencyTable = new ParserService(currencyRepo, averageRepo).parser(banks);
        currencyTable.addAll(new ParserService(currencyRepo, averageRepo).parser(creditOrganizations));
        currencyTable.addAll(new ParserService(currencyRepo, averageRepo).parser(investmentOrganizations));

        return currencyTable;
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
    private List<Request> fitlerByType (List<Request> requests, RequestType requestType){
        return requests.stream().filter(request -> request.getRequestType() == requestType).collect(Collectors.toList());
    }
}