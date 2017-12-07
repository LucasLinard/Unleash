package tech.linard.android.unleash.model;

import android.net.Uri;

import java.util.UUID;

/**
 * Created by llinard on 06/12/17.
 */

public class User {
    private String uuid;
    private String token;

    public User(String uuid, String token) {
        this.uuid = uuid;
        this.token = token;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
