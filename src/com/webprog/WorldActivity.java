package com.webprog;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
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
    			mRenderer.darkSwitch();
    			break;
    			
    		case MotionEvent.ACTION_UP:
    			break;
    	}
		
		return true;
	}
}
