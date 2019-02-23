package am.rate.project.bestexchangerate.service;

import am.rate.project.bestexchangerate.Exception.ForAverageException;
import am.rate.project.bestexchangerate.Exception.InternetChkaException;
import am.rate.project.bestexchangerate.dom.Average;
import am.rate.project.bestexchangerate.dom.Currency;
import am.rate.project.bestexchangerate.dom.CurrencyTable;
import am.rate.project.bestexchangerate.repo.AverageRepo;
import am.rate.project.bestexchangerate.repo.CurrencyRepo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ParserService {

    private Document document;

    private List<CurrencyTable> currencyTable = new ArrayList<>();

    private Average average = new Average();

    private boolean isAverege = false;
    private final CurrencyRepo currencyRepo;

    private final AverageRepo averageRepo;

    @Autowired
    public ParserService(CurrencyRepo currencyRepo, AverageRepo averageRepo) {
        this.currencyRepo = currencyRepo;
        this.averageRepo = averageRepo;
    }

    private void retry(int count, int max, String http) {
        try {
            document = Jsoup.connect(http).get();
        } catch (IOException e) {
            if (count >= max) throw new InternetChkaException();
            retry(++count, max, http);
        }
    }

    private void retry(int count, int max, String http, String currency) {
        try {
            document = Jsoup.connect(http).cookie("Cookie.CurrencyList", currency + ",1 EUR,1 RUR,1 GBP").get();
        } catch (IOException e) {
            if (count >= max) throw new InternetChkaException();
            retry(++count, max, http, currency);
        }
    }

    public List<CurrencyTable> parser(String http) {

        retry(0, 10, http);

        List<String> currencies = document.getElementById("ctl00_Content_RB_dlCurrency1").getElementsByTag("option")
                .stream().map(Element::text).collect(Collectors.toList());

        currencies.stream().filter(c -> !currencyRepo.existsByCurrencyType(c)).forEach(c -> currencyRepo.save(new Currency(c)));

        for (String currency : currencies) {
            retry(0, 10, http, currency);
            Element table = document.getElementById("rb");
            Elements rows = table.select("tr");
            Currency currency1 = currencyRepo.findByCurrencyType(currency);
            for (int i = 2; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements cols = row.select("td");
                try {
                    if (cols.isEmpty() || cols.get(0).text().equals("Minimum") || cols.get(0).text().equals("Maximum") ||
                            cols.get(0).text().equals("Average") || cols.get(0).text().contains("Fluctuation")) {
                        throw new ForAverageException();
                    }
                    currencyTable.stream().filter(obj -> obj.getCompanyName().equals(cols.get(1).text()))
                            .findFirst()
                            .map(obj -> {
                                obj.getBuy().put(currency1, floatParser(cols.get(5).text()));
                                obj.getSell().put(currency1, floatParser(cols.get(6).text()));
                                return obj;
                            })
                            .orElseGet(() -> {
                                currencyTable.add(new CurrencyTable(cols.get(1).text(),
                                        new HashMap<Currency, Float>() {{
                                            put(currency1, floatParser(cols.get(5).text()));
                                        }},
                                        new HashMap<Currency, Float>() {{
                                            put(currency1, floatParser(cols.get(6).text()));
                                        }}));
                                return new CurrencyTable();
                            });
                } catch (IndexOutOfBoundsException e) {
                } catch (ForAverageException e) {
                    if(cols.get(0).text().equals("Average")){
                        average.getAverageCurrencyBuy().put(currency1, bigDecimalParser(cols.get(1).text()));
                        average.getAverageCurrencySell().put(currency1, bigDecimalParser(cols.get(2).text()));
                        isAverege = true;
                    }
                }
            }
        }

        if(isAverege){
            Average average1 = averageRepo.findByDate(LocalDate.now());
            if(average1 != null){
                average = merge(average1, average);
                averageRepo.save(average);
            }else{
                averageRepo.save(average);
            }
        }
        return currencyTable;
    }

    private Average merge(Average average1, Average average2) {
        average1.setAverageCurrencyBuy(merge(average2.getAverageCurrencyBuy(),average1.getAverageCurrencyBuy()));
        average1.setAverageCurrencySell(merge(average2.getAverageCurrencySell(),average1.getAverageCurrencySell()));
        return average1;
    }

    private Map<Currency, BigDecimal>  merge(Map<Currency, BigDecimal> averageCurrency1, Map<Currency, BigDecimal> averageCurrency2) {
        Map<Currency, BigDecimal> averageMap = new HashMap<>();
        Set<Currency> currencies = averageCurrency1.keySet();
        List<String> strings = currencies.stream().map(Currency::getCurrencyType).collect(Collectors.toList());
        averageCurrency2.forEach((currency,bigDecimal) -> {if(!strings.contains(currency.getCurrencyType())) currencies.add(currency);});
        for (Currency currency : currencies) {
            BigDecimal average = null;
            if(averageCurrency1.get(currency) != null && averageCurrency2.get(currency) != null) {
                average = (averageCurrency1.get(currency).add(averageCurrency2.get(currency))).divide(BigDecimal.valueOf(2),2);
            }
            else if(averageCurrency1.get(currency) == null){
                average = averageCurrency2.get(currency);
            }
            else if(averageCurrency2.get(currency) == null){
                average = averageCurrency1.get(currency);
            }
            averageMap.put(currency, average);
        }

        return averageMap;
    }

    private Float floatParser(String text) {
        if (text.equals("")) return null;
        else return Float.parseFloat(text);
    }

    private BigDecimal bigDecimalParser(String text) {
        if (text.equals("")) return null;
        else return new  BigDecimal(text);
    }
}
