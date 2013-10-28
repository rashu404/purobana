package com.webprog;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.LinearLayout;

import com.webprog.render.MyRenderer;
import com.webprog.ui.AnalogStick;

public final class PhysxWorldActivity extends Activity{
	private static MyRenderer myRenderer;
	private static AnalogStick analogStick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContent();
		setAnalogStick();
		
		myRenderer = (MyRenderer) findViewById(R.id.renderer);
	}
	
	private void setContent(){
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);
	}
	
	private void setAnalogStick(){
		analogStick = new AnalogStick(this);
		
		LinearLayout analogLayout = (LinearLayout)findViewById(R.id.analog_stick_layout);
		analogLayout.addView(analogStick);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(Menu.NONE, 0, 0, "Switch Dark or Noon");
		menu.add(Menu.NONE, 1, 1, "Falling Cubes");
		menu.add(Menu.NONE, 2, 2, "Init Cubes Postion");

		return super.onCreateOptionsMenu(menu);
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		MyEvent mte = MyEvent.getInstance();
		mte.onSelectedMenuItem(item, myRenderer.getWorld());

		return true;
	}

	public static MyRenderer getMyRenderer(){
		return myRenderer;
	}
	
	public static AnalogStick getAnalogStick(){
		return analogStick;
	}

}
