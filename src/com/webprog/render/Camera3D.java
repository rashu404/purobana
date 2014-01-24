package com.webprog.render;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLU;

import com.webprog.render.World.Grobal;
import com.webprog.tool.CustomMath;
import com.webprog.tool.RenderUtil;

public class Camera3D {
	private Context context;
	
	private Vector3f eye = new Vector3f(2f, 10f, 3f);
	private Vector3f look = new Vector3f();
	private Vector3f up = new Vector3f(0, 0, 1);
	
	private double lookRotRadius;
	
	private int moveAngle;
	private float moveAccelDecel;
	private boolean isMove;
	
	private float eyeAngleDegH = 250;
	private float eyeAngleDegV = 5;
	
	public Camera3D(Context context) {
		this.context = context;
		
		Vector3f ray = Grobal.tmpVec;
		ray.sub(look, eye);
		
		this.lookRotRadius = CustomMath.fastSqrt(
			(ray.x * ray.x)
			+ (ray.y * ray.y)
			+ (ray.z * ray.z)
		);
		
		int idxV = (int) this.eyeAngleDegV;
		int idxH = (int) this.eyeAngleDegH;
		
		Vector3f initLook = this.rotate3D(idxV, idxH);
		this.look.set(initLook);
	}
	
	public void moveEye(){
		if(this.isMove) { this.moveAccel(); } else { this.moveDecel(); }
	}
	
	private void moveAccel(){
		if(this.moveAccelDecel < 1) this.moveAccelDecel += 0.053f;
		this.moveEye(this.moveAngle);
	}
	
	private void moveDecel(){
		if(this.moveAccelDecel <= 0) return;
		this.moveAccelDecel -= 0.053f;
		this.moveEye(this.moveAngle);
	}
	
	// 指定した角度にカメラ位置を移動する
	private void moveEye(int moveAngleDeg){
		// eye位置から見たlook位置の角度
		double t = CustomMath.fastAtan2(look.x - eye.x, look.y - eye.y);
		t = CustomMath.convertAtan2To360AngRad(-t);
		
		int moveAngDeg = (int)(180/Math.PI*t) + moveAngleDeg;
		if(moveAngDeg > 360) moveAngDeg -= 360;
		
		float moveSpeed = getMoveSpeed();
			
		int idx = moveAngDeg;
		double sin = CustomMath.fastSin(idx);
		double cos = CustomMath.fastCos(idx);
		
		float x = (float)(moveSpeed * -cos);
		float y = (float)(moveSpeed * -sin);
		
		this.setEye(x, y, 0f);
		this.setLook(x, y, 0f);
	}
	
	// 解像度に合わせた移動速度を返す
	private float getMoveSpeed(){
		Point dispSize = RenderUtil.getSizeXY(this.context);
		double cos = CustomMath.fastCos(this.moveAngle);
		
		float moveSpeed = 0.0f;
		float dispAver = (float)dispSize.x / dispSize.y / 6;
		
		double val = Math.abs(cos);
		moveSpeed = (float) (dispAver + val / 13.5f);
				
		return moveSpeed * moveAccelDecel;
	}
	
	// eyeの周囲を回転するlookを求める
	public void lookRotation(float angDegH, float angDegV){
		this.eyeAngleDegH += angDegH;
		if(this.eyeAngleDegH > 360) this.eyeAngleDegH -= 360;
		if(this.eyeAngleDegH < 0) this.eyeAngleDegH += 360;
				
		this.eyeAngleDegV += angDegV;
		if(this.eyeAngleDegV > 55) this.eyeAngleDegV -= angDegV;
		if(this.eyeAngleDegV < 0) this.eyeAngleDegV = 0;
				
		int idxV = (int) this.eyeAngleDegV;
		int idxH = (int) this.eyeAngleDegH;
		
		Vector3f applyRotate = this.rotate3D(idxV, idxH);
		applyRotate.x += eye.x;
		applyRotate.y += eye.y;
		
		this.look.set(applyRotate);
	}
	
	private Vector3f rotate3D(int angVDeg, int angHDeg){
		Vector3f ret = Grobal.tmpVec;
		
		double sinV = CustomMath.fastSin(angVDeg);
		double cosV = CustomMath.fastCos(angVDeg);
		double sinH = CustomMath.fastSin(angHDeg);
		double cosH = CustomMath.fastCos(angHDeg);
		
		float x = (float) (this.lookRotRadius * cosV * cosH);
		float y = (float) (this.lookRotRadius * cosV * sinH);
		float z = (float) (this.lookRotRadius * sinV);
		
		ret.set(x, y, z);
		
		return ret;
	}
	
	public void gluLookAt(GL10 gl){
		GLU.gluLookAt(gl, eye.x, eye.y, eye.z, look.x, look.y, look.z, up.x, up.y, up.z);
	}
	
	public Vector3f getEye(){
		return this.eye;
	}
	
	public Vector3f getLook(){
		return this.look;
	}
	
	public Vector3f getUp(){
		return this.up;
	}
	
	public float getAngleDegH(){
		return this.eyeAngleDegH;
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
}
