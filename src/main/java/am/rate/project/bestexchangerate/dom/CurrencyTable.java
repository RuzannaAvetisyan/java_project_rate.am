package am.rate.project.bestexchangerate.dom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyTable {

    private String companyName;

    private HashMap<Currency, Float> buy = new HashMap<>();

    private HashMap<Currency, Float> sell = new HashMap<>();
}
