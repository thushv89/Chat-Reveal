package com.fb.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

public class FacebookHandler {

	private Activity authActivity;
	private Context context;
	SharedPreferences mPrefs;
	
	public FacebookHandler(Activity activity){
		authActivity=activity;
		context=authActivity.getBaseContext();	
	}	

	public Context getContext(){
		return context;
	}
	public void ssoInitialAuth(){
		AppUtil.FB.authorize(authActivity, new DialogListener() {
			@Override
			public void onComplete(Bundle values) {}

			@Override
			public void onFacebookError(FacebookError error) {}

			@Override
			public void onError(DialogError e) {}

			@Override
			public void onCancel() {}
		});
	}
	
	public void getFBPermission(){
		AppUtil.FB.authorize(authActivity, new String[] { "friends_about_me", "friends_online_presence"},

				new DialogListener() {
			@Override
			public void onComplete(Bundle values) {}

			@Override
			public void onFacebookError(FacebookError error) {}

			@Override
			public void onError(DialogError e) {}

			@Override
			public void onCancel() {}
		}
		);
	}
	
	public void getAccessToken(){
		mPrefs=authActivity.getPreferences(authActivity.MODE_PRIVATE);
		String accessToken=mPrefs.getString("access_token", null);
		long tokenExpires=mPrefs.getLong("access_expires", 0);
		if(accessToken != null) {
			AppUtil.FB.setAccessToken(accessToken);
		}
		if(tokenExpires != 0) {
			AppUtil.FB.setAccessExpires(tokenExpires);
		}

		if(!AppUtil.FB.isSessionValid()){
			AppUtil.FB.authorize(authActivity,new String[] {},new DialogListener() {

				@Override
				public void onFacebookError(FacebookError e) {
					Toast.makeText(context, "Request could not be fulfilled. Please try again.", Toast.LENGTH_LONG).show();
				}

				@Override
				public void onError(DialogError e) {
					Toast.makeText(context, "Error has occured.", Toast.LENGTH_LONG).show();
				}

				@Override
				public void onComplete(Bundle values) {
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putString("access_token",AppUtil.FB.getAccessToken());
					editor.putLong("access_expires", AppUtil.FB.getAccessExpires());
					editor.commit();
				}

				@Override
				public void onCancel() {

				}
			});
		}
	}
}
