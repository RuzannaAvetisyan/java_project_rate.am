package am.rate.project.bestexchangerate.dom;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

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
    private Date deadline = new Date();
    @ManyToOne()
    @JoinColumn(name = "fk_client")
    private Client client;

    public Request(RequestType requestType, ExchangeOption exchangeOption, Currency currency, Float value, boolean active, Date deadline, Client client) {
        this.requestType = requestType;
        this.exchangeOption = exchangeOption;
        this.currency = currency;
        this.value = value;
        this.active = active;
        this.deadline = deadline;
        this.client = client;
    }

    public Request() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ExchangeOption getExchangeOption() {
        return exchangeOption;
    }

    public void setExchangeOption(ExchangeOption exchangeOption) {
        this.exchangeOption = exchangeOption;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
