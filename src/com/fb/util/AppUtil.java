package com.fb.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.facebook.android.Facebook;



public class AppUtil {

	//public static List<String> FRIENDS=new ArrayList<String>();
	//public static List<String> FRIENDS_ID=new ArrayList<String>();
	public static boolean FRIEND_ONLINE=false;
	public static URL FRIEND_IMG_URL;
	public static Presence ONLINE_PRESENCE=Presence.OFFLINE;
	public static int QUERY_INTERVAL=30000;
	public static String API_KEY="301400543256301";
	public static Facebook FB=new Facebook(API_KEY);
	
}
