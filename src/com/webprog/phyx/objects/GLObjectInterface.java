package com.webprog.phyx.objects;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public interface GLObjectInterface {
	public void init(GL10 gl, Context context);

	public void draw(GL10 gl);
}
