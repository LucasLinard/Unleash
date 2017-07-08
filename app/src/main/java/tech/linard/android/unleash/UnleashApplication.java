package tech.linard.android.unleash;

import android.app.Application;
import android.util.Log;

/**
 * Created by llinard on 07/07/17.
 */

public class UnleashApplication extends Application {
    private static final String TAG = UnleashApplication.class.getSimpleName();
    private static UnleashApplication instance = null;
    public static UnleashApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "UnleashApplication.OnCreate()");
        instance = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "UnleashApplication.OnTerminate()");
    }
}
