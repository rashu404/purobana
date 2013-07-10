package com.webprog.render;

import java.lang.ref.WeakReference;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.webprog.objects.Cube;
import com.webprog.objects.Ground;
import com.webprog.objects.Sky;
import com.webprog.utils.PhysicsUtils;
import com.webprog.utils.RenderUtils;

import android.content.Context;
import android.util.Log;

public class World {
	Context mContext;
	Cube mOb1;
	DynamicsWorld mDynamicsWorld;
	GL10 gl10;

	List<Cube> mCubes;
	List<Cube> mCubeBullets;
	WeakReference<Cube> mCubeBulletWeek;

	Ground mGround;
	Sky mSky;

	private int cubeBulletNum;

	private int shootNum = -1;
	private boolean shootSwitch = false;

	private float bgColor = 1.0f, bgColorB = 0.83f;

	private boolean dark = false;
	private boolean isTouch = false;

	private float width, height;

	boolean posSwitch = false;

	int iTime = 0;
	boolean rainSwitch = false;

	public World(Context context) {
		mContext = context;

		mDynamicsWorld = PhysicsUtils.getInitDynamicsWorld();

		mCubes = new ArrayList<Cube>();
		mCubes.add(new Cube(mDynamicsWorld, new Vector3f(0, 0, 3)));
		mCubes.add(new Cube(mDynamicsWorld, new Vector3f(0, 0, 5)));

		mCubeBullets = new ArrayList<Cube>();

		mGround = new Ground(mDynamicsWorld);
		mSky = new Sky(mDynamicsWorld);

	}

	public void onDrawFrame(GL10 gl) {

		// if(posSwitch == false)
		// GLU.gluLookAt(gl, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY,
		// upZ);
		// else
		// GLU.gluLookAt(gl, mCubePos.x + 2, mCubePos.y + 2, mCubePos.z + 2,
		// mCubePos.x, mCubePos.y, mCubePos.z, upX, upY, upZ);

		// gl.glRotatef(rotateX, 0, 0, 1);
		// rotateX += 0.5f;
		//
		// RenderUtils.enableMaterial(gl, dark);

		// if(isTouch){
		// eyeX += 0.5f;
		// lookX += 0.5f;
		// }

		for (Cube cube : mCubes)
			cube.draw(gl);

		if (cubeBulletNum != 0) {
			for (int i = 0; i < mCubeBullets.size(); i++)
				mCubeBullets.get(i).draw(gl);
		}

		mGround.draw(gl);
		mSky.draw(gl);

		if (shootSwitch)
			mOb1.draw(gl);

		if (shootNum >= 0) {
			for (Cube mBullet : mCubeBullets) {
				if (mBullet != null) {
					mBullet.draw(gl);
				}
			}
		}

		if (rainSwitch == true) {
			if (iTime % 8 == 0)
				fallingCube();

			iTime += 1f;
		}

		try {
			mDynamicsWorld.stepSimulation(0.33f);
		} catch (NullPointerException e) {
			Log.d("NullPo", "ぬるぽ回避");
		} catch (ArrayIndexOutOfBoundsException e2) {
			Log.d("ArrayIndex", "ArrayIndex回避");
		}

		// fpsCounter.logFrame();

	}

	public void shootInit() {
		mOb1 = new Cube(mDynamicsWorld, new Vector3f(0, -10, 2));

		Vector3f linVel = new Vector3f(0, 50, 0);

		mOb1.shootCube(linVel);

		shootSwitch = true;
	}

	public void shootInit(Vector3f linVel, Vector3f eye) {
		if (cubeBulletNum > 10) {
			for (int i = 0; i < mCubeBullets.size(); i++)
				mDynamicsWorld.removeRigidBody(mCubeBullets.get(i).getRigidBody());

			mCubeBullets.clear();

			cubeBulletNum = 0;

		}

		mCubeBullets.add(new Cube(mDynamicsWorld, eye));

		linVel.normalize();
		linVel.scale(100f);

		mCubeBullets.get(cubeBulletNum).shootCube(linVel);

		cubeBulletNum++;
	}

	public void fallingSwitch(boolean rainSwitch) {
		this.rainSwitch = rainSwitch;
	}

	public void fallingCube() {
		if (cubeBulletNum > 10) {
			for (int i = 0; i < mCubeBullets.size(); i++)
				mDynamicsWorld.removeRigidBody(mCubeBullets.get(i).getRigidBody());

			mCubeBullets.clear();

			cubeBulletNum = 0;

		}

		Random random = new Random();

		float fallX = random.nextFloat() * 20.f;
		float fallY = random.nextFloat() * -10.f;
		float fallZ = random.nextFloat() * 10.f + 1f;

		if (random.nextBoolean())
			fallX = -fallX;
		if (random.nextBoolean())
			fallY = -fallY;

		mCubeBullets.add(new Cube(mDynamicsWorld, new Vector3f(fallX, fallY, fallZ)));
		mCubeBullets.get(cubeBulletNum).shootCube(new Vector3f(0, 0, -100));
		
		cubeBulletNum++;

	}

	public void darkSwitch() {
		if (dark) {
			dark = false;
			bgColor = 1.0f;
			bgColorB = 0.83f;
		} else if (!dark) {
			dark = true;
			bgColor = 0.f;
			bgColorB = 0.f;
		}
	}

	public void translateX(boolean isTouch) {
		this.isTouch = isTouch;
	}

	public void worldInit(GL10 gl, Context context) {
		for (Cube cube : mCubes) {
			cube.init(gl, context);
		}

		mGround.init(gl, context);
		mSky.init(gl, context);

		RenderUtils.enableLight(gl);

		gl10 = gl;
	}

}
