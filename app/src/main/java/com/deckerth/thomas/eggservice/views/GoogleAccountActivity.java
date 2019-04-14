package com.deckerth.thomas.eggservice.views;

import android.content.Context;
import android.content.Intent;

public class GoogleAccountActivity extends GoogleSignInActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, GoogleAccountActivity.class);
        intent.setAction("android.intent.action.APPLICATION_PREFERENCES");
        return intent;
    }
}
