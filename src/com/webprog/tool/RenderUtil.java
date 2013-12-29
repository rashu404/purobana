package com.webprog.tool;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.*;
import android.opengl.GLUtils;
import android.view.*;

public final class RenderUtil {
	private static final int SIZEOF_BYTE = Byte.SIZE / 8;
	private static final int SIZEOF_SHORT = Short.SIZE / 8;
	private static final int SIZEOF_FLOAT = Float.SIZE / 8;
	
	private static Point size;
	
	private static float[] matAmbient = new float[4];
	private static float[] matDiffuse = new float[4];
	
	private RenderUtil() {
	}
	
	// テクスチャをロードして返す
	public static int loadTex(GL10 gl, Context context, int texResId) {
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), texResId);
		int[] textureNo = new int[1];

		gl.glGenTextures(1, textureNo, 0);
		int texture = textureNo[0];

		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		bmp.recycle();

		return texture;
	}

	// 光源を有効化
	public static void enableLight(GL10 gl) {
		float[] lightAmbient = new float[] { 0.5f, 0.5f, 0.5f, 1.f };
		float[] lightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.f };
		float[] lightPos = new float[] { 10, 10, 3, 0 };

		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);

		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
	}

	// 夜のマテリアルを有効化
	public static void enableDarkMaterial(GL10 gl) {
		matAmbient[0] = 0.6f;
		matAmbient[1] = 0.6f;
		matAmbient[2] = 0.9f;
		matAmbient[3] = 1.0f;
		
		matDiffuse[0] = 0.0f;
		matDiffuse[1] = 0.0f;
		matDiffuse[2] = 1.0f;
		matDiffuse[3] = 1.0f;
		
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);
	}
	
	// 昼のマテリアルを有効化
	public static void enableNoonMaterial(GL10 gl){
		for(int i=0; i<4; i++){
			matAmbient[i] = 1.0f;
			matDiffuse[i] = 1.0f;
		}

		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);
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
	
	// FloatBufferからVBOを作成する
	public static int makeFloatVBO(GL11 gl11, FloatBuffer fb) {
		int[] bufferIds = new int[1];
		gl11.glGenBuffers(1, bufferIds, 0);
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, bufferIds[0]);
		gl11.glBufferData(GL11.GL_ARRAY_BUFFER, fb.capacity() * 4, fb, GL11.GL_STATIC_DRAW);
		return bufferIds[0];
	}
	
	// 端末の解像度を返す
	public static Point getSizeXY(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		// sizeに解像度をセット
		if(size == null) size = new Point();
		display.getSize(size);
		
		return size;
	}
}
