package am.rate.project.bestexchangerate.dom;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;


@NoArgsConstructor
@Getter
@Setter
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
    @JoinColumn(name = "fk_currency" , referencedColumnName = "currency_type")
    private Currency currency;

    @Column(nullable = false)
    private Float value = 0.0F;

    @Column(name = "active")
    private boolean active = true;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "fk_client", referencedColumnName = "username")
    private Client client;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return  " Type: " + requestType.getName() +
                ", Exchange Option: " + exchangeOption.getName() +
                ", Currency: " + currency.getCurrencyType() +
                ", Value: " + value +
                ", Deadline: " + deadline;
    }
}
