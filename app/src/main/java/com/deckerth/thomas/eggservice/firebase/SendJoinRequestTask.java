package com.deckerth.thomas.eggservice.firebase;

import android.content.Context;

import static com.deckerth.thomas.eggservice.firebase.DataManagement.getPathFromEmail;

public class SendJoinRequestTask extends DataManagementTask  {

    public SendJoinRequestTask (Context context, DataManagementTask.Callback callback) {
        super(context,callback);
    }

    @Override
    protected DataManagementResult doInBackground(String... strings) {
        String email = strings[0];
        String group = getPathFromEmail(email);
        SendJoinRequestJob job = new SendJoinRequestJob(email);
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
