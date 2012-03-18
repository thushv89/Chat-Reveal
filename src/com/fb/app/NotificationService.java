package com.fb.app;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.fb.app.R;
import com.fb.util.AppUtil;
import com.fb.util.FacebookHandler;
import com.fb.util.Presence;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

public class NotificationService extends Service {

	FacebookHandler fbHandler;
	Facebook fb;
	NotificationManager nm;
	Notification nfOnline;
	Notification nfOffline;
	Calendar time=Calendar.getInstance();
	Long counter=0L;
	Timer timer=new Timer();
	String[] frnd_uid=null;
	String[] frnd_name=null;
	Bitmap frnd_pic=null;
	Boolean offlineNotfShown=false;
	RemoteViews rView;
	ArrayList<String>friendsOnlineIDs;
	ArrayList<String>friendsOnlineNames;
	Context context;
	int friendOnlineNotification=251;
	@Override
	public void onCreate() {
		super.onCreate();
		friendsOnlineIDs=new ArrayList<String>();
		friendsOnlineNames=new ArrayList<String>();
		nm=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Toast.makeText(this,"Service created at "+time.getTime(),Toast.LENGTH_LONG).show();
		//rView=new RemoteViews(getPackageName(),R.layout.custom_notification);
		
	}

	private void incrementCounter() {
		timer.scheduleAtFixedRate(new TimerTask(){ 
			public void run() {
				//counter++;
				//Toast.makeText(getApplicationContext(), String.valueOf(AppUtil.FRIEND_ONLINE),Toast.LENGTH_SHORT).show();
				friendsOnlineIDs.clear();
				friendsOnlineNames.clear();
				friendOnlineNotification=251;
				if(frnd_uid!=null && !"".equals(frnd_uid[0])){
					for(int i=0;i<frnd_uid.length;i++){
						if(checkOnlineStatus(frnd_uid[i])){
							friendsOnlineIDs.add(frnd_uid[i]);
							friendsOnlineNames.add(frnd_name[i]);
						}
					}
				}
				if(friendsOnlineIDs.size()>0){
					offlineNotfShown=false;
					for(int i=friendOnlineNotification;i>251;i--){
						nm.cancel(252);
					}
					nm.cancel(251);
					
					for(int i=0;i<friendsOnlineIDs.size();i++){
						showChatOnlineNotification(friendsOnlineNames.get(i));
					}
					
				}else{	//if no friends are online
					
					if(!offlineNotfShown){
						nm.cancel(253);
						nm.cancel(252);
						showChatOfflineNotification();
					}
					offlineNotfShown=true;
				}
			}}, 100, AppUtil.QUERY_INTERVAL);
	}

	private void showAppOnlineNotification(){
		CharSequence text="Service Started";
		Notification nf=new Notification(R.drawable.app_icon, text, System.currentTimeMillis());
		PendingIntent contentIntn=PendingIntent.getActivity(this,0,new Intent(this,ChatRevealActivity.class), 0);		
		nf.setLatestEventInfo(this, "App online",text, contentIntn);
		nm.notify(250, nf);
		
		
	}
	private void showChatOnlineNotification(String name) {
		CharSequence text=name;
		Notification nfOnline=new Notification(R.drawable.online_chat, text, System.currentTimeMillis());
		PendingIntent contentIntn=PendingIntent.getActivity(this,0,new Intent(this,FBLoginActivity.class), 0);		
		nfOnline.setLatestEventInfo(this,"Online",text, contentIntn);
		friendOnlineNotification++;
		nm.notify(friendOnlineNotification, nfOnline);
	}
	private void showChatOfflineNotification() {
		CharSequence text="Offline";
		Notification nfOffline=new Notification(R.drawable.offline_chat, text, System.currentTimeMillis());
		PendingIntent contentIntn=PendingIntent.getActivity(this,0,new Intent(this,ChatRevealActivity.class), 0);		
		nfOffline.setLatestEventInfo(this, "None are onlne",text, contentIntn);
		nm.notify(251, nfOffline);
	}

	@Override
	public void onDestroy(){
		Toast.makeText(this, "Service Terminated", Toast.LENGTH_LONG).show();
		nm.cancelAll();
		super.onDestroy();
	}
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId)
	{
		super.onStartCommand(intent, flags, startId);

		Bundle data = intent.getExtras();
		if(data!=null){
			frnd_uid=data.getStringArray("friend_uid");
			frnd_name=data.getStringArray("friend_name");
			//byte[] img=data.getByteArray("friend_pic");
			//frnd_pic = BitmapFactory.decodeByteArray(img, 0, img.length);
		}
		
		showAppOnlineNotification();
		incrementCounter();
		return 0;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	public Boolean checkOnlineStatus(String uid){
		Bundle params=new Bundle();
		params.putString("method", "fql.query");
		params.putString("query","SELECT uid, name, online_presence FROM user WHERE uid = "+uid);
		String response="";
		String name="";

		String onlinePresence="";
		try {
			response=AppUtil.FB.request(params);
			response = "{\"data\":" + response + "}";

			JSONObject json = Util.parseJson( response );
			JSONArray data = json.getJSONArray( "data" );

			for ( int i = 0, size = data.length(); i < size; i++ ){
				JSONObject friend = data.getJSONObject( i );

				onlinePresence = friend.getString( "online_presence" );
				name = friend.getString( "name" );

			}
						
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FacebookError e) {
			Toast.makeText(getApplicationContext(), "Unable to fulfill the request", Toast.LENGTH_LONG);
		}
		if("active".equals(onlinePresence.toLowerCase())){
			AppUtil.FRIEND_ONLINE=true;
			AppUtil.ONLINE_PRESENCE=Presence.ONLINE;
			return true;
		}else if("idle".equals(onlinePresence.toLowerCase())){
			AppUtil.FRIEND_ONLINE=true;
			AppUtil.ONLINE_PRESENCE=Presence.IDLE;
			return true;
		}else{
			AppUtil.FRIEND_ONLINE=false;
			AppUtil.ONLINE_PRESENCE=Presence.OFFLINE;
			return false;
		}
	
	}
}
