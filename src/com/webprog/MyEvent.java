package com.webprog;

import com.webprog.render.MyRenderer;
import com.webprog.render.World;

import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class MyEvent {
	private static MyEvent instance = new MyEvent();
	
	private MyRenderer myRenderer;
	private World mWorld;
	
	private float distanceX;
	private long tapUpMillis;
	
	public MyEvent() {
		myRenderer = PhysxWorldActivity.getMyRenderer();
		mWorld = myRenderer.getWorld();
	}
	
	
	// 上下左右ボタンのタッチイベント
	public void onButtonEvent(View v, MotionEvent e){
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
		}
	}
	
	// レンダラ上のタッチイベント
	public void onRenderEvent(MotionEvent event){
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
			
			Log.d("DEBUG", "ActioUp");
			
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
	public void onSelectedMenuItem(MenuItem item){
		switch (item.getItemId()) {
		case 0:
			mWorld.darkSwitch();
			break;
		case 1:
			mWorld.rainSwitch();
			break;
		}
	}
	
	public static MyEvent getInstance(){
		return instance;
	}
}
