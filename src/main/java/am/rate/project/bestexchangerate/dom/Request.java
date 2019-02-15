package am.rate.project.bestexchangerate.dom;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
//@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"request_type","exchange_options", "fk_currency", "active", "fk_client"}) })
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, name = "request_type")
    @Enumerated(value = EnumType.STRING)
    private RequestType requestType = RequestType.SELL;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "exchange_options")
    private ExchangeOption exchangeOption = ExchangeOption.CASH;

    @ManyToOne()
    @JoinColumn(name = "fk_currency")
    private Currency currency;

    @Column(nullable = false)
    private Float value = 0.0F;

    @Column(name = "active")
    private boolean active = true;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    @ManyToOne()
    @JoinColumn(name = "fk_client")
    private Client client;

}
