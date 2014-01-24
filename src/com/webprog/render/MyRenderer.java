package com.webprog.render;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.webprog.MyEvent;
import com.webprog.PhysxWorldActivity;
import com.webprog.objects.Myself;
import com.webprog.tool.FPSCounter;
import com.webprog.tool.PhysicsUtil;
import com.webprog.ui.FieldMap;

public final class MyRenderer extends GLSurfaceView implements GLSurfaceView.Renderer, Runnable{
	private Context context;

	private World world;
	private Myself myself;
	private FieldMap fieldMap;
	private Camera3D camera3D;
	
	private FPSCounter fpsCounter = new FPSCounter();
	
	public MyRenderer(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;
		this.world = new World();
		
		this.camera3D = new Camera3D(this.context);
		
		DynamicsWorld dynamicsWorld = world.getDynamicsWorld();
		this.myself = new Myself(dynamicsWorld, camera3D.getEye());
		
		this.setRenderer(this);
		new FieldMapHandler().startSyncMap();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		MyEvent.getInstance().onRendererTouch(event, this);
		return true;
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		this.camera3D.moveEye();
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		this.camera3D.gluLookAt(gl);
		
		this.myself.sync(gl, camera3D.getEye(), camera3D.getLook());
		
		this.world.onDraw(gl);
		
		this.fpsCounter.logFrame();
	}
	
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		float fovy = 75;

		gl.glViewport(0, 0, width, height);
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		GLU.gluPerspective(gl, fovy, (float) width / height, 1.f, 1000.f);
		
		gl.glEnable(GL10.GL_DEPTH_TEST);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		this.world.worldInit(gl, context);
		
		this.fieldMap = PhysxWorldActivity.getFieldMap();
	}
	
	public World getWorld(){
		return this.world;
	}

	public Camera3D getCamera3D(){
		return this.camera3D;
	}
	
	public void setFieldMap(FieldMap fieldMap){
		this.fieldMap = fieldMap;
	}

	// タップ位置へキューブ弾を発射
	public void shootCube(MotionEvent event){
		Vector3f eye = camera3D.getEye();
		Vector3f look = camera3D.getLook();
		Vector3f up = camera3D.getUp();
		
		int x = (int) event.getX();
		int y = (int) event.getY();
		
		int width = getWidth();
		int height = getHeight();
		
		Vector3f point = PhysicsUtil.getRayTo(x, y, eye, look, up, width, height);
		this.world.shootCube(point, eye);
	}
	
	@Override
	public void run() {
		this.world.darkSwitch();
	}

	private class FieldMapHandler extends Handler implements Runnable{
		private void startSyncMap(){
			this.postDelayed(this, 1000);
		}
		
		@Override
		public void run() {
			fieldMap.syncMap(camera3D, world);
			this.postDelayed(this, 10);
		}
		
	}
}
