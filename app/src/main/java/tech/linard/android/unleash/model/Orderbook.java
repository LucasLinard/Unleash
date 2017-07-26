package tech.linard.android.unleash.model;

import java.util.ArrayList;

/**
 * Created by llinard on 08/07/17.
 */

public class Orderbook {
    private ArrayList<OrderbookItem> asks;
    private ArrayList<OrderbookItem> bids;


    public Orderbook(ArrayList<OrderbookItem> asks, ArrayList<OrderbookItem> bids) {
        this.asks = asks;
        this.bids = bids;
    }

    public Orderbook() {

    }

    public ArrayList<OrderbookItem> getAsks() {
        return asks;
    }

    public void setAsks(ArrayList<OrderbookItem> asks) {
        this.asks = asks;
    }

    public ArrayList<OrderbookItem> getBids() {
        return bids;
    }

    public void setBids(ArrayList<OrderbookItem> bids) {
        this.bids = bids;
    }
}
