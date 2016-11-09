package com.bear.bridge;

import java.io.File;
import java.util.Date;

import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.widget.Toast;

import com.hh.bean.GameBean;
import com.hh.image.CropImageActivity;
import com.hh.image.ModifyAvatarDialog;
import com.hh.image.PreviewActivity;

//本地功能代码
public class MMethod extends MNative {
	
	public static final int FLAG_CHOOSE_IMG = 5;
	public static final int FLAG_CHOOSE_PHONE = 6;
	public static final int FLAG_MODIFY_FINISH = 7;
	public static final int FLAG_MODIFY_PREVIEW = 8;
	
	public static final String ROOT_PATH = "poke";
	public static final String IMAGE_PATH = "Image";
	public static final File FILE_SDCARD = Environment.getExternalStorageDirectory();
	public static final File FILE_ROOT = new File(FILE_SDCARD, ROOT_PATH);
	public static final File FILE_IMAGE = new File(FILE_ROOT, IMAGE_PATH);
	public static String localTempImageFileName = "";
	
	public GameBean gameBean = new GameBean();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
	}
	
	public void initUpAvatarConfig(Bundle bundle) {
		gameBean.setUpAvatarQuality(bundle.getInt("quality"));
		gameBean.setUpAvatarSize(bundle.getInt("size"));
		gameBean.setPlayerId(bundle.getString("playerId"));
		gameBean.setUpAvatarUrl(bundle.getString("url"));
	}

	public void openUpAvatarDialog(Bundle bundle) {
		gameBean.setUpAvatarNum(bundle.getString("num"));
		gameBean.setUpAvatarSite(bundle.getString("site"));

		ModifyAvatarDialog modifyAvatarDialog = new ModifyAvatarDialog(this) {
			public void doGoToImg() {
				this.dismiss();
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, FLAG_CHOOSE_IMG);
			}

			public void doGoToPhone() {
				this.dismiss();
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) {
					try {
						localTempImageFileName = "";
						localTempImageFileName = String.valueOf((new Date()).getTime()) + ".png";
						File filePath = FILE_IMAGE;
						if (!filePath.exists()) {
							filePath.mkdirs();
						}
						Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						File f = new File(filePath, localTempImageFileName);
						Uri u = Uri.fromFile(f);
						intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
						startActivityForResult(intent, FLAG_CHOOSE_PHONE);
					} catch (ActivityNotFoundException e) {
					}
				}
			}
		};
		String title = "请选择上传图片方式";
		AlignmentSpan span = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);
		AbsoluteSizeSpan span_size = new AbsoluteSizeSpan(25, true);
		SpannableStringBuilder spannable = new SpannableStringBuilder();
		spannable.append(title);
		spannable.setSpan(span, 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(span_size, 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		modifyAvatarDialog.setTitle(spannable);
		modifyAvatarDialog.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FLAG_CHOOSE_IMG && resultCode == RESULT_OK) {
			if (data != null) {
				Uri uri = data.getData();
				if (uri != null && uri.getAuthority() != null && !TextUtils.isEmpty(uri.getAuthority())) {
					Cursor cursor = getContentResolver().query(uri, new String[] { MediaStore.Images.Media.DATA }, null, null, null);
					if (null == cursor) {
						showLongToast("图片获取异常，请重试");
						return;
					}
					cursor.moveToFirst();
					String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();

					if (path != null && !path.isEmpty()) {
						Intent intent = new Intent(this, CropImageActivity.class);
						intent.putExtra("path", path);
						intent.putExtra("avatarSize", gameBean.getUpAvatarSize());
						startActivityForResult(intent, FLAG_MODIFY_FINISH);
					} else {
						showLongToast("图片获取异常，请重试");
					}
				} else {
					Intent intent = new Intent(this, CropImageActivity.class);
					intent.putExtra("path", uri.getPath());
					intent.putExtra("avatarSize", gameBean.getUpAvatarSize());
					startActivityForResult(intent, FLAG_MODIFY_FINISH);
				}
			}
		} else if (requestCode == FLAG_CHOOSE_PHONE && resultCode == RESULT_OK) {
			File f = new File(FILE_IMAGE, localTempImageFileName);
			Intent intent = new Intent(this, CropImageActivity.class);
			intent.putExtra("path", f.getAbsolutePath());
			startActivityForResult(intent, FLAG_MODIFY_FINISH);
		} else if (requestCode == FLAG_MODIFY_FINISH && resultCode == RESULT_OK) {
			if (data != null) {
				Intent intent = new Intent(this, PreviewActivity.class);
				intent.putExtra("playerId", gameBean.getPlayerId());
				intent.putExtra("avatarQuality", gameBean.getUpAvatarQuality());
				intent.putExtra("avatarUrl", gameBean.getUpAvatarUrl());
				intent.putExtra("avatarNum", gameBean.getUpAvatarNum());
				intent.putExtra("avatarSite", gameBean.getUpAvatarSite());
				intent.putExtra("path", data.getStringExtra("path"));
				intent.putExtra("bitmap", data.getParcelableExtra("bitmap"));
				startActivityForResult(intent, FLAG_MODIFY_PREVIEW);
			}
		} else if (requestCode == FLAG_MODIFY_PREVIEW && resultCode == RESULT_CANCELED) {
			if (data != null) {
				Intent intent = new Intent(this, CropImageActivity.class);
				intent.putExtra("path", data.getStringExtra("path"));
				intent.putExtra("avatarSize", gameBean.getUpAvatarSize());
				startActivityForResult(intent, FLAG_MODIFY_FINISH);
			}
		} else if (requestCode == FLAG_MODIFY_PREVIEW && resultCode == RESULT_OK) {
			nativeIsUpAvatarSuccess();
		}
	}
	
	public void showLongToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	public void showShortToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
