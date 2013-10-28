package com.webprog;

import android.graphics.Canvas;
import android.view.*;

import com.webprog.render.*;
import com.webprog.ui.AnalogStick;
import com.webprog.util.MathUtil;

public final class MyEvent {
	private static MyEvent instance;

	private float distanceX;
	private long tapUpMillis;

	private MyEvent() {
	}

	// アナログスティックのタッチイベント
	public void onAnalogStick(Canvas canvas, MotionEvent e){
		// キャンバスの中心座標
		float centerX = canvas.getWidth() / 2;
		float centerY = canvas.getHeight() / 2;
		
		// アナログスティック位置の極座標
		double anaAng = Math.atan2(e.getX() - centerX, e.getY() - centerY);
		anaAng = MathUtil.convertSinCosRad(anaAng);
		
		// カメラ位置の移動先の角度
		double moveAng = Math.atan2(e.getX() - centerX, centerY - e.getY());
		moveAng = MathUtil.convertAnalogRad(moveAng);
		
		// アナログスティックを倒す最大値
		float r = canvas.getWidth() / 6;
		
		// アナログスティック位置
		float analogStickX = (float) (r * Math.cos(anaAng)) + centerX;
		float analogStickY = (float) (r * Math.sin(anaAng)) + centerY;
				
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
		
		myRenderer.setMoveAngle((int)Math.toDegrees(moveAng));
	}

	// レンダラ上のタッチイベント
	public void onRenderEvent(MotionEvent event, MyRenderer myRenderer){

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			distanceX = event.getX();
			tapUpMillis = System.currentTimeMillis();

			break;

		case MotionEvent.ACTION_MOVE:

			myRenderer.lookRotation((event.getX() - distanceX) / -10);

			distanceX = event.getX();

			break;

		case MotionEvent.ACTION_UP:

			long cTimeMillis = System.currentTimeMillis();
			long diffTime = cTimeMillis - tapUpMillis;

			if(diffTime < 100)
				myRenderer.shootCube(event);

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

	public static MyEvent getInstance(){
		if(instance == null)
			instance = new MyEvent();

		return instance;
	}
}
