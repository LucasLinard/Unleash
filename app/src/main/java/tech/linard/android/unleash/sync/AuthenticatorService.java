package tech.linard.android.unleash.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import tech.linard.android.unleash.sync.Authenticator;

public class AuthenticatorService extends Service {

    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
