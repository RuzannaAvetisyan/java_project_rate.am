package am.rate.project.bestexchangerate.dom;

public enum RequestType {
    SELL("Sell"),
    BUY("Buy");

    private final String name;

    RequestType(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}

