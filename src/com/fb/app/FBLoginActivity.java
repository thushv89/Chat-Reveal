package com.fb.app;

import android.app.Activity;
import android.os.Bundle;

//This is supposed to prompt the user to login to fb
public class FBLoginActivity extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fb_login);
	}
}
