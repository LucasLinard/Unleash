package tech.linard.android.unleash.model;

import java.util.ArrayList;

/**
 * Created by llinard on 08/07/17.
 */

public class OrderBook {
    private ArrayList<OrderBookItem> asks;
    private ArrayList<OrderBookItem> bids;

    public OrderBook(ArrayList<OrderBookItem> asks, ArrayList<OrderBookItem> bids) {
        this.asks = asks;
        this.bids = bids;
    }

    public ArrayList<OrderBookItem> getAsks() {
        return asks;
    }

    public void setAsks(ArrayList<OrderBookItem> asks) {
        this.asks = asks;
    }

    public ArrayList<OrderBookItem> getBids() {
        return bids;
    }

    public void setBids(ArrayList<OrderBookItem> bids) {
        this.bids = bids;
    }
}
