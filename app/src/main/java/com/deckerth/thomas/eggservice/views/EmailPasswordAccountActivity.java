package com.deckerth.thomas.eggservice.views;

import android.content.Context;
import android.content.Intent;

public class EmailPasswordAccountActivity extends EmailPasswordSignInActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, EmailPasswordAccountActivity.class);
        intent.setAction("android.intent.action.APPLICATION_PREFERENCES");
        return intent;
    }
}
