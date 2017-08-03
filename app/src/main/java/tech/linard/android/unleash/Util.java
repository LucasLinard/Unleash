package tech.linard.android.unleash;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import tech.linard.android.unleash.data.UnleashContract;
import tech.linard.android.unleash.model.Orderbook;
import tech.linard.android.unleash.model.OrderbookItem;
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

    public static Orderbook orderbookFromJSon(JSONObject response) {
        Orderbook orderbook = new Orderbook();
        orderbook.setAsks(fetchOrderbookItemsFromJSON(response, "asks"));
        orderbook.setBids(fetchOrderbookItemsFromJSON(response, "bids"));
        return orderbook;
    }

    private static ArrayList<OrderbookItem>
    fetchOrderbookItemsFromJSON(JSONObject jsonObject, String string) {
        ArrayList<OrderbookItem> arrayList = new ArrayList<>();
        JSONArray asksArray = jsonObject.optJSONArray(string);

        for (int x = 0; x < asksArray.length(); x++) {
            JSONArray currentItem = asksArray.optJSONArray(x);
            double price = currentItem.optDouble(0);
            double volume = currentItem.optDouble(1);
            arrayList.add(new OrderbookItem(price, volume));
        }
        return arrayList;
    }

    public static String getReadableDateFromUnixTime(int unixTime) {
        Date date = new Date((long) unixTime*1000);
        String formatedDate = DateFormat.getDateTimeInstance().format(date);
        return formatedDate;
    }
}
