package com.deckerth.thomas.eggservice.firebase;

import android.content.Context;
import android.os.AsyncTask;

public class GetMemberDatabaseTask extends AsyncTask<Void, Void, GetMemberDatabaseResult> {

    private final Context mContext;
    private final Callback mCallback;
    private Exception mException;

    public interface Callback {
        void onGetComplete(GetMemberDatabaseResult result);
        void onError(Exception e);
    }

    public GetMemberDatabaseTask(Context context, Callback callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(GetMemberDatabaseResult result) {
        super.onPostExecute(result);
        if (mException != null) {
            mCallback.onError(mException);
        } else if (result == null) {
            mCallback.onError(null);
        } else {
            mCallback.onGetComplete(result);
        }
    }

    @Override
    protected GetMemberDatabaseResult doInBackground(Void... voids) {
        GetMemberDatabaseJob job = new GetMemberDatabaseJob();
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
