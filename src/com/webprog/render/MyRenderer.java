package com.webprog.render;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import com.webprog.MyEvent;
import com.webprog.util.PhysicsUtil;
import com.webprog.util.RenderUtil;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyRenderer extends GLSurfaceView implements GLSurfaceView.Renderer{
	private static Context mContext;

	private World mWorld;
	private float width, height;

	private Vector3f eye = new Vector3f(2f, 10f, 3f);
	private Vector3f look = new Vector3f(0f, 0f, 1f);
	private Vector3f up = new Vector3f(0, 0, 1);

	private Vector3f forbackVec = new Vector3f();
	private Vector3f sideVec = new Vector3f();

	private boolean isForward, isLeft, isRight, isBack;
	
	private float bgColor = 1.0f, bgColorB = 0.83f;

	public MyRenderer(Context context, AttributeSet attrs) {
		super(context, attrs);

		mContext = context;

		mWorld = new World(context);
		setRenderer(this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		MyEvent myEvent = MyEvent.getInstance();
		myEvent.onRenderEvent(event);
		
		return true;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		
		this.eyeTranslate();
		
		gl.glClearColor(bgColor, bgColor, bgColorB, 1.0f); // 昼夜
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glEnable(GL10.GL_DEPTH_TEST);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		GLU.gluLookAt(gl, eye.x, eye.y, eye.z, look.x, look.y, look.z, up.x, up.y, up.z);

		RenderUtil.enableMaterial(gl, false);

		mWorld.onDrawFrame(gl);
	}
	
	// eye位置の平行移動をする
	private void eyeTranslate(){
		if (isForward()) {
			this.forbackTranslate(true);
		}

		if (isLeft()) {
			this.sideTranslate(true);
		}

		if (isRight()) {
			this.sideTranslate(false);
		}

		if (isBack()) {
			this.forbackTranslate(false);
		}
	}

	// 前後へ移動するメソッド
	private void forbackTranslate(boolean forward){
		// eyeからlookへ向かう単位ベクトルを作成
		forbackVec.sub(look, eye);
		forbackVec.normalize();

		// ベクトルのスケーリングをする
		forbackVec.scale(0.15f);

		if(forward){
			setEye(forbackVec.x, forbackVec.y, 0f);
			setLook(forbackVec.x, forbackVec.y, 0f);
		}else {
			setEye(-forbackVec.x, -forbackVec.y, 0f);
			setLook(-forbackVec.x, -forbackVec.y, 0f);
		}
	}

	// 左右へ移動するメソッド
	private void sideTranslate(boolean left) {
		// eyeからlookへ向かう単位ベクトルを作成
		forbackVec.sub(look, eye);
		forbackVec.normalize();
		
		// 遠い方向へ向け、より正確なベクトルにする
		float farPlane = 10000f;
		forbackVec.scale(farPlane);
		
		// 上方向のベクトルとeyeからlookへ向かうベクトルの外積
		sideVec.cross(up, forbackVec);
		sideVec.normalize();
		sideVec.scale(0.15f);
	
		if(left){
			setEye(sideVec.x, sideVec.y, 0f);
			setLook(sideVec.x, sideVec.y, 0f);
		}else {
			setEye(-sideVec.x, -sideVec.y, 0f);
			setLook(-sideVec.x, -sideVec.y, 0f);
		}
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

	public static Context getMContext() {
		return mContext;
	}

	public void setDark() {
		bgColor = 0f;
		bgColorB = 0f;
	}

	public void setNoDark() {
		bgColor = 1.0f;
		bgColorB = 0.83f;
	}

	public void setEye(float x, float y, float z) {
		eye.x += x;
		eye.y += y;
		eye.z += z;
	}

	public void setLook(float x, float y, float z) {
		look.x += x;
		look.y += y;
		look.z += z;
	}

	public void setForward(boolean arg) {
		this.isForward = arg;
	}

	public void setLeft(boolean arg) {
		this.isLeft = arg;
	}

	public void setRight(boolean arg) {
		this.isRight = arg;
	}

	public void setBack(boolean arg) {
		this.isBack = arg;
	}

	public boolean isForward() {
		return isForward;
	}

	public boolean isLeft() {
		return isLeft;
	}

	public boolean isRight() {
		return isRight;
	}

	public boolean isBack() {
		return isBack;
	}
	
	public void shootCube(MotionEvent event){
		// タップ位置へキューブ弾を発射
		Vector3f point = PhysicsUtil.getRayTo((int) event.getX(), (int) event.getY(), eye, look,
				up, width, height);
		mWorld.shootInit(point, eye);
	}
	
	// eyeの周囲を回転するlookを求める
	public void lookRotation(double degH){
		
		// 現在の水平の視点の角度を極座標で求める
		double thetaH = Math.atan2(look.x - eye.x , look.y - eye.y);
		thetaH = convertRectRad(thetaH);
	
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
	
	/**
	 * 極座標θを直交座標の角度θにして返す
	 * 
	 * @param polarRad 極座標θのラジアン値
	 * @return ラジアン値
	 */
	private double convertRectRad(double polarRad){
		switch (getSign(polarRad)) {
		case 1:
			
			// 符号がプラス
			return Math.toRadians(450) - polarRad;
			
		case -1:
			
			// 符号がマイナス
			return Math.toRadians(90) + Math.abs(polarRad);
			
		default:
			
			break;
		}
		
		return 0;
	}
	
	// double値の符号を取得
	private int getSign(double n){
		if(n > 0){
			return 1;
		}else if (n < 0) {
			return -1;
		}else {
			return 0;
		}
	}
	
}
