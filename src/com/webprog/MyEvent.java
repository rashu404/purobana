package com.webprog;

import android.graphics.Canvas;
import android.view.*;

import com.webprog.render.*;
import com.webprog.tool.MathUtil;
import com.webprog.ui.AnalogStick;

public final class MyEvent {
	private static MyEvent instance;

	private double[] sinCache = new double[361], 
					 cosCache = new double[361];
	
	private long touchDownTime;
	private float lastX, lastY;
	
	private MyEvent() {
		setSinCosCache();
	}
	
	public void onClickButton(View v, World world){
		switch (v.getId()) {
		case R.id.noon_or_dark_button:
			world.darkSwitch();
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
	public void onAnalogStick(Canvas canvas, MotionEvent e){
		// キャンバスの中心座標
		float centerX = canvas.getWidth() / 2;
		float centerY = canvas.getHeight() / 2;
		
		// アナログスティック位置の極座標
		double anaAng = Math.atan2(e.getX() - centerX, e.getY() - centerY);
		anaAng = MathUtil.convertAtan2To360AngRad(anaAng) + (Math.PI/180*90);
		
		if((180/Math.PI*anaAng) > 360){
			anaAng -= Math.PI/180*360;
		}
		
		// アナログスティックを倒す最大値
		float r = canvas.getWidth() / 6;
		
		int idx = (int) (180/Math.PI*anaAng);
		float analogStickX = (float) (r * -cosCache[idx]) + centerX;
		float analogStickY = (float) (r * sinCache[idx]) + centerY;
		
		boolean onAnalogStick = true;
		if(e.getAction() == MotionEvent.ACTION_UP){
			analogStickX = centerX;
			analogStickY = centerY;
			
			onAnalogStick = false;
		}
		
		AnalogStick analogStick = PhysxWorldActivity.getAnalogStick();
		analogStick.setAnalogStickPos(analogStickX, analogStickY);
		
		MyRenderer myRenderer = PhysxWorldActivity.getMyRenderer();
		myRenderer.setMove(onAnalogStick);
		
		myRenderer.setMoveAngle((int)(180/Math.PI*anaAng));
	}
	
	public void onRendererTouch(MotionEvent event, MyRenderer myRenderer){
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			this.touchDownTime = System.currentTimeMillis();
			this.lastX = event.getX();
			this.lastY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			myRenderer.lookRotation((event.getX()-lastX)/-10, (event.getY()-lastY)/-10);
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
		case 0:
			world.darkSwitch();
			break;
		case 1:
			world.rainSwitch();
			break;
		case 2:
			world.initCubePosition();
			break;

		default:
			break;
		}
	}
	
	public double[] getSinCache(){
		return this.sinCache;
	}
	
	public double[] getCosCache(){
		return this.cosCache;
	}
	
	public void setSinCosCache(){
		if(sinCache[45] == 0 && cosCache[45] == 0){
			for(int i = 0; i < 361; i++){
				// Math.toRadians()よりも手動の方が速い
				double angRad = Math.PI / 180 * i;
				sinCache[i] = Math.sin(angRad);
				cosCache[i] = Math.cos(angRad);
			}
		}
	}

	public static MyEvent getInstance(){
		if(instance == null) instance = new MyEvent();
		return instance;
	}
}
