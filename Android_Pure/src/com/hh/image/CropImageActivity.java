package com.hh.image;

import com.hh.test.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


public class CropImageActivity extends Activity implements OnClickListener {
	
	private CropImageView mImageView;
	private Bitmap mBitmap;

	private CropImage mCrop;

	private Button mSave;
	private Button mCancel, rotateLeft, rotateRight;
	
	private String mPath = "";
	private int avatarSize;
	
	public int screenWidth = 0;
	public int screenHeight = 0;

	private ProgressBar mProgressBar;
	
	public static final int SHOW_PROGRESS = 2000;
	public static final int REMOVE_PROGRESS = 2001;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_PROGRESS:
				mProgressBar.setVisibility(View.VISIBLE);
				break;
			case REMOVE_PROGRESS:
				mHandler.removeMessages(SHOW_PROGRESS);
				mProgressBar.setVisibility(View.INVISIBLE);
				break;
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gl_modify_avatar);

		init();
	}

	protected void onStop() {
		super.onStop();
		if (mBitmap != null) {
			mBitmap = null;
		}
	}

	private void init() {
		getWindowWH();
		
		mPath = getIntent().getStringExtra("path");
		avatarSize = getIntent().getIntExtra("avatarSize", 320);
		
		mImageView = (CropImageView) findViewById(R.id.gl_modify_avatar_image);
		mSave = (Button) this.findViewById(R.id.gl_modify_avatar_save);
		mCancel = (Button) this.findViewById(R.id.gl_modify_avatar_cancel);
		rotateLeft = (Button) this.findViewById(R.id.gl_modify_avatar_rotate_left);
		rotateRight = (Button) this.findViewById(R.id.gl_modify_avatar_rotate_right);
		mSave.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		rotateLeft.setOnClickListener(this);
		rotateRight.setOnClickListener(this);
		try {
			mBitmap = createBitmap(mPath, screenWidth, screenHeight);
			if (mBitmap == null) {
				Toast.makeText(CropImageActivity.this, "娌℃湁鎵惧埌鍥剧墖", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				resetImageView(mBitmap);
			}
		} catch (Exception e) {
			Toast.makeText(CropImageActivity.this, "娌℃湁鎵惧埌鍥剧墖", Toast.LENGTH_SHORT).show();
			finish();
		}
		addProgressbar();
	}

	/**
	 * 鑾峰彇灞忓箷鐨勯珮鍜屽
	 */
	private void getWindowWH() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	private void resetImageView(Bitmap b) {
		mImageView.clear();
		mImageView.setImageBitmap(b);
		mImageView.setImageBitmapResetBase(b, true);
		mCrop = new CropImage(this, mImageView, mHandler);
		mCrop.crop(b);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gl_modify_avatar_cancel:
			finish();
			break;
		case R.id.gl_modify_avatar_save:
			Rect r = mCrop.getCropRect();
			int width = r.width();
			int height = r.height();
			if(width >= avatarSize) {
				try {
					Bitmap cropBitmap = mCrop.cropAndSave();
					
					float scaleWidth = (float)avatarSize / width;
			        float scaleHeight = (float)avatarSize / height;
			        Matrix matrix = new Matrix();
			        matrix.postScale(scaleWidth, scaleHeight);
			        Bitmap bitmap = Bitmap.createBitmap(cropBitmap, 0, 0, width, height, matrix, false);
					
		    		Intent intent = new Intent();
		    		intent.putExtra("path", mPath);
		    		intent.putExtra("bitmap", bitmap);
		    		setResult(RESULT_OK, intent);
					finish();
				} catch (Exception e) {
					Toast.makeText(CropImageActivity.this, "瑁佸壀鍑虹幇寮傚父锛岃閲嶈瘯", Toast.LENGTH_SHORT).show();
				}
			}
			else
				Toast.makeText(CropImageActivity.this, "鍥剧墖闇�澶т簬" + avatarSize + "x" + avatarSize, Toast.LENGTH_SHORT).show();
			break;
		case R.id.gl_modify_avatar_rotate_left:
			mCrop.startRotate(270.f);
			break;
		case R.id.gl_modify_avatar_rotate_right:
			mCrop.startRotate(90.f);
			break;
		}
	}

	protected void addProgressbar() {
		mProgressBar = new ProgressBar(this);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		addContentView(mProgressBar, params);
		mProgressBar.setVisibility(View.INVISIBLE);
	}

	public Bitmap createBitmap(String path, int w, int h) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			// 杩欓噷鏄暣涓柟娉曠殑鍏抽敭锛宨nJustDecodeBounds璁句负true鏃跺皢涓嶄负鍥剧墖鍒嗛厤鍐呭瓨銆�
			BitmapFactory.decodeFile(path, opts);
			int srcWidth = opts.outWidth;// 鑾峰彇鍥剧墖鐨勫師濮嬪搴�
			int srcHeight = opts.outHeight;// 鑾峰彇鍥剧墖鍘熷楂樺害
			int destWidth = 0;
			int destHeight = 0;
			// 缂╂斁鐨勬瘮渚�
			double ratio = 0.0;
			if (srcWidth < w || srcHeight < h) {
				ratio = 0.0;
				destWidth = srcWidth;
				destHeight = srcHeight;
			} else if (srcWidth > srcHeight) {// 鎸夋瘮渚嬭绠楃缉鏀惧悗鐨勫浘鐗囧ぇ灏忥紝maxLength鏄暱鎴栧鍏佽鐨勬渶澶ч暱搴�
				ratio = (double) srcWidth / w;
				destWidth = w;
				destHeight = (int) (srcHeight / ratio);
			} else {
				ratio = (double) srcHeight / h;
				destHeight = h;
				destWidth = (int) (srcWidth / ratio);
			}
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 缂╂斁鐨勬瘮渚嬶紝缂╂斁鏄緢闅炬寜鍑嗗鐨勬瘮渚嬭繘琛岀缉鏀剧殑锛岀洰鍓嶆垜鍙彂鐜板彧鑳介�氳繃inSampleSize鏉ヨ繘琛岀缉鏀撅紝鍏跺�艰〃鏄庣缉鏀剧殑鍊嶆暟锛孲DK涓缓璁叾鍊兼槸2鐨勬寚鏁板��
			newOpts.inSampleSize = (int) ratio + 1;
			// inJustDecodeBounds璁句负false琛ㄧず鎶婂浘鐗囪杩涘唴瀛樹腑
			newOpts.inJustDecodeBounds = false;
			// 璁剧疆澶у皬锛岃繖涓竴鑸槸涓嶅噯纭殑锛屾槸浠nSampleSize鐨勪负鍑嗭紝浣嗘槸濡傛灉涓嶈缃嵈涓嶈兘缂╂斁
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// 鑾峰彇缂╂斁鍚庡浘鐗�
			return BitmapFactory.decodeFile(path, newOpts);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

}