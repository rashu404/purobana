package com.webprog;

import javax.vecmath.Vector3f;

import android.app.Activity;
import android.content.Context;
import android.drm.DrmStore.Action;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class WorldActivity extends Activity {
	PurobanaGLView mGLView;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mGLView = new PurobanaGLView(this);
        
        setContentView(mGLView);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }
}

class PurobanaGLView extends GLSurfaceView {
	World mRenderer;
	Ground mGround;
	
	public PurobanaGLView(Context context){
		super(context);
		
		mRenderer = new World(context);
		setRenderer(mRenderer);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		queueEvent(new Runnable() {
			public void run() {
			}
		});
		switch(ev.getActionMasked())
    	{
    		case MotionEvent.ACTION_DOWN:
    			//mRenderer.shootInit();
    			/* 固定位置へキューブ弾を発射 */
    			
    			//mRenderer.darkSwitch();
    			/* 昼夜の切り替え */
    			
    			//mRenderer.translateX(true);
    			/* 電車風景のようにカメラの平行移動 */
    			
    			Vector3f point = mRenderer.getRayTo((int)ev.getX(), (int)ev.getY());
    			mRenderer.shootInit(point);
    			/* タッチ位置へキューブ弾を発射 */
    			
    			//Log.d("VecPoint", "X=" + point.x + "_Y=" + point.y + "_Z=" + point.z);
    			//Log.d("IntPoint", "X=" + (int)ev.getX() + "_Y=" + (int)ev.getY());
    			break;
    			
    		case MotionEvent.ACTION_UP:
    			//mRenderer.translateX(false);
    			/* 電車風平行移動の静止 */
    			
    			break;
    	}
		
		//Log.d("MotionEvent", "X= " + ev.getX() + "_Y= " + ev.getY());
		
		return true;
	}
}
