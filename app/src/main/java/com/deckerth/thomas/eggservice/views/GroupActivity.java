package com.deckerth.thomas.eggservice.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.deckerth.thomas.eggservice.BasicApp;
import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.firebase.CheckConnectivityTask;
import com.deckerth.thomas.eggservice.firebase.DataManagement;
import com.deckerth.thomas.eggservice.firebase.DataManagementResult;
import com.deckerth.thomas.eggservice.firebase.DataManagementTask;
import com.deckerth.thomas.eggservice.firebase.OwnRequestsObserver;
import com.deckerth.thomas.eggservice.firebase.SendJoinRequestTask;

public class GroupActivity extends BaseActivity {

    public static final String TAG = "GroupActivity";

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, GroupActivity.class);
        return intent;
    }

    private TextView mStatusText;
    private TextView mGroupName;
    private Button mCreateGroup;
    private Button mJoinGroup;
    private Button mLeaveGroup;
    private Button mDeleteGroup;
    private Button mOfflineMode;
    private Boolean mSticky;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        String action = getIntent().getAction();
        mSticky = (action != null) &&  action.contentEquals("android.intent.action.APPLICATION_PREFERENCES");

        mStatusText = findViewById(R.id.status_text);
        mGroupName = findViewById(R.id.group_name);

        mCreateGroup = findViewById(R.id.create_group);
        mCreateGroup.setOnClickListener(v -> {
            DataManagement.getInstance().createGroup();
            if (mSticky) {
                updateUI();
            } else {
                startActivity(MainActivity.getIntent(GroupActivity.this, ""));
            }
        });

        mJoinGroup = findViewById(R.id.join_group);
        mJoinGroup.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
            View content = getLayoutInflater().inflate(R.layout.join_group, null);
            builder.setTitle(getString(R.string.title_join_group))
                    .setView(content)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            EditText name = content.findViewById(R.id.memberName);
                            String email = name.getText().toString();
                            if (!email.isEmpty()) sendRequest(email);
                            dialog.dismiss();
                            updateUI();
                        }
                    })
                    .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

            builder.create().show();
        });

        mLeaveGroup = findViewById(R.id.leave_group);
        mLeaveGroup.setOnClickListener(v -> {
            DataManagement.getInstance().leaveGroup();
            updateUI();
        });

        mDeleteGroup = findViewById(R.id.delete_group);
        mDeleteGroup.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
            builder.setTitle(getString(R.string.title_delete_group))
                    .setMessage(R.string.delete_group_question)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DataManagement.getInstance().deleteGroup();
                            dialog.dismiss();
                            updateUI();
                        }
                    })
                    .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        });

        mOfflineMode = findViewById(R.id.offline_mode);
        mOfflineMode.setOnClickListener(v -> {
            startActivity(MainActivity.getIntent(GroupActivity.this, ""));
        });

        checkConnectivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        OwnRequestsObserver.getInstance().setOnAccessRequestAcceptedListener(new OwnRequestsObserver.OnAccessRequestAccepted() {
            @Override
            public Boolean handleRequest(String requestingUserPath) {
                if (mSticky) {
                    Toast.makeText(BasicApp.getContext(), R.string.request_accepted, Toast.LENGTH_SHORT).show();
                    updateUI();
                } else {
                    startActivity(MainActivity.getIntent(GroupActivity.this, ""));
                }
                return true;
            }
        });
        OwnRequestsObserver.getInstance().setOnAccessRequestRejectedListener(new OwnRequestsObserver.OnAccessRequestRejected() {
            @Override
            public Boolean handleRequest(String requestingUserPath) {
                Toast.makeText(BasicApp.getContext(), R.string.request_rejected, Toast.LENGTH_SHORT).show();
                updateUI();

                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        OwnRequestsObserver.getInstance().setOnAccessRequestAcceptedListener(null);
        OwnRequestsObserver.getInstance().setOnAccessRequestRejectedListener(null);
    }

    private void checkConnectivity() {
        super.showProgressDialog();
        try {
            new CheckConnectivityTask(BasicApp.getContext(),mCheckConnectivityCallback).execute("");
        } catch (Exception e) {  }
        super.hideProgressDialog();
    }


    private void sendRequest(String email) {
        super.showProgressDialog();
        try {
            new SendJoinRequestTask(BasicApp.getContext(),mSendJoinRequestCallback).execute(email);
        } catch (Exception e) {  }
        super.hideProgressDialog();
    }

    private void updateUI() {
        mStatusText.setText(DataManagement.getInstance().getStateText());
        String groupName = DataManagement.getEmailFromPath(DataManagement.getInstance().getGroupName());
        if (!groupName.isEmpty()) {
            mGroupName.setText(getString(R.string.current_group,groupName));
        } else {
            mGroupName.setText("");
        }
        switch (DataManagement.getInstance().getConnectivityState()) {
            case JOINED:
                mCreateGroup.setVisibility(View.GONE);
                mJoinGroup.setVisibility(View.GONE);
                mLeaveGroup.setVisibility(View.VISIBLE);
                mDeleteGroup.setVisibility(View.GONE);
                mOfflineMode.setVisibility(View.GONE);
                break;
            case LIVE:
                mCreateGroup.setVisibility(View.GONE);
                mJoinGroup.setVisibility(View.GONE);
                mLeaveGroup.setVisibility(View.GONE);
                mDeleteGroup.setVisibility(View.VISIBLE);
                mOfflineMode.setVisibility(View.GONE);
                break;
            case NO_VALID_GROUP: case REQUEST_PENDING:  case NOT_AUTHORIZED:
                mCreateGroup.setVisibility(View.VISIBLE);
                mJoinGroup.setVisibility(View.VISIBLE);
                mLeaveGroup.setVisibility(View.GONE);
                mDeleteGroup.setVisibility(View.GONE);
                if (mSticky) {
                    mOfflineMode.setVisibility(View.GONE);
                } else {
                    mOfflineMode.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private DataManagementTask.Callback mCheckConnectivityCallback = new DataManagementTask.Callback() {

        @Override
        public void onRequestComplete(DataManagementResult result) {
            updateUI();
            hideProgressDialog();
        }

        @Override
        public void onError(Exception e) {
            updateUI();
            hideProgressDialog();
        }
    };

    private DataManagementTask.Callback mSendJoinRequestCallback = new DataManagementTask.Callback() {
        @Override
        public void onRequestComplete(DataManagementResult result) {
            switch (result.getState()) {
                case JOINED: case LIVE:
                    if (!mSticky) startActivity(MainActivity.getIntent(GroupActivity.this, ""));
                    break;
                case NO_VALID_GROUP:
                    Toast.makeText(BasicApp.getContext(), R.string.unknown_group, Toast.LENGTH_SHORT).show();
                    break;
                case REQUEST_PENDING:
                    Toast.makeText(BasicApp.getContext(), R.string.request_sent, Toast.LENGTH_SHORT).show();
                    break;
                case NOT_AUTHORIZED:
                    Toast.makeText(BasicApp.getContext(), R.string.not_authorized, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onError(Exception e) {
            Toast.makeText(BasicApp.getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show();
        }
    };

}
