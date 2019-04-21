package com.deckerth.thomas.eggservice.firebase;

import android.content.Context;
import android.os.AsyncTask;

public abstract class DataManagementTask extends AsyncTask<String, Void, DataManagementResult> {

    private final Context mContext;
    private final DataManagementTask.Callback mCallback;
    private Exception mException;

    public interface Callback {
        void onRequestComplete(DataManagementResult result);
        void onError(Exception e);
    }

    public DataManagementTask() {
        mCallback = null;
        mContext = null;
    }

    public DataManagementTask (Context context, DataManagementTask.Callback callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(DataManagementResult result) {
        super.onPostExecute(result);
        if (mCallback != null ){
            if (mException != null) {
                mCallback.onError(mException);
            } else if (result == null) {
                mCallback.onError(null);
            } else {
                mCallback.onRequestComplete(result);
            }
        }
    }
}
