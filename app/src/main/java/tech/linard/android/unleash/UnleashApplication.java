package tech.linard.android.unleash;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.stetho.Stetho;

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
        configureStetho(getApplicationContext());

        Log.d(TAG, "UnleashApplication.OnCreate()");
        instance = this;
    }

    private void configureStetho(Context applicationContext) {
        // Create an InitializerBuilder
        Stetho.InitializerBuilder initializerBuilder =
                Stetho.newInitializerBuilder(this);

        // Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(
                Stetho.defaultInspectorModulesProvider(this)
        );

        // Enable command line interface
        initializerBuilder.enableDumpapp(
                Stetho.defaultDumperPluginsProvider(applicationContext)
        );

        // Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = initializerBuilder.build();

        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "UnleashApplication.OnTerminate()");
    }
}
