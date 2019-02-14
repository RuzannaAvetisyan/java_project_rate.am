package am.rate.project.bestexchangerate.dom;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Currency {
    @Id
    private String currencyType;

    public Currency() {
    }

    public String getCurrencyType() {
        return currencyType;
    }
}
