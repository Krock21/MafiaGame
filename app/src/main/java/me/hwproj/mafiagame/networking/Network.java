package me.hwproj.mafiagame.networking;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class Network {
    public static GoogleSignInAccount googleSignInAccount;

    public Network(GoogleSignInAccount googleSignInAccount) {
        this.googleSignInAccount = googleSignInAccount;
    }
}
