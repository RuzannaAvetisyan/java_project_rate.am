package am.rate.project.bestexchangerate.dom;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
@Data
@NoArgsConstructor
@Entity
public class Statistics {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal buy;
    private BigDecimal Sell;
    private BigDecimal differenceBuy;
    private BigDecimal differenceSell;
    private BigDecimal growthRateBuy;
    private BigDecimal growthRateSell;


}
