package com.webprog;

import com.webprog.render.MyRenderer;
import com.webprog.render.World;

import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class MyEvent {
	private static MyEvent instance;

	private float distanceX;
	private long tapUpMillis;
	
	private MyEvent() {
	}
	
	// 上下左右ボタンのタッチイベント
	public void onButtonEvent(View v, MotionEvent e, MyRenderer myRenderer){
		
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:

			switch (v.getId()) {
			case R.id.forward_button:

				myRenderer.setForward(true);

				break;

			case R.id.left_button:

				myRenderer.setLeft(true);

				break;

			case R.id.right_button:

				myRenderer.setRight(true);

				break;

			case R.id.back_button:

				myRenderer.setBack(true);

				break;
			}

			break;

		case MotionEvent.ACTION_UP:

			myRenderer.setForward(false);
			myRenderer.setLeft(false);
			myRenderer.setRight(false);
			myRenderer.setBack(false);

			break;
			
		default:
			break;
		}
	}
	
	// レンダラ上のタッチイベント
	public void onRenderEvent(MotionEvent event, MyRenderer myRenderer){
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			distanceX = event.getX();
			tapUpMillis = System.currentTimeMillis();
			
			break;

		case MotionEvent.ACTION_MOVE:
			
			myRenderer.lookRotation((event.getX() - distanceX) / 10);
			
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
