package com.webprog.phyx.manager;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.content.Context;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.webprog.phyx.objects.Cube;
import com.webprog.phyx.objects.GLObjectInterface;

public class GrobalVariables {
	public static Context mContext;
	public static GLObjectInterface mObjects[];
	public static Cube mCubeBullets[];
	public static Cube mOb1;
	public static DynamicsWorld mDynamicsWorld;
	public static GL10 gl10;

	public static FPSCounter fpsCounter;

	public static int bullets;

	public static float eyeX = 2.f, eyeY = 10.f, eyeZ = 3.f;
	public static float lookX = 0.f, lookY = 0, lookZ = 1;
	public static float upX = 0, upY = 0, upZ = 1;

	public static float rotateX = 0.0f;

	public static int shootNum = -1;
	public static boolean shootSwitch = false;

	public static float bgColor = 1.0f, bgColorB = 0.83f;

	public static boolean dark = false;
	public static boolean isTouch = false;

	public static float width, height;

	public static Vector3f mCubePos;
	public static boolean posSwitch = false;

	public static int iTime = 0;
	public static boolean rainSwitch = false;

}