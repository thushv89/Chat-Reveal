package com.fb.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.AsyncFacebookRunner.RequestListener;


public class FriendHandler {


	Boolean gotFriends=false;
	Context context;
	ArrayList<String> friendNames=new ArrayList<String>();
	ArrayList<String> friendIDs=new ArrayList<String>();
	
	ProgressDialog mProgress;
	AsyncFacebookRunner mAsyncRunner;
	
	public FriendHandler(Context context){
		this.context=context;
		mAsyncRunner=new AsyncFacebookRunner(AppUtil.FB);
		mProgress=new ProgressDialog(context);
	}
	
	//get the friends available 
	public void getFriends(){
		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgress.setMessage("Loading friends...");
		mProgress.setCancelable(true);

		mProgress.show();
		mAsyncRunner.request("me/friends",new FriendListRequestListener());		
	}
	
	public ArrayList<String> getFriendListNames(){
		return friendNames;
	}
	public ArrayList<String> getFriendListIDs(){
		return friendIDs;
	}
	
	//get profile pic of a specific user
	public Bitmap getProfPic(String uid){
		Bitmap icon=null;
		try {
			if(!"".equals(uid) && uid!=null){
				AppUtil.FRIEND_IMG_URL = new URL("http://graph.facebook.com/"+uid+"/picture?type=large");
				icon = BitmapFactory.decodeStream(AppUtil.FRIEND_IMG_URL.openConnection().getInputStream());
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		return icon;
	}
	
	//When the request for the friend list is made, it fires certain events
	//what to do in those events are included in the following code.
	class FriendListRequestListener implements RequestListener{

		@Override
		public void onComplete(String response, Object state) {
			// TODO Auto-generated method stub
			try {
				JSONObject friends=Util.parseJson(response);
				JSONArray friendsArray=friends.getJSONArray("data");
				
				for(int i=0;i<friendsArray.length();i++){
					JSONObject friend=friendsArray.getJSONObject(i);
					friendNames.add(friend.getString("name"));
					friendIDs.add(friend.getString("id"));
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (FacebookError e) {
				e.printStackTrace();
			}
			mProgress.dismiss();
		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			
		}

		//No such file
		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		//Invalid URL
		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			Toast.makeText(context, "Invalid request", Toast.LENGTH_LONG);
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
