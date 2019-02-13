package am.rate.project.bestexchangerate.dom;

public enum ExchangeOption {
    CASH("Cash"),
    NON_CASH("Non cash"),
    CARD("Card");

    private final String name;

    ExchangeOption(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
