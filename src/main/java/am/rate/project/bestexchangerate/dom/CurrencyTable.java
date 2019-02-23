package am.rate.project.bestexchangerate.dom;

import lombok.*;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyTable {

    private String companyName;

    private HashMap<Currency, Float> buy = new HashMap<>();

    private HashMap<Currency, Float> sell = new HashMap<>();

    @Override
    public String toString() {
        return  companyName + '\'' +
                ", buy=" + buy +
                ", sell=" + sell ;
    }
}
