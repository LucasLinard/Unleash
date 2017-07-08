package tech.linard.android.unleash;

import org.json.JSONObject;

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
}
