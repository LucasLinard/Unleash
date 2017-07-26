package tech.linard.android.unleash.model;

/**
 * Created by llinard on 08/07/17.
 */

public class OrderbookItem {
    private Double price;
    private Double volume;

    public OrderbookItem(Double price, Double volume) {
        this.price = price;
        this.volume = volume;
    }

    public OrderbookItem() {

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
