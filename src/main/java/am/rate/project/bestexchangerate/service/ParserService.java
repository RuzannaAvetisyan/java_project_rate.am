package am.rate.project.bestexchangerate.service;

import am.rate.project.bestexchangerate.dom.Currency;
import am.rate.project.bestexchangerate.dom.CurrencyTable;
import am.rate.project.bestexchangerate.repo.CurrencyRepo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParserService {

    private Document document;

    private List<CurrencyTable> currencyTable = new ArrayList<>();

    private final CurrencyRepo currencyRepo;

    @Autowired
    public ParserService(CurrencyRepo currencyRepo) {
        this.currencyRepo = currencyRepo;
    }

    private void retry(int count, int max, String http) {
        try {
            document = Jsoup.connect(http).get();
        } catch (IOException e) {
            if (count >= max) throw new RuntimeException();
            retry(++count, max, http);
        }
    }

    private void retry(int count, int max, String http, String currency) {
        try {
            document = Jsoup.connect(http).cookie("Cookie.CurrencyList", currency + ",1 EUR,1 RUR,1 GBP").get();
        } catch (IOException e) {
            if (count >= max) throw new RuntimeException();
            retry(++count, max, http);
        }
    }

    public List<CurrencyTable> parser(String http) {

        retry(0, 10, http);

        List<String> currencies = document.getElementById("ctl00_Content_RB_dlCurrency1").getElementsByTag("option")
                .stream().map(Element::text).collect(Collectors.toList());

        List<String> curr = currencies.stream().filter(c -> !currencyRepo.existsByCurrencyType(c)).collect(Collectors.toList());
        currencies.stream().filter(c -> !currencyRepo.existsByCurrencyType(c)).forEach(c -> currencyRepo.save(new Currency(c)));

        for (String currency : currencies) {
            retry(0, 10, http, currency);
            Element table = document.getElementById("rb");
            Elements rows = table.select("tr");
            Currency currency1 = currencyRepo.getByCurrencyType(currency);
            for (int i = 2; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements cols = row.select("td");
                if (cols.isEmpty() || cols.get(0).text().equals("Minimum") || cols.get(0).text().equals("Maximum") ||
                        cols.get(0).text().equals("Average") || cols.get(0).text().contains("Fluctuation")) {
                    break;
                }
                try {
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
                }
            }
        }

        return currencyTable;
    }

    private Float floatParser(String text) {
        if (text.equals("")) return null;
        else return Float.parseFloat(text);
    }
}
