package tech.linard.android.unleash.model;

/**
 * Created by llinard on 08/07/17.
 */

public class OrderBookItem {
    private Double price;
    private Double volume;

    public OrderBookItem(Double price, Double volume) {
        this.price = price;
        this.volume = volume;
    }

    public OrderBookItem() {

    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }
}
