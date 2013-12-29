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
import com.webprog.render.World.Grobal;
import com.webprog.tool.MathUtil;
import com.webprog.tool.PhysicsUtil;
import com.webprog.tool.RenderUtil;

public final class MyRenderer extends GLSurfaceView implements GLSurfaceView.Renderer{
	private Context context;

	private World world;

	private Myself myself;
	
	private Vector3f eye = new Vector3f(2f, 10f, 3f);
	private Vector3f look = new Vector3f();
	private Vector3f up = new Vector3f(0, 0, 1);
	
	private int moveAngle;
	private float moveAccelDecel;
	private boolean isMove;
	
	private float eyeAngleDegH = 250;
	private float eyeAngleDegV = 5;
	
	
	public MyRenderer(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;
		this.world = new World(context);
		
		DynamicsWorld dynamicsWorld = world.getDynamicsWorld();
		this.myself = new Myself(dynamicsWorld, eye);
		
		MyEvent me = MyEvent.getInstance();
		double[] sinCache = me.getSinCache();
		double[] cosCache = me.getCosCache();
		
		Vector3f ray = Grobal.tmpVec;
		ray.sub(look, eye);
		
		this.lookRotRadius = Math.sqrt(
			(ray.x * ray.x)
			+ (ray.y * ray.y)
			+ (ray.z * ray.z)
		);
		
		int idxV = (int) this.eyeAngleDegV;
		int idxH = (int) this.eyeAngleDegH;
		float x = (float) (this.lookRotRadius * cosCache[idxV] * cosCache[idxH]) + eye.x;
		float y = (float) (this.lookRotRadius * cosCache[idxV] * sinCache[idxH]) + eye.y;
		float z = (float) (this.lookRotRadius * sinCache[idxV]);
		
		this.look.set(x, y, z);
		
		this.setRenderer(this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {		
		MyEvent.getInstance().onRendererTouch(event, this);
		return true;
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		if(this.isMove) {
			if(this.moveAccelDecel < 1.0f) this.moveAccelDecel += 0.05f;
			this.moveEye(this.moveAngle);
		}else{
			if(this.moveAccelDecel > 0.0f){
				this.moveAccelDecel -= 0.05f;
				this.moveEye(this.moveAngle);
			}
		}
		
		if(this.world.isDark()){
			gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		}else {
			gl.glClearColor(0.525f, 0.7f, 0.9f, 1.0f);
		}
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glEnable(GL10.GL_DEPTH_TEST);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		GLU.gluLookAt(gl, eye.x, eye.y, eye.z, look.x, look.y, look.z, up.x, up.y, up.z);
		
		this.myself.sync(gl, eye);

		this.world.onDrawFrame(gl);
	}
	
	// 指定した角度にカメラ位置を移動する
	public void moveEye(int moveAngleDeg){
		// eye位置から見たlook位置の角度
		double t = Math.atan2(look.x - eye.x, look.y - eye.y);
		t = MathUtil.convertAtan2To360AngRad(-t);
		
		int moveAngDeg = (int)(180/Math.PI*t) + moveAngleDeg;
		if(moveAngDeg > 360) moveAngDeg -= 360;
		
		float moveSpeed = getMoveSpeed();
		
		MyEvent me = MyEvent.getInstance();
		double[] sinAngleRadCache = me.getSinCache();
		double[] cosAngleRadCache = me.getCosCache(); 
		
		int idx = moveAngDeg;
		float x = (float)(moveSpeed * -cosAngleRadCache[idx]);
		float y = (float)(moveSpeed * -sinAngleRadCache[idx]);
		
		this.setEye(x, y, 0f);
		this.setLook(x, y, 0f);
	}
	
	// 解像度に合わせた移動速度を返す
	private float getMoveSpeed(){
		Point dispSize = RenderUtil.getSizeXY(this.context);
		double[] cosCache = MyEvent.getInstance().getCosCache();
		
		float moveSpeed = 0.0f;
		float dispAver = (float)dispSize.x / dispSize.y / 6;
		
		double val = 0;
		if((val = cosCache[this.moveAngle]) < 0){
			val = -val;
		}
		moveSpeed = (float) (dispAver + (val / 13.5));
				
		return moveSpeed * moveAccelDecel;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		float mFovy = 75;

		gl.glViewport(0, 0, width, height);
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		GLU.gluPerspective(gl, mFovy, (float) width / height, 1.f, 1000.f);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		this.world.worldInit(gl, context);
	}
	
	public World getWorld(){
		return this.world;
	}

	private void setEye(float x, float y, float z) {
		this.eye.x += x;
		this.eye.y += y;
		this.eye.z += z;
	}

	private void setLook(float x, float y, float z) {
		this.look.x += x;
		this.look.y += y;
		this.look.z += z;
	}
	
	public void setMoveAngle(int moveAngle){
		this.moveAngle = moveAngle;
	}
	
	public void setMove(boolean isMove){
		this.isMove = isMove;
	}
	
	public void setMoveAccelDecel(float moveAccelDecel){
		this.moveAccelDecel = moveAccelDecel;
	}

	// タップ位置へキューブ弾を発射
	public void shootCube(MotionEvent event){
		Vector3f point = PhysicsUtil.getRayTo(
				(int) event.getX(), (int) event.getY(), eye, look, up, getWidth(), getHeight());
		this.world.shootCube(point, eye);
	}
	
	private double lookRotRadius;
	
	// eyeの周囲を回転するlookを求める
	public void lookRotation(float angDegH, float angDegV){
		MyEvent me = MyEvent.getInstance();
		double[] sinCache = me.getSinCache();
		double[] cosCache = me.getCosCache();
		
		this.eyeAngleDegH += angDegH;
		if(this.eyeAngleDegH > 360) this.eyeAngleDegH -= 360;
		if(this.eyeAngleDegH < 0) this.eyeAngleDegH += 360;
				
		this.eyeAngleDegV += angDegV;
		if(this.eyeAngleDegV > 48) this.eyeAngleDegV -= angDegV;
		if(this.eyeAngleDegV < 0) this.eyeAngleDegV = 0;
				
		int idxV = (int) this.eyeAngleDegV;
		int idxH = (int) this.eyeAngleDegH;
		float x = (float) (this.lookRotRadius * cosCache[idxV] * cosCache[idxH]) + eye.x;
		float y = (float) (this.lookRotRadius * cosCache[idxV] * sinCache[idxH]) + eye.y;
		float z = (float) (this.lookRotRadius * sinCache[idxV]);
		
		this.look.set(x, y, z);
	}

}
