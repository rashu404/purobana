package com.webprog.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class RenderUtil {
	private static final int SIZEOF_BYTE = Byte.SIZE / 8;
	private static final int SIZEOF_SHORT = Short.SIZE / 8;
	private static final int SIZEOF_FLOAT = Float.SIZE / 8;
	
	private RenderUtil() {
	}
	
	/**
	 * テクスチャをロードして返す
	 * 
	 * @param gl
	 * @param context
	 * @param loadTex ロードするテクスチャのresId
	 * @return
	 */
	public static int returnTex(GL10 gl, Context context, int loadTex) {
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), loadTex);
		int[] textureNo = new int[1];

		gl.glGenTextures(1, textureNo, 0);
		int texture = textureNo[0];

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);

		bmp.recycle();

		return texture;
	}

	/**
	 * ワールドのライティングをEnableにする
	 * 
	 * @param gl
	 */
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

	/**
	 * ワールドのマテリアルをEnableにする
	 * 
	 * @param gl
	 * @param dark
	 */
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
		ByteBuffer bb = ByteBuffer.allocateDirect(floats.length * SIZEOF_FLOAT);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = bb.asFloatBuffer();
		buffer.put(floats);
		buffer.position(0);

		return buffer;
	}

	public static final ShortBuffer allocateShortBuffer(short[] shorts) {
		ByteBuffer bb = ByteBuffer.allocateDirect(shorts.length * SIZEOF_SHORT);
		bb.order(ByteOrder.nativeOrder());
		ShortBuffer sb = bb.asShortBuffer();
		sb.put(shorts);
		sb.position(0);
		return sb;
	}

	public static ByteBuffer allocateByteBuffer(byte bytes[]) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length * SIZEOF_BYTE);
		buffer.put(bytes);
		buffer.position(0);

		return buffer;
	}

	/**
	 * FloatBufferからVBOを作成する
	 * 
	 * @param gl11
	 * @param fb
	 * @return
	 */
	public static int makeFloatVBO(GL11 gl11, FloatBuffer fb) {
		int[] bufferIds = new int[1];
		gl11.glGenBuffers(1, bufferIds, 0);
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, bufferIds[0]);
		gl11.glBufferData(GL11.GL_ARRAY_BUFFER, fb.capacity() * 4, fb, GL11.GL_STATIC_DRAW);
		return bufferIds[0];
	}
}
