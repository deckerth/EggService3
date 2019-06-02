package com.deckerth.thomas.eggservice.views;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.deckerth.thomas.eggservice.firebase.DataManagement;
import com.deckerth.thomas.eggservice.firebase.PendingRequestObserver;
import com.deckerth.thomas.eggservice.persistency.DataRepository;
import com.deckerth.thomas.eggservice.R;
import com.deckerth.thomas.eggservice.databinding.ActivityMainBinding;
import com.deckerth.thomas.eggservice.viewmodel.MemberListViewModel;

public class MainActivity extends AppCompatActivity {

    public static MainActivity Current;

    private Boolean mEggCookMode = Boolean.FALSE;
    private MemberListViewModel mViewModel;
    private ActivityMainBinding mBinding;
    private Boolean mIsIdle = Boolean.TRUE;
    private TimerFragment timerFragment = null;
    private MemberListFragment memberListFragment = null;

    public final static String EXTRA_PATH = "MainActivity_Path";

    public static Intent getIntent(Context context, String path) {
        Intent filesIntent = new Intent(context, MainActivity.class);
        filesIntent.putExtra(MainActivity.EXTRA_PATH, path);
        return filesIntent;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        if (mEggCookMode) {
                            mEggCookMode = Boolean.FALSE;
                            if (memberListFragment == null) memberListFragment = new MemberListFragment();

                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                    .replace(R.id.fragment_container, memberListFragment).commit();
                        }
                        return true;

                    case R.id.navigation_cook_eggs:
                        if (!mEggCookMode) {
                            mEggCookMode = Boolean.TRUE;
                            if (timerFragment == null) {
                                timerFragment = new TimerFragment();
                            }

                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                                    .replace(R.id.fragment_container, timerFragment).commit();
                        }
                        return true;
                }
                return false;
            };

    public String getStringFromResource(int id){
       return getString(id);
    }

    public void setIdle(Boolean isIdle) {
        mIsIdle = isIdle;
        if (isIdle) PendingRequestObserver.getInstance().handleNextRequest();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Current = this;

        //setContentView(R.layout.activity_main);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        mViewModel = ViewModelProviders.of(this).get(MemberListViewModel.class);

        subscribeUi(mViewModel);

        // Add list fragment if this is first creation
        if (savedInstanceState == null) {
            MemberListFragment fragment = new MemberListFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, MemberListFragment.TAG).commit();
        }
        PendingRequestObserver.getInstance().setOnAccessRequestArrivedListener(mOnAccessRequestArrived);
        DataRepository.getPersistency().loadMembers();
    }

    private void subscribeUi(MemberListViewModel viewModel) {
        viewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean myIsLoading) {
                if (myIsLoading != null) {
                    mBinding.setIsLoading(myIsLoading);
                }
                mBinding.executePendingBindings();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.action_info) {
            Intent settingsIntent = new Intent(this, InfoActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataRepository.getPersistency().loadMembers();
    }

    PendingRequestObserver.OnAccessRequestArrived mOnAccessRequestArrived = requestingUserPath -> {
        if (mIsIdle) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
            View content = getLayoutInflater().inflate(R.layout.join_group_request, null);
            TextView name = content.findViewById(R.id.memberName);
            name.setText(DataManagement.getEmailFromPath(requestingUserPath));
            builder.setTitle(getString(R.string.title_join_group_request))
                    .setView(content)
                    .setPositiveButton(R.string.yes, (dialog, id) -> {
                        DataManagement.getInstance().acceptRequest(requestingUserPath);
                        DataManagement.getInstance().removeRequest(requestingUserPath);
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.no, (dialog, id) -> {
                        DataManagement.getInstance().removeRequest(requestingUserPath);
                        dialog.dismiss();
                    })
                    .setCancelable(true)
                    .setOnCancelListener(dialog -> {
                        PendingRequestObserver.getInstance().addRequest(requestingUserPath);
                        dialog.dismiss();
                    });
            builder.create().show();
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    };
}
