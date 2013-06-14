package com.webprog.phyx;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.webprog.phyx.manager.GrobalVariables;
import com.webprog.phyx.objects.World;
import com.webprog.phyx.objects.GLObjectInterface;
import com.webprog.phyx.utils.Utils;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

class MyRenderer implements GLSurfaceView.Renderer {

	World mWorld;

	public MyRenderer(Context context) {
		GrobalVariables.mContext = context;
		mWorld = new World();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// gl.glClearColor(0f, 0f, 0f, 1.0f); //夜
		gl.glClearColor(GrobalVariables.bgColor, GrobalVariables.bgColor,
				GrobalVariables.bgColorB, 1.0f); // 昼
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glEnable(GL10.GL_DEPTH_TEST);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		if (GrobalVariables.posSwitch == false)
			GLU.gluLookAt(gl, GrobalVariables.eyeX, GrobalVariables.eyeY,
					GrobalVariables.eyeZ, GrobalVariables.lookX,
					GrobalVariables.lookY, GrobalVariables.lookZ,
					GrobalVariables.upX, GrobalVariables.upY,
					GrobalVariables.upZ);
		else
			GLU.gluLookAt(gl, GrobalVariables.mCubePos.x + 2,
					GrobalVariables.mCubePos.y + 2,
					GrobalVariables.mCubePos.z + 2, GrobalVariables.mCubePos.x,
					GrobalVariables.mCubePos.y, GrobalVariables.mCubePos.z,
					GrobalVariables.upX, GrobalVariables.upY,
					GrobalVariables.upZ);

		mWorld.draw(gl);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {// widthとheight逆かも
		float mFovy = 90;

		GrobalVariables.width = (float) width;
		GrobalVariables.height = (float) height;

		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		GLU.gluPerspective(gl, mFovy, (float) GrobalVariables.width
				/ GrobalVariables.height, 1.f, 1000.f);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
		for (GLObjectInterface mObject : GrobalVariables.mObjects) {
			mObject.init(gl, GrobalVariables.mContext);
		}
		Utils.enableLight(gl);

		GrobalVariables.gl10 = gl;
	}

	public void fallingSwitch(boolean rainSwitch) {
		GrobalVariables.rainSwitch = rainSwitch;
	}
}