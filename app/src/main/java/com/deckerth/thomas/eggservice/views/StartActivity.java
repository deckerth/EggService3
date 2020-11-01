package com.deckerth.thomas.eggservice.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.deckerth.thomas.eggservice.BasicApp;
import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.firebase.DataManagement;
import com.deckerth.thomas.eggservice.settings.Settings;

public class StartActivity extends AppCompatActivity {

    public final static String EXTRA_PATH = "StartActivity_Path";

    public static StartActivity Current;

    private Boolean mSticky = Boolean.TRUE;

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.CONNECTING) {
            return true;

        } else if (
                connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, StartActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Current = this;

        setContentView(R.layout.start_activity);

        String action = getIntent().getAction();
        mSticky = (action != null) && action.contentEquals("android.intent.action.APPLICATION_PREFERENCES");

        Button googleLoginButton = findViewById(R.id.googleSignIn);
        googleLoginButton.setOnClickListener(v -> {
            if (isInternetOn()) {
                Intent intent;
                if (mSticky) {
                    intent = GoogleAccountActivity.getIntent(StartActivity.this);
                } else {
                    intent = GoogleSignInActivity.getIntent(StartActivity.this);
                }
                startActivity(intent);
            } else {
                Toast.makeText(StartActivity.this, getText(R.string.not_connected), Toast.LENGTH_LONG).show();
            }
        });

        Button emailLoginButton = findViewById(R.id.emailSignIn);
        emailLoginButton.setOnClickListener(v -> {
            if (isInternetOn()) {
                Intent intent;
                if (mSticky) {
                    intent = EmailPasswordAccountActivity.getIntent(StartActivity.this);
                } else {
                    intent = EmailPasswordSignInActivity.getIntent(StartActivity.this);
                }
                startActivity(intent);
            } else {
                Toast.makeText(StartActivity.this, getText(R.string.not_connected), Toast.LENGTH_LONG).show();
            }
        });

        Button offlineMode = findViewById(R.id.offline_mode);
        offlineMode.setOnClickListener(v -> {
            if (mSticky) {
                finish();
            } else {
                Settings.Current.setDisplayStartDialog(StartActivity.this, Boolean.FALSE);
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(getString(R.string.offline_mode));
                builder.setMessage(getString(R.string.offline_mode_info));
                builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    dialog.dismiss();
                    startActivity(MainActivity.getIntent(StartActivity.this, ""));
                });
                builder.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mSticky) {
            if (DataManagement.getInstance().isConnectedToFirebase(false)) {
                String group = Settings.Current.getGroupName(BasicApp.getContext());
                if ((group != null) && (!group.isEmpty())) {
                    startActivity(MainActivity.getIntent(StartActivity.this, ""));
                } else {
                    startActivity(GroupActivity.getIntent(StartActivity.this));
                }
            } else if (!Settings.Current.getDisplayStartDialog(StartActivity.this)) {
                startActivity(MainActivity.getIntent(StartActivity.this, ""));
            }
        }
    }

}
