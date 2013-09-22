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
	private DynamicsWorld mDynamicsWorld;

	private List<Cube> mCubes;
	private List<Cube> mFallingBullets;
	private List<Cube> mCubeBullets;

	private Ground mGround;
	private Sky mSky;

	private int fallingBulletNum;
	private int cubeBulletNum;

	private boolean shootSwitch, rainSwitch, dark;

	private int iTime;

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

		if (dark)
			RenderUtil.enableMaterial(gl, dark);
		
		this.drawObjcets(gl);
		
		if (rainSwitch) {
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
	
	private void drawObjcets(GL10 gl){
		// デフォルトのキューブを描画
		for (Cube cube : mCubes){
			cube.draw(gl);
		}

		// キューブ雨を描画
		for(int i = 0; i < mFallingBullets.size() && isFalling(); i++){
			mFallingBullets.get(i).draw(gl);
		}
		
		// 地面を描画
		mGround.draw(gl);
		
		// 空を描画
		mSky.draw(gl);

		// キューブ弾を描画
		for(int i = 0; i < mCubeBullets.size() && isShoot(); i++){
			mCubeBullets.get(i).draw(gl);
		}

	}
	
	private boolean isFalling(){
		return fallingBulletNum != 0;
	}
	
	private boolean isShoot(){
		return shootSwitch && cubeBulletNum > 0;
	}

	public void shootCube(Vector3f linVel, Vector3f eye) {
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
		dark = !dark;
		
		MyRenderer myRenderer = PhysxWorldActivity.getMyRenderer();
		myRenderer.setDark(dark);
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
