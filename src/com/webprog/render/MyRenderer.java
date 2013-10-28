package com.webprog.render;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.webprog.MyEvent;
import com.webprog.objects.Myself;
import com.webprog.util.*;

public final class MyRenderer extends GLSurfaceView implements GLSurfaceView.Renderer{
	private Context mContext;

	private World mWorld;
	private float width, height;

	private Myself mOneself;
	
	private Vector3f eye = new Vector3f(2f, 10f, 3f);
	private Vector3f look = new Vector3f(0f, 0f, 1f);
	private Vector3f up = new Vector3f(0, 0, 1);
	
	private int moveAngle;
	
	private boolean isMove;
	
	public MyRenderer(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;
		mWorld = new World(context);
		
		DynamicsWorld dynamicsWorld = mWorld.getDynamicsWorld();
		mOneself = new Myself(dynamicsWorld, eye);
				
		setRenderer(this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		MyEvent myEvent = MyEvent.getInstance();
		myEvent.onRenderEvent(event, this);
		
		return true;
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		if(isMove) moveEye(moveAngle);
		
		if(mWorld.isDark()){
			gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		}else {
			gl.glClearColor(0.525f, 0.7f, 0.9f, 1.0f);
		}
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glEnable(GL10.GL_DEPTH_TEST);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		GLU.gluLookAt(gl, eye.x, eye.y, eye.z, look.x, look.y, look.z, up.x, up.y, up.z);
		
		mOneself.sync(gl, eye);

		mWorld.onDrawFrame(gl);
	}
	
	// 指定した角度にカメラ位置を移動する
	public void moveEye(int degTheta){
		// eye位置から見たlook位置の角度
		double t = Math.atan2(look.x - eye.x, look.y - eye.y);
		t = MathUtil.convertSinCosRad(t);
		
		// 角度tからdegTheta度回転した角度
		double theta = t + Math.toRadians(degTheta);
		
		// 移動スピード
		float speed = getMoveSpeed(degTheta);
		
		// 加算演算するXとYの値
		float x = (float)(speed * Math.cos(theta));
		float y = (float)(speed * Math.sin(theta));
		
		setEye(x, y, 0f);
		setLook(x, y, 0f);
	}
	
	// 解像度に合わせたθ度の移動速度を返す
	private float getMoveSpeed(int degTheta){
		Point size = RenderUtil.getSizeXY(mContext);
		
		double radTheta = Math.toRadians(degTheta);
		float speed = 0.0f;
		if(size.x < size.y){
			float disp = (float)size.y / size.x / 6;
			speed = (float) ((float)disp - Math.abs(Math.sin(radTheta) / 13.5));
		}else {
			float disp = (float)size.x / size.y / 8;
			speed = (float) ((float)disp + Math.abs(Math.sin(radTheta)/ 13.5));
		}
		
		return speed;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		float mFovy = 75;

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
	
	public World getWorld(){
		return mWorld;
	}

	private void setEye(float x, float y, float z) {
		eye.x += x;
		eye.y += y;
		eye.z += z;
	}

	private void setLook(float x, float y, float z) {
		look.x += x;
		look.y += y;
		look.z += z;
	}
	
	public void setMoveAngle(int moveAngle){
		this.moveAngle = moveAngle;
	}
	
	public void setMove(boolean isMove){
		this.isMove = isMove;
	}

	// タップ位置へキューブ弾を発射
	public void shootCube(MotionEvent event){
		Vector3f point = PhysicsUtil.getRayTo(
				(int) event.getX(), (int) event.getY(), eye, look, up, width, height);
		mWorld.shootCube(point, eye);
	}
	
	// eyeの周囲を回転するlookを求める
	public void lookRotation(double degH){
		// 現在の水平の視点の角度を極座標で求める
		double thetaH = Math.atan2(look.x - eye.x , look.y - eye.y);
		thetaH = MathUtil.convertSinCosRad(thetaH);
	
		// eyeからlookまでの水平の距離
		double rH = Math.sqrt(Math.pow(look.x - eye.x, 2) + Math.pow(look.y - eye.y, 2));	
		
		// 角度を加算
		thetaH += Math.toRadians(degH);
		
		// lookのX座標とY座標を求める
		double x = rH * Math.cos(thetaH) + eye.x;
		double y = rH * Math.sin(thetaH) + eye.y;
		
		look.x = (float) x;
		look.y = (float) y;
	}
	
}
