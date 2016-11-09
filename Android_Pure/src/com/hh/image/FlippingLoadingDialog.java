package com.hh.image;

import com.hh.test.R;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

public class FlippingLoadingDialog extends Dialog {

	private FlippingImageView mFivIcon;
	private TextView mHtvText;
	private String mText;

	public FlippingLoadingDialog(Context context) {
		super(context, R.style.Theme_Light_FullScreenDialogAct);
		init();
	}

	private void init() {
		setContentView(R.layout.common_flipping_loading_diloag);
		mFivIcon = (FlippingImageView) findViewById(R.id.loadingdialog_fiv_icon);
		mHtvText = (TextView) findViewById(R.id.loadingdialog_htv_text);
		mFivIcon.startAnimation();
		mHtvText.setText(mText);
	}

	public void setText(String text) {
		mText = text;
		mHtvText.setText(mText);
	}

	public void dismiss() {
		if (isShowing())
			super.dismiss();
	}

}