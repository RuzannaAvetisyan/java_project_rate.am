package am.rate.project.bestexchangerate.dom;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Entity
@javax.persistence.Table(name = "statistics_table")
public class Table {
    @Id
    private LocalDate date = LocalDate.now();

//    @OneToMany(targetEntity = Statistics.class, fetch = FetchType.EAGER, mappedBy = "id", cascade = CascadeType.ALL)
//    List<Statistics> currencyStatistics = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable()
    @MapKeyColumn(name = "currency")
    private Map<Currency, Statistics> currencyStatistics = new HashMap<>();
}
