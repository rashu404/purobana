package com.webprog.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class RenderUtils {

	public static int returnTex(GL10 gl, Context context, int loadTex) {
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), loadTex);
		int[] textureNo = new int[1];

		gl.glGenTextures(1, textureNo, 0);
		int mTexture = textureNo[0];

		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);

		bmp.recycle();

		return mTexture;
	}

	public static void enableLight(GL10 gl) {
		float[] lightAmbient = new float[] { 0.38f, 0.38f, 0.38f, 1.f };
		float[] lightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.f };
		float[] lightPos = new float[] { 8, 8, 30, 0 };

		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);

		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
	}

	public static void enableMaterial(GL10 gl, boolean dark) {
		if (dark) {
			float[] matAmbient = new float[] { 0.6f, 0.6f, 0.9f, 1.0f };
			float[] matDiffuse = new float[] { 0.0f, 0.0f, 1.0f, 1.0f };

			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);
		} else if (!dark) {
			float[] matAmbient = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
			float[] matDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);
		}
	}

	public static FloatBuffer allocateFloatBuffer(float floats[]) {
		ByteBuffer bb = ByteBuffer.allocateDirect(floats.length * Float.SIZE / 8);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = bb.asFloatBuffer();
		buffer.put(floats);
		buffer.position(0);

		return buffer;
	}

	public static ByteBuffer allocateByteBuffer(byte bytes[]) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
		buffer.put(bytes);
		buffer.position(0);

		return buffer;
	}

	public static void drawVertexOptimization(GL11 gl11, FloatBuffer vertexBuffer) {
		int buffers[] = new int[1];
		gl11.glGenBuffers(1, buffers, 0);
		int posBufferObject = buffers[0];

		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, posBufferObject);
		gl11.glBufferData(GL11.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer,
				GL11.GL_STATIC_DRAW);

	}
}
