package am.rate.project.bestexchangerate.dom;

public enum ExchangeOption {
    CASH("Cash"),
    NON_CASH("Non cash"),
    CARD("Card"),
    IGNORE_OPTION("Ignore option");

    private final String name;

    ExchangeOption(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
