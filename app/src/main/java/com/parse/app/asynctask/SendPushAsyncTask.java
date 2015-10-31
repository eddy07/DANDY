package com.parse.app.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.parse.app.R;
import com.parse.app.proxy.IdjanguiProxyException;
import com.parse.app.proxy.IdjanguiProxyImpl;
import com.parse.app.utilities.NetworkUtil;

public class SendPushAsyncTask extends AsyncTask<Void, Void, Boolean> {
	
    private String userId;
    private String channel;
    private String alert;
    private String title;
    private String status;
    private ProgressDialog progressDialog;
	private IdjanguiProxyImpl IdjanguiProxy = IdjanguiProxyImpl.getInstance();
	private Activity activity;
	public static int TYPE_NOT_CONNECTED = 0;
	
	public SendPushAsyncTask(String userId, String channel, String title, String alert, Activity activity) {
		super();
		this.userId = userId;
		this.channel = channel;
		this.title = title;
		this.alert = alert;
		this.activity = activity;
	}
	
	private void showProgress(boolean show){
        progressDialog  = new ProgressDialog(activity.getApplicationContext());
        progressDialog.setMessage("sending ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        if(show == true) progressDialog.show();
        else progressDialog.dismiss();
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		showProgress(true);
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {

		
		try {
			if(NetworkUtil.getConnectivityStatus(activity.getApplicationContext())==TYPE_NOT_CONNECTED){
				Toast.makeText(activity.getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
			}else{
				status = IdjanguiProxy.sendPush(userId,channel,alert,title);
            }
			
		} catch (IdjanguiProxyException e) {

			e.printStackTrace();
		}
		if(status.isEmpty() || status==null || status == "push_fail")
			return false;
		else
			return true;
	}
	
	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);
		showProgress(false);

		if (success) {
            Toast.makeText(activity.getApplicationContext(), "Push send", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(activity.getApplicationContext(), "Fail to send", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected void onCancelled() {

		super.onCancelled();
		showProgress(false);
	}

}
