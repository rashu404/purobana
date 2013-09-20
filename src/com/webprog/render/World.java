package com.webprog.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.webprog.PhysxWorldActivity;
import com.webprog.R;
import com.webprog.objects.Cube;
import com.webprog.objects.Ground;
import com.webprog.objects.Sky;
import com.webprog.util.PhysicsUtil;
import com.webprog.util.RenderUtil;

import android.content.Context;
import android.util.Log;

public class World {
	private Cube mOb1;
	private DynamicsWorld mDynamicsWorld;

	private static List<Cube> mCubes;
	private List<Cube> mFallingBullets;
	private List<Cube> mCubeBullets;

	private Ground mGround;
	private Sky mSky;

	private int fallingBulletNum;
	private int cubeBulletNum;

	private int shootNum = -1;
	private boolean shootSwitch = false;

	private boolean dark = false;

	private int iTime = 0;
	private boolean rainSwitch = false;

	public World(Context context) {		
		mDynamicsWorld = PhysicsUtil.getInitDynamicsWorld();

		mCubes = new ArrayList<Cube>();
		mCubes.add(new Cube(mDynamicsWorld, new Vector3f(0, 0, 3)));
		mCubes.add(new Cube(mDynamicsWorld, new Vector3f(0, 0, 5)));

		mFallingBullets = new ArrayList<Cube>();
		mCubeBullets = new ArrayList<Cube>();

		mGround = new Ground(mDynamicsWorld);
		mSky = new Sky(mDynamicsWorld);

	}

	public void onDrawFrame(GL10 gl) {

		if (dark) {
			RenderUtil.enableMaterial(gl, dark);
		}
		
		for (Cube cube : mCubes)
			cube.draw(gl);

		if (fallingBulletNum != 0) {
			for (int i = 0; i < mFallingBullets.size(); i++)
				mFallingBullets.get(i).draw(gl);
		}

		mGround.draw(gl);
		mSky.draw(gl);
		
		if (shootSwitch) {

			if (cubeBulletNum > 0) {
				for (int i = 0; i < mCubeBullets.size(); i++)
					mCubeBullets.get(i).draw(gl);

			}
		}

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
			shootSwitch = false;

		}

		mCubeBullets.add(new Cube(mDynamicsWorld, eye));

		linVel.normalize();
		linVel.scale(30f);

		mCubeBullets.get(cubeBulletNum).shootCube(linVel);

		cubeBulletNum++;
		shootSwitch = true;
	}

	public void fallingSwitch(boolean arg) {
		rainSwitch = arg;
	}

	public void fallingCube() {
		if (fallingBulletNum > 12) {
			for (int i = 0; i < mFallingBullets.size(); i++)
				mDynamicsWorld.removeRigidBody(mFallingBullets.get(i).getRigidBody());

			mFallingBullets.clear();

			fallingBulletNum = 0;
		}

		Random random = new Random();

		float fallX = random.nextFloat() * 20.f;
		float fallY = random.nextFloat() * -10.f;
		float fallZ = random.nextFloat() * 10.f + 20f;

		if (random.nextBoolean())
			fallX = -fallX;
		if (random.nextBoolean())
			fallY = -fallY;

		mFallingBullets.add(new Cube(mDynamicsWorld, new Vector3f(fallX, fallY, fallZ)));
		mFallingBullets.get(fallingBulletNum).shootCube(new Vector3f(0, 0, -100));

		fallingBulletNum++;

	}

	public void rainSwitch() {
		rainSwitch = !rainSwitch;
	}

	public void darkSwitch() {
		MyRenderer myRenderer = PhysxWorldActivity.getMyRenderer();
		
		if (dark) {
			dark = false;
			myRenderer.setNoDark();
		} else if (!dark) {
			dark = true;
			myRenderer.setDark();
		}
	}

	
	public void worldInit(GL10 gl, Context context) {		
		int texture = RenderUtil.returnTex(gl, context, R.drawable.mokume2);
		int vboId = RenderUtil.makeFloatVBO((GL11)gl, mCubes.get(0).getVertexFloatBuffer());

		Cube.setTexture(texture);
		Cube.setVboId(vboId);
		
		mGround.init(gl, context);
		mSky.init(gl, context);
		
		RenderUtil.enableLight(gl);
	}

}
