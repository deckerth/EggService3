package com.deckerth.thomas.eggservice.firebase;

import android.content.Context;

public class CheckConnectivityTask extends DataManagementTask  {

    public CheckConnectivityTask (Context context, DataManagementTask.Callback callback) {
        super(context,callback);
    }

    @Override
    protected DataManagementResult doInBackground(String... strings) {
        CheckConnectivityJob job = new CheckConnectivityJob();
        while (true) {
            switch (job.getJobState()) {
                case FAILED:
                    return job.getResult();
                case COMPLETE:
                    return job.getResult();
                case IN_PROGRESS:
                    /*try {
                        wait(100);
                    } catch (InterruptedException e) { }*/
            }
        }
    }
}
