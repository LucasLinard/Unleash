package tech.linard.android.unleash;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import tech.linard.android.unleash.model.OrderBook;
import tech.linard.android.unleash.model.OrderBookItem;
import tech.linard.android.unleash.model.Ticker;

/**
 * Created by llinard on 07/07/17.
 */

public class Util {
    public static Ticker tickerFromJSon(JSONObject jsonObject) {
        double high =  jsonObject.optDouble("high");
        double low = jsonObject.optDouble("low");
        double vol =  jsonObject.optDouble("vol");
        double last = jsonObject.optDouble("last");
        double buy = jsonObject.optDouble("buy");
        double sell = jsonObject.optDouble("sell");
        Integer date = jsonObject.optInt("date");

        Ticker currentTicker = new Ticker(high, low, vol, last, buy, sell, date);

        return currentTicker;
    }

    public static OrderBook orderbookFromJSon(JSONObject response) {
        OrderBook orderBook = new OrderBook();

        orderBook.setAsks(fetchOrderbookItemsFromJSON(response, "asks"));
        orderBook.setBids(fetchOrderbookItemsFromJSON(response, "bids"));

        return orderBook;
    }

    private static ArrayList<OrderBookItem>
    fetchOrderbookItemsFromJSON(JSONObject jsonObject, String string) {
        ArrayList<OrderBookItem> arrayList = new ArrayList<>();
        JSONArray asksArray = jsonObject.optJSONArray(string);

        for (int x = 0; x < asksArray.length(); x++) {
            JSONArray currentItem = asksArray.optJSONArray(x);
            double price = currentItem.optDouble(0);
            double volume = currentItem.optDouble(1);
            arrayList.add(new OrderBookItem(price, volume));
        }

        return arrayList;

    }
}
