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
    private RequestType requestType;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "Exchange_options")
    private ExchangeOption exchangeOption = ExchangeOption.CASH;
    @ManyToOne
    @JoinColumn(name = "fk_currency")
    private Currency currency;
    @Column(nullable = false)
    private Float value = 0.0F;
    @Column(name = "active")
    private boolean active;
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deadline;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_client")
    private Client client;

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

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
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
}
