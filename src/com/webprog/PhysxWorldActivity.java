package com.webprog;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.webprog.render.MyRenderer;
import com.webprog.ui.AnalogStick;
import com.webprog.ui.FieldMap;

public final class PhysxWorldActivity extends Activity implements OnClickListener{
	private static MyRenderer myRenderer;
	private static FieldMap fieldMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContent();
		setAnalogStick();
		setObjectMap();
		setButton();
		
		myRenderer = (MyRenderer) findViewById(R.id.renderer);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		myRenderer.setFieldMap(fieldMap);
	}
	
	private void setContent(){
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);
	}
	
	private void setAnalogStick(){
		AnalogStick analogStick = new AnalogStick(this);
		
		LinearLayout analogLayout = (LinearLayout)findViewById(R.id.analog_stick_layout);
		analogLayout.addView(analogStick);
	}
	
	private void setObjectMap(){
		fieldMap = new FieldMap(this);
		
		LinearLayout mapLayout = (LinearLayout)findViewById(R.id.object_map_layout);
		mapLayout.addView(fieldMap);	
	}
	
	private void setButton(){
		((Button)findViewById(R.id.noon_or_dark_button)).setOnClickListener(this);
		((Button)findViewById(R.id.start_domino_button)).setOnClickListener(this);
		((Button)findViewById(R.id.init_cube_button)).setOnClickListener(this);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, 0, "Switch Dark or Noon");
		menu.add(Menu.NONE, 1, 1, "Init Object Postion");

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
	
	public static FieldMap getFieldMap(){
		return fieldMap;
	}

	@Override
	public void onClick(View v) {
		MyEvent.getInstance().onClickButton(v, myRenderer);
	}

}
