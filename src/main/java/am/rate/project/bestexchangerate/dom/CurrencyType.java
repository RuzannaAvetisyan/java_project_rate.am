package am.rate.project.bestexchangerate.dom;

public enum CurrencyType {
    USD("USD"),
    EUR("EUR"),
    RUR("RUR"),
    GBP("GBP");
//    GEL, CHF, CAD, AED, CNY, AUD, JPY, SEK, HKD, KZT, XAU

    private final String name;

    CurrencyType(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
