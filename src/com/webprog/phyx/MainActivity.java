package com.webprog.phyx;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	private PurobanaGLView mGLView;

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