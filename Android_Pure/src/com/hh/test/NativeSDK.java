package com.hh.test;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bear.bridge.MMethod;

public class NativeSDK extends MMethod {
	
	public static ProgressDialog mAutoLoginWaitingDlg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
	}
	
	public void login(Bundle bundle)
	{
	}
	
	public void pay(Bundle bundle)
	{
	}
	
	// 平台授权成功,让用户进入游戏. 由游戏自己实现登录的逻辑
    public void letUserLogin() {
    }
	
	// 登出后, 更新view. 由游戏自己实现登出的逻辑
    public void letUserLogout() {
    }
	
	public void stopWaiting() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(MainActivity.LOG_TAG,"stopWaiting");
                if (mAutoLoginWaitingDlg != null && mAutoLoginWaitingDlg.isShowing()) {
                    mAutoLoginWaitingDlg.dismiss();
                }
            }
        });

    }

    public void showToastTips(String tips) {
        Toast.makeText(this,tips,Toast.LENGTH_LONG).show();
    }
}
