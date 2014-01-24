package com.webprog;

import android.graphics.Canvas;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.webprog.render.Camera3D;
import com.webprog.render.MyRenderer;
import com.webprog.render.World;
import com.webprog.tool.CustomMath;
import com.webprog.ui.AnalogStick;

public final class MyEvent {
	private static MyEvent instance;

	private long touchDownTime;
	private float lastX, lastY;
	
	private MyEvent() {
	}
	
	public void onClickButton(View v, MyRenderer myRenderer){
		World world = myRenderer.getWorld();
		switch (v.getId()) {
		case R.id.noon_or_dark_button:
			myRenderer.queueEvent(myRenderer);
			break;
		case R.id.init_cube_button:
			world.initCubePosition();
			world.initDominoPosition();
			break;
		case R.id.start_domino_button:
			world.startDomino();
			break;
		default:
			break;
		}
	}

	// アナログスティックのタッチイベント
	public void onAnalogStick(Canvas canvas, MotionEvent e, AnalogStick analogStick){
		// キャンバスの中心座標
		float centerX = canvas.getWidth() / 2;
		float centerY = canvas.getHeight() / 2;
		
		// アナログスティック位置の極座標
		double anaAng = CustomMath.fastAtan2(e.getX() - centerX, e.getY() - centerY);
		anaAng = CustomMath.convertAtan2To360AngRad(anaAng) + (Math.PI/180*90);
		
		if((180/Math.PI*anaAng) > 360){
			anaAng -= Math.PI/180*360;
		}
		
		// アナログスティックを倒す最大値
		float r = canvas.getWidth() / 6;
		
		int idx = (int) (180/Math.PI*anaAng);
		double sin = CustomMath.fastSin(idx);
		double cos = CustomMath.fastCos(idx);
		
		float analogStickX = (float) (r * -cos) + centerX;
		float analogStickY = (float) (r * sin) + centerY;
		
		boolean onAnalogStick = true;
		if(e.getAction() == MotionEvent.ACTION_UP){
			analogStickX = centerX;
			analogStickY = centerY;
			
			onAnalogStick = false;
		}
		
		analogStick.setPosition(analogStickX, analogStickY);
		
		Camera3D cam = PhysxWorldActivity.getMyRenderer().getCamera3D();
		cam.setMove(onAnalogStick);
		cam.setMoveAngle((int)(180/Math.PI*anaAng));
	}
	
	public void onRendererTouch(MotionEvent event, MyRenderer myRenderer){
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			this.touchDownTime = System.currentTimeMillis();
			this.lastX = event.getX();
			this.lastY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			myRenderer.getCamera3D().lookRotation((event.getX()-lastX)/-10, (event.getY()-lastY)/-10);
			this.lastX = event.getX();
			this.lastY = event.getY();
			break;
		case MotionEvent.ACTION_UP:
			long difTime = System.currentTimeMillis() - touchDownTime;
			if(difTime < 100) myRenderer.shootCube(event);
			break;
		default:
			break;
		}
	}

	// メニュー選択のイベント
	public void onSelectedMenuItem(MenuItem item, World world){
		switch (item.getItemId()) {
		case 0: world.darkSwitch();
			break;
		case 1:	world.initCubePosition();
			break;
		default:
			break;
		}
	}
	
	public static MyEvent getInstance(){
		if(instance == null) instance = new MyEvent();
		return instance;
	}
}
