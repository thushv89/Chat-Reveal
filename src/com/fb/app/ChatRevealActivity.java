package com.fb.app;

import java.util.ArrayList;

import com.fb.net.NetworkHandler;
import com.fb.util.AppUtil;
import com.fb.util.FacebookHandler;
import com.fb.util.FriendHandler;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ChatRevealActivity extends Activity {

	MultiAutoCompleteTextView friendsET;
	Button exitB;
	ToggleButton activateTB;
	FacebookHandler fbHandler;
	FriendHandler friendHandler;
	Intent notifyIntent;
	NetworkHandler nwHandler;

	ArrayList<String> frndNames;
	ArrayList<String> frndIDs;

	
	ServiceConnection mConnection=new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			Toast.makeText(getApplicationContext(), "Error occured while Service Bind", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			//startService(notifyIntent);
			Toast.makeText(getApplicationContext(), "Service bind successfully", Toast.LENGTH_LONG).show();			
		}
	};
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		nwHandler=new NetworkHandler(this);	//This handles all the network concerns
		NetworkInfo nwInfo=nwHandler.getNetworkinfo();	//get the information about user's internect connectivity

		//if there's a connection continue
		if(nwInfo!=null && nwInfo.isAvailable()){
			frndNames=new ArrayList<String>();
			frndIDs=new ArrayList<String>();

			fbHandler=new FacebookHandler(this);
			fbHandler.ssoInitialAuth();	//get Single Sign
			fbHandler.getFBPermission();	//get permission to check user's friend details
			fbHandler.getAccessToken();	//get Access token which allows to do so

			friendHandler=new FriendHandler(this);	
			friendHandler.getFriends();	//get all the friends available

			initialize();

			frndNames=friendHandler.getFriendListNames();
			frndIDs=friendHandler.getFriendListIDs();
		}else{
			Toast.makeText(this, "You are not connected to Internet", Toast.LENGTH_LONG).show();
		}
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		AppUtil.FB.authorizeCallback(requestCode, resultCode, data);
	}

	//this method initiate the views on android activity
	private void initialize(){
		friendsET=(MultiAutoCompleteTextView)findViewById(R.id.friendsET);
		friendsET.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,friendHandler.getFriendListNames());
		friendsET.setAdapter(adapter);

		exitB=(Button)findViewById(R.id.exitB);
		exitB.setOnClickListener(new ButtonHandler());

		activateTB=(ToggleButton)findViewById(R.id.activateTB);
		activateTB.setOnClickListener(new ButtonHandler());
	}

	//this includes what to do when exit and activate buttons are clicked
	class ButtonHandler implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(v.getId()==exitB.getId()){
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				stopService(notifyIntent);	//stop the notification service
				startActivity(intent);

			}else if(v.getId()==activateTB.getId()){
				if(activateTB.isChecked()){
					String[] names=friendsET.getText().toString().split(",");
					String[] ids=new String[names.length-1];
					for(int i=0;i<ids.length && ids.length>0;i++){
						String name=names[i].toString().trim();
						int idx=frndNames.indexOf(name);
						ids[i]=frndIDs.get(idx);
					}
					
					notifyIntent=new Intent(getApplicationContext(),NotificationService.class);
					notifyIntent.putExtra("friend_uid", ids);
					//notifyIntent.putExtra("friend_pic", friendHandler.getProfPic(frnd_id));
					notifyIntent.putExtra("friend_name", names);
					//boolean result=bindService(notifyIntent, mConnection, BIND_AUTO_CREATE);
					startService(notifyIntent);	//start notification service
				}else{
					stopService(new Intent(getApplicationContext(),NotificationService.class));
				}
			}
		}

	}
}