package com.hh.image;

import java.io.ByteArrayOutputStream;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.hh.test.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.Base64;
import com.loopj.android.http.RequestParams;

public class PreviewActivity extends Activity {

	private Bitmap mBitmap;
	private ImageView iv_avatar;
	private String mPath;
	private String photo = "";
	
	private String playerId;
	private int avatarQuality;
	private String avatarUrl;
	private String avatarNum;
	private String avatarSite;
	
	protected FlippingLoadingDialog mLoadingDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gl_modify_preview);

		mLoadingDialog = new FlippingLoadingDialog(this);

		playerId = getIntent().getStringExtra("playerId");
		avatarQuality = getIntent().getIntExtra("avatarQuality", 80);
		avatarUrl = getIntent().getStringExtra("avatarUrl");
		avatarNum = getIntent().getStringExtra("avatarNum");
		avatarSite = getIntent().getStringExtra("avatarSite");
		mPath = getIntent().getStringExtra("path");
		mBitmap = getIntent().getParcelableExtra("bitmap");

		iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
		iv_avatar.setImageBitmap(mBitmap);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_up:
			up();
			break;
		case R.id.btn_cancel:
    		Intent intent = new Intent();
    		intent.putExtra("path", mPath);
    		setResult(RESULT_CANCELED, intent);
    		finish();
			break;
		}
	}

	public void up() {
		try {
			showLoadingDialog("上传中，请稍后...");
			
			if (photo.equals("")) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				mBitmap.compress(Bitmap.CompressFormat.JPEG, avatarQuality, baos);
				baos.close();
				byte[] buffer = baos.toByteArray();
				photo = Base64.encodeToString(buffer, 0, buffer.length, Base64.DEFAULT);
			}

			if (photo != null && !photo.equals("")) {
				AsyncHttpClient client = new AsyncHttpClient();
				RequestParams params = new RequestParams();
				params.put("playerId", playerId);
				params.put("img", avatarNum);
				params.put("site", avatarSite);
				params.put("photo", photo);

				client.post(avatarUrl, params, new AsyncHttpResponseHandler() {

					public void onSuccess(int statusCode, Header[] headers, byte[] response) {
						dismissLoadingDialog();
						
						Toast.makeText(PreviewActivity.this, "上传成功", Toast.LENGTH_LONG).show();
						
			    		Intent intent = new Intent();
			    		setResult(RESULT_OK, intent);
			    		finish();
					}

					public void onFailure(int statusCode, Header[] headers, byte[] response, Throwable e) {
						dismissLoadingDialog();

						Toast.makeText(PreviewActivity.this, "上传失败，请重试", Toast.LENGTH_LONG).show();
					}

				});
			} else
				Toast.makeText(PreviewActivity.this, "图片处理失败，请重试", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showLoadingDialog(String text) {
		mLoadingDialog.setText(text);
		mLoadingDialog.setCanceledOnTouchOutside(false);
		mLoadingDialog.setCancelable(false);
		mLoadingDialog.show();
	}

	public void dismissLoadingDialog() {
		if (mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
	}	
	
}