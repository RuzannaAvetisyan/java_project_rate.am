package am.rate.project.bestexchangerate.service;

import am.rate.project.bestexchangerate.dom.*;
import am.rate.project.bestexchangerate.repo.AverageRepo;
import am.rate.project.bestexchangerate.repo.StatisticsRepo;
import am.rate.project.bestexchangerate.repo.TableRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.Set;

@Slf4j
@Service
public class SchedulerStatisticsService {

    private final AverageRepo averageRepo;

    private final TableRepo tableRepo;

    private final StatisticsRepo statisticsRepo;

    @Autowired
    public SchedulerStatisticsService(AverageRepo averageRepo, TableRepo tableRepo, StatisticsRepo statisticsRepo) {
        this.averageRepo = averageRepo;
        this.tableRepo = tableRepo;
        this.statisticsRepo = statisticsRepo;
    }

    @Scheduled/*(cron = "0 0/2 * * * *")*/(cron = "0 0 1 * * ?")
    private void statisticsCreation(){
        Average averageBase = averageRepo.findByDate(LocalDate.now().minusDays(10));
        System.out.println(LocalDate.now().minusDays(10));
        Table table = new Table();
        Average averageToday = averageRepo.findByDate(LocalDate.now());
        Average averageYesterday = averageRepo.findByDate(LocalDate.now().minusDays(1));
        Set<Currency> currencies = averageToday.getAverageCurrencyBuy().keySet();
        for (Currency currency : currencies) {
            Statistics statistics = new Statistics();
            statistics.setBuy(averageToday.getAverageCurrencyBuy().get(currency));
            statistics.setSell(averageToday.getAverageCurrencySell().get(currency));
            statistics.setDifferenceBuy(averageYesterday.getAverageCurrencyBuy().get(currency).subtract(averageToday.getAverageCurrencyBuy().get(currency)));
            statistics.setDifferenceSell(averageYesterday.getAverageCurrencySell().get(currency).subtract(averageToday.getAverageCurrencySell().get(currency)));
            statistics.setGrowthRateBuy(growthRate(averageBase,averageToday,currency, RequestType.BUY));
            statistics.setGrowthRateSell(growthRate(averageBase,averageToday,currency, RequestType.SELL));
            statisticsRepo.save(statistics);
            table.getCurrencyStatistics().put(currency,statistics);
        }
        tableRepo.save(table);
    }

    private BigDecimal growthRate(Average base, Average current ,Currency currency, RequestType type) {
        double d;
        try{
            if(type.equals(RequestType.BUY)){
                d= current.getAverageCurrencyBuy().get(currency).divide(base.getAverageCurrencyBuy().get(currency), 4).doubleValue();
            } else{
                d = current.getAverageCurrencySell().get(currency).divide(base.getAverageCurrencySell().get(currency), 4).doubleValue();
            }
            double pow = Math.pow(d, 0.1);
            MathContext mathContext = new MathContext(4);
            return new BigDecimal(pow,mathContext);
        }catch (NullPointerException e){
            return null;
        }


    }

}

