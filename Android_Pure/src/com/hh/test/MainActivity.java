package com.hh.test;

import com.unity3d.player.UnityPlayerActivity;

import android.os.Bundle;

public class MainActivity extends NativeSDK {

	public static final String LOG_TAG = "Test_Unity";
	
	private static MainActivity actInstance;

	public static MainActivity getInstance() {
		return actInstance;
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		actInstance = this;
	}
}
