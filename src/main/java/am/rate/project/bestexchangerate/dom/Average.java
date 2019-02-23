package am.rate.project.bestexchangerate.dom;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@Entity
public class Average {

    @Id
    private LocalDate date = LocalDate.now();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "average_sell")
    @MapKeyColumn(name = "average_currency_sell")
    private Map<Currency, BigDecimal> averageCurrencySell = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "average_buy")
    @MapKeyColumn(name = "average_currency_buy")
    private Map<Currency, BigDecimal> averageCurrencyBuy = new HashMap<>();
}
