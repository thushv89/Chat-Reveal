package com.fb.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkHandler {

	Context context;
	NetworkInfo activeNetworkInfo;
	ConnectivityManager connectivityManager;
	
	public NetworkHandler(Context context){
		activeNetworkInfo=null;
		this.context=context;
		connectivityManager 
        = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	public boolean isNetworkAvailable() {    
	    activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
	
	public NetworkInfo getNetworkinfo(){
		activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo;
	}
	
	public Boolean getIsConnected(){
		return activeNetworkInfo.isConnected();
	}
}
