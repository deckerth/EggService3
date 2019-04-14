package com.deckerth.thomas.eggservice.views;

import android.content.Context;
import android.content.Intent;

public class LoginActivity extends StartActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setAction("android.intent.action.APPLICATION_PREFERENCES");
        return intent;
    }

}
