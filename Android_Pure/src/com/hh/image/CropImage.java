package com.hh.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.os.Environment;
import android.os.Handler;

/**
 * 裁剪处理
 * 
 */
public class CropImage {

	public static final float IMAGE_SCALE = 320;
	
	public static final File FILE_SDCARD = Environment.getExternalStorageDirectory();

	public static final File FILE_LOCAL = new File(FILE_SDCARD, "weixin");

	public boolean mWaitingToPick; // Whether we are wait the user to pick a face.
	public boolean mSaving; // Whether the "save" button is already clicked.
	public HighlightView mCrop;

	private Context mContext;
	private Handler mHandler;
	private CropImageView mImageView;
	private Bitmap mBitmap;

	public CropImage(Context context, CropImageView imageView, Handler handler) {
		mContext = context;
		mImageView = imageView;
		mImageView.setCropImage(this);
		mHandler = handler;
	}

	/**
	 * 图片裁剪
	 */
	public void crop(Bitmap bm) {
		mBitmap = bm;
		startFaceDetection();
	}

	public void startRotate(float d) {
		if (((Activity) mContext).isFinishing()) {
			return;
		}
		final float degrees = d;
		showProgressDialog("请稍等", new Runnable() {
			public void run() {
				final CountDownLatch latch = new CountDownLatch(1);
				mHandler.post(new Runnable() {
					public void run() {
						try {
							Matrix m = new Matrix();
							m.setRotate(degrees);
							Bitmap tb = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), m, false);
							mBitmap = tb;
							mImageView.resetView(tb);
							if (mImageView.mHighlightViews.size() > 0) {
								mCrop = mImageView.mHighlightViews.get(0);
								mCrop.setFocus(true);
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
						latch.countDown();
					}
				});
				try {
					latch.await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				// mRunFaceDetection.run();
			}
		}, mHandler);
	}

	private void startFaceDetection() {
		if (((Activity) mContext).isFinishing()) {
			return;
		}
		showProgressDialog("请稍后", new Runnable() {
			public void run() {
				final CountDownLatch latch = new CountDownLatch(1);
				final Bitmap b = mBitmap;
				mHandler.post(new Runnable() {
					public void run() {
						if (b != mBitmap && b != null) {
							mImageView.setImageBitmapResetBase(b, true);
							mBitmap.recycle();
							mBitmap = b;
						}
						if (mImageView.getScale() == 1.0f) {
							mImageView.center(true, true);
						}
						latch.countDown();
					}
				});
				try {
					latch.await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				mRunFaceDetection.run();
			}
		}, mHandler);
	}

	/**
	 * 裁剪并保存
	 * 
	 * @return
	 */
	public Bitmap cropAndSave() {
		final Bitmap bmp = onSaveClicked(mBitmap);
//		mImageView.mHighlightViews.clear();
		return bmp;
	}

	/**
	 * 裁剪并保存
	 * 
	 * @return
	 */
	public Bitmap cropAndSave(Bitmap bm) {
		final Bitmap bmp = onSaveClicked(bm);
		mImageView.mHighlightViews.clear();
		return bmp;
	}

	/**
	 * 取消裁剪
	 */
	public void cropCancel() {
		mImageView.mHighlightViews.clear();
		mImageView.invalidate();
	}
	
	public Rect getCropRect() {
		Rect r = mCrop.getCropRect();
		return r;
	}
	
	private Bitmap onSaveClicked(Bitmap bm) {
		if (mSaving)
			return bm;

		if (mCrop == null)
			return bm;

		mSaving = true;

		Rect r = mCrop.getCropRect();
		int width = r.width();
		int height = r.height();
		Bitmap croppedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		{
			Canvas canvas = new Canvas(croppedImage);
			Rect dstRect = new Rect(0, 0, width, height);
			canvas.drawBitmap(bm, r, dstRect, null);
		}
		
		return croppedImage;
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

	public String saveToLocal(Bitmap bm) {
		String path = FILE_LOCAL + "mm7.jpg";
		try {
			FileOutputStream fos = new FileOutputStream(path);
			bm.compress(CompressFormat.JPEG, 80, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return path;
	}

	private void showProgressDialog(String msg, Runnable job, Handler handler) {
		// final ProgressDialog progress = ProgressDialog.show(mContext, null,
		// msg);
		// new Thread(new BackgroundJob(progress, job, handler)).start();
		new Thread(new BackgroundJob(msg, job, handler)).start();
	}

	Runnable mRunFaceDetection = new Runnable() {
		float mScale = 1F;
		Matrix mImageMatrix;
		FaceDetector.Face[] mFaces = new FaceDetector.Face[3];
		int mNumFaces;

		// For each face, we create a HightlightView for it.
		@SuppressWarnings("unused")
		private void handleFace(FaceDetector.Face f) {
			PointF midPoint = new PointF();

			int r = ((int) (f.eyesDistance() * mScale)) * 2;
			f.getMidPoint(midPoint);
			midPoint.x *= mScale;
			midPoint.y *= mScale;

			int midX = (int) midPoint.x;
			int midY = (int) midPoint.y;

			HighlightView hv = new HighlightView(mImageView);

			int width = mBitmap.getWidth();
			int height = mBitmap.getHeight();

			Rect imageRect = new Rect(0, 0, width, height);

			RectF faceRect = new RectF(midX, midY, midX, midY);
			faceRect.inset(-r, -r);
			if (faceRect.left < 0) {
				faceRect.inset(-faceRect.left, -faceRect.left);
			}

			if (faceRect.top < 0) {
				faceRect.inset(-faceRect.top, -faceRect.top);
			}

			if (faceRect.right > imageRect.right) {
				faceRect.inset(faceRect.right - imageRect.right, faceRect.right - imageRect.right);
			}

			if (faceRect.bottom > imageRect.bottom) {
				faceRect.inset(faceRect.bottom - imageRect.bottom, faceRect.bottom - imageRect.bottom);
			}

			hv.setup(mImageMatrix, imageRect, faceRect, false, true);

			mImageView.add(hv);
		}

		// Create a default HightlightView if we found no face in the picture.
		private void makeDefault() {
			HighlightView hv = new HighlightView(mImageView);

			int width = mBitmap.getWidth();
			int height = mBitmap.getHeight();

			Rect imageRect = new Rect(0, 0, width, height);

			// CR: sentences!
			// make the default size about 4/5 of the width or height
			int cropWidth = Math.min(width, height) * 4 / 5;
			int cropHeight = cropWidth;

			int x = (width - cropWidth) / 2;
			int y = (height - cropHeight) / 2;

			RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
			hv.setup(mImageMatrix, imageRect, cropRect, false, true);
			mImageView.add(hv);
		}

		// Scale the image down for faster face detection.
		private Bitmap prepareBitmap() {
			if (mBitmap == null) {
				return null;
			}

			// 256 pixels wide is enough.
			if (mBitmap.getWidth() > 256) {
				mScale = 256.0F / mBitmap.getWidth(); // CR: F => f (or change all f to F).
			}
			Matrix matrix = new Matrix();
			matrix.setScale(mScale, mScale);
			Bitmap faceBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
			return faceBitmap;
		}

		public void run() {
			mImageMatrix = mImageView.getImageMatrix();
			Bitmap faceBitmap = prepareBitmap();

			mScale = 1.0F / mScale;
			if (faceBitmap != null) {
				FaceDetector detector = new FaceDetector(faceBitmap.getWidth(), faceBitmap.getHeight(), mFaces.length);
				mNumFaces = detector.findFaces(faceBitmap, mFaces);
			}

			if (faceBitmap != null && faceBitmap != mBitmap) {
				faceBitmap.recycle();
			}

			mHandler.post(new Runnable() {
				public void run() {
					mWaitingToPick = mNumFaces > 1;
					// if (mNumFaces > 0) {
					// // for (int i = 0; i < mNumFaces; i++) {
					// for (int i = 0; i < 1; i++) {
					// handleFace(mFaces[i]);
					// }
					// } else {
					makeDefault();
					// }
					mImageView.invalidate();
					if (mImageView.mHighlightViews.size() > 0) {
						mCrop = mImageView.mHighlightViews.get(0);
						mCrop.setFocus(true);
					}

					if (mNumFaces > 1) {
						// CR: no need for the variable t. just do
						// Toast.makeText(...).show().
						// Toast t = Toast.makeText(mContext,
						// R.string.multiface_crop_help, Toast.LENGTH_SHORT);
						// t.show();
					}
				}
			});
		}
	};

	class BackgroundJob implements Runnable {
		// private ProgressDialog mProgress;
		@SuppressWarnings("unused")
		private String message;
		private Runnable mJob;
		private Handler mHandler;

		// public BackgroundJob(ProgressDialog progress, Runnable job, Handler
		// handler)
		// {
		// mProgress = progress;
		// mJob = job;
		// mHandler = handler;
		// }
		public BackgroundJob(String m, Runnable job, Handler handler) {
			message = m;
			mJob = job;
			mHandler = handler;
			// mProgress = new ProgressDialog(mContext);
			// mProgress.setMessage(message);
			// mProgress.show();
		}

		public void run() {
			final CountDownLatch latch = new CountDownLatch(1);
			mHandler.post(new Runnable() {
				public void run() {
					try {
						mHandler.sendMessage(mHandler.obtainMessage(CropImageActivity.SHOW_PROGRESS));
						// if (mProgress != null && !mProgress.isShowing())
						// {
						// mProgress.show();
						// }
					} catch (Exception e) {
						// TODO: handle exception
					}

					latch.countDown();
				}
			});
			try {
				latch.await();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			try {
				mJob.run();
			} finally {
				mHandler.sendMessage(mHandler.obtainMessage(CropImageActivity.REMOVE_PROGRESS));
				// mHandler.post(new Runnable()
				// {
				// public void run()
				// {
				// if (mProgress != null && mProgress.isShowing())
				// {
				// mProgress.dismiss();
				// mProgress = null;
				// }
				// }
				// });
			}
		}
	}
}
