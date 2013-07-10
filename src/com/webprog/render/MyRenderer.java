package com.webprog.render;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import com.webprog.utils.PhysicsUtils;
import com.webprog.utils.RenderUtils;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyRenderer extends GLSurfaceView implements GLSurfaceView.Renderer {
	private Context mContext;

	private World mWorld;
	private float width, height;

	private final Vector3f eye = new Vector3f(2f, 10f, 3f);
	private final Vector3f look = new Vector3f(0f, 0f, 1f);
	private final Vector3f up = new Vector3f(0, 0, 1);

	private float mDistance = 50f;
	private float mAngleH = 5f;
	private float mAngleV = 10f;

	private float mAngleDiff = 0.1f;
	
	private boolean b;

	public MyRenderer(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;

		mWorld = new World(context);
		setRenderer(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			// mWorld.shootInit();
			/* 固定位置へキューブ弾を発射 */

			// mRenderer.darkSwitch();
			/* 昼夜の切り替え */

			// mRenderer.translateX(true);
			/* 電車風景のようにカメラの平行移動 */
			
			Vector3f point = PhysicsUtils.getRayTo((int)event.getX(), (int)event.getY(), eye, look, up, width, height);
			mWorld.shootInit(point, eye);
			/* タッチ位置へキューブ弾を発射 */

			//mWorld.fallingSwitch(b);
			//b = !b;
			/* タッチでランダム地点にキューブ雨を降らせる */

			break;

		case MotionEvent.ACTION_UP:
			// mRenderer.translateX(false);
			/* 電車風平行移動の静止 */

			break;

		}

		return false;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		setAngleRotate();

		gl.glClearColor(0f, 0f, 0f, 1.0f); // 夜
		// gl.glClearColor(bgColor, bgColor, bgColorB, 1.0f); //昼
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glEnable(GL10.GL_DEPTH_TEST);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		GLU.gluLookAt(gl, eye.x, eye.y, eye.z, look.x, look.y, look.z, up.x, up.y, up.z);

		RenderUtils.enableMaterial(gl, false);
		
		//gl.glRotatef(rotate, 0, 0, 1);
		//rotate += 0.3f;

		mWorld.onDrawFrame(gl);

	}

	private void setAngleRotate() {
		mAngleV += mAngleDiff;
		if (mAngleV >= 20.0f) {
			mAngleDiff = -mAngleDiff;
		} else if (mAngleV <= 2.0f) {
			mAngleDiff = -mAngleDiff;
		}
		
		mAngleH += 0.5f;
		
		
		double angV = Math.toRadians(mAngleV);
		double angH = Math.toRadians(mAngleH);
		eye.x = (float) (mDistance * Math.cos(angV) * Math.cos(angH));
		eye.y = (float) (mDistance * Math.cos(angV) * Math.sin(angH));
		eye.z = (float) (mDistance * Math.sin(angV));
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		float mFovy = 90;

		this.width = (float) width;
		this.height = (float) height;

		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		GLU.gluPerspective(gl, mFovy, (float) width / height, 1.f, 1000.f);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		mWorld.worldInit(gl, mContext);

	}

}
