package com.bear.bridge;

import com.hh.test.MainActivity;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

//本地原生与unity通信
public class MNative extends UnityPlayerActivity {

	public static final String GameObject = "Main Camera";
	public static final String AvatarUpSuccMethod = "UpAvatarBack";
	public static final String NativeLoginMethod = "NativeLoginBack";
	public static final String PayBackMethod = "NativePayBack";

	public void nativeIsUpAvatarSuccess() {
		//UnityPlayer.UnitySendMessage(GameObject, AvatarUpSuccMethod, "头像上传成功");
	}
	
	public void nativeLogin(String json) {
		//UnityPlayer.UnitySendMessage(GameObject, NativeLoginMethod, json);
	}
	
	public void nativeRechargeBack(String status) {
		//UnityPlayer.UnitySendMessage(GameObject, PayBackMethod, status);
	}

	public static final int INIT = 0x0001;
	public static final int LOGIN = 0x0002;
	public static final int LOGOUT = 0x0003;
	public static final int EXIT = 0x0004;
	public static final int PAY = 0x0005;
	public static final int INIT_UP_AVATAR_CONFIG = 0x0010;
	public static final int OPEN_UP_ACATAR_DIALOG = 0x0011;

	private static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case INIT:
				UnityPlayer.UnitySendMessage(GameObject, AvatarUpSuccMethod, "初始化成功");
				break;
			case LOGIN:
				//MainActivity.getInstance().login(msg.getData());
				UnityPlayer.UnitySendMessage(GameObject, NativeLoginMethod, "登录成功");
				break;
			case LOGOUT:
				UnityPlayer.UnitySendMessage(GameObject, AvatarUpSuccMethod, "登出成功");
				break;
			case EXIT:
				UnityPlayer.UnitySendMessage(GameObject, AvatarUpSuccMethod, "退出成功");
				break;
			case PAY:
				//MainActivity.getInstance().pay(msg.getData());
				UnityPlayer.UnitySendMessage(GameObject, PayBackMethod, "支付成功");
				break;
			case INIT_UP_AVATAR_CONFIG:
				MainActivity.getInstance().initUpAvatarConfig(msg.getData());
				break;
			case OPEN_UP_ACATAR_DIALOG:
				MainActivity.getInstance().openUpAvatarDialog(msg.getData());
				break;
			default:
				break;
			}
		}
	};

	public static void sendHandlerMessage(int what) {
		if (handler == null)
			return;

		handler.sendEmptyMessage(what);
	}

	public static void sendHandlerMessage(int what, Bundle bundle) {
		if (handler == null)
			return;

		Message msg = new Message();
		msg.setData(bundle);
		msg.what = what;
		handler.sendMessage(msg);
	}

	public static void initUpAvatarConfig(int quality, int size,
			String playerId, String url) {
		Bundle bundle = new Bundle();
		bundle.putInt("quality", quality);
		bundle.putInt("size", size);
		bundle.putString("playerId", playerId);
		bundle.putString("url", url);
		sendHandlerMessage(INIT_UP_AVATAR_CONFIG, bundle);
	}

	public static void openUpAvatarDialog(String num, String site) {
		Bundle bundle = new Bundle();
		bundle.putString("num", num);
		bundle.putString("site", site);
		sendHandlerMessage(OPEN_UP_ACATAR_DIALOG, bundle);
	}
	
	public static void initSDK() {
		Bundle bundle = new Bundle();
		sendHandlerMessage(INIT, bundle);
	}

	public static void login(int platform) {
		Bundle bundle = new Bundle();
		bundle.putInt("platform", platform);
		sendHandlerMessage(LOGIN, bundle);
	}

	public static void logout() {
		Bundle bundle = new Bundle();
		sendHandlerMessage(LOGOUT, bundle);
	}

	public static void exit() {
		Bundle bundle = new Bundle();
		sendHandlerMessage(EXIT, bundle);
	}
	
	public static void pay(String json) {
		Bundle bundle = new Bundle();
		bundle.putString("json", json);
		sendHandlerMessage(PAY, bundle);
	}
}