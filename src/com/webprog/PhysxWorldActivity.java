package com.webprog;

import com.webprog.render.MyRenderer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class PhysxWorldActivity extends Activity implements OnTouchListener {
	private static MyRenderer myRenderer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		this.setButton(R.id.forward_button);
		this.setButton(R.id.left_button);
		this.setButton(R.id.right_button);
		this.setButton(R.id.back_button);
		
		myRenderer = (MyRenderer) findViewById(R.id.renderer);

	}
	
	private void setButton(int resId){
		Button btn = (Button) findViewById(resId);
		btn.setOnTouchListener(this);
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(Menu.NONE, 0, 0, "Dark or NonDark");
		menu.add(Menu.NONE, 1, 1, "FallingCube");
		menu.add(Menu.NONE, 2, 2, "InitCubePostion");

		return super.onCreateOptionsMenu(menu);
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		MyEvent mte = MyEvent.getInstance();
		mte.onSelectedMenuItem(item, myRenderer.getWorld());

		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		MyEvent me = MyEvent.getInstance();
		me.onButtonEvent(v, e, myRenderer);
		
		return false;
	}
	
	public static MyRenderer getMyRenderer(){
		return myRenderer;
	}

}
