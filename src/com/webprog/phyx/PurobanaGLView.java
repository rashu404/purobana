package com.webprog.phyx;

import com.webprog.phyx.objects.Ground;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class PurobanaGLView extends GLSurfaceView {
	MyRenderer mRenderer;
	Ground mGround;
	boolean b = true;

	public PurobanaGLView(Context context) {
		super(context);
		mRenderer = new MyRenderer(context);
		setRenderer(mRenderer);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		queueEvent(new Runnable() {
			public void run() {
			}
		});
		switch (ev.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			// mRenderer.shootInit();
			/* 固定位置へキューブ弾を発射 */

			// mRenderer.darkSwitch();
			/* 昼夜の切り替え */

			// mRenderer.translateX(true);
			/* 電車風景のようにカメラの平行移動 */

			// Vector3f point = mRenderer.getRayTo((int)ev.getX(),
			// (int)ev.getY());
			// mRenderer.shootInit(point);
			/* タッチ位置へキューブ弾を発射 */

			mRenderer.fallingSwitch(b);
			b = !b;
			/* タッチでランダム地点にキューブ雨を降らせる */

			// Log.d("VecPoint", "X=" + point.x + "_Y=" + point.y + "_Z=" +
			// point.z);
			// Log.d("IntPoint", "X=" + (int)ev.getX() + "_Y=" +
			// (int)ev.getY());
			break;

		case MotionEvent.ACTION_UP:
			// mRenderer.translateX(false);
			/* 電車風平行移動の静止 */

			break;
		}

		// Log.d("MotionEvent", "X= " + ev.getX() + "_Y= " + ev.getY());

		return true;
	}
}
