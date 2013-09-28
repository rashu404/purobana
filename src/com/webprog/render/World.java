package com.webprog.render;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.webprog.PhysxWorldActivity;
import com.webprog.objects.Cube;
import com.webprog.objects.Ground;
import com.webprog.objects.Sky;
import com.webprog.util.PhysicsUtil;
import com.webprog.util.RenderUtil;

import android.content.Context;

public class World {
	private static final int MAX_CUBE_BULLETS = 7,
			 				 MAX_FALLING_CUBE = 7;
	
	private DynamicsWorld mDynamicsWorld;

	private Cube[] mCubes = new Cube[3];
	private Cube[] mCubeBullets = new Cube[MAX_CUBE_BULLETS];
	private Cube[] mFallBullets = new Cube[MAX_FALLING_CUBE];
	
	private Ground mGround;
	private Sky mSky;

	private Random random;
	
	private int currentFallBullet;
	private int currentCubeBullet;

	private boolean isShoot, isFall, isDark;

	private int fallIntervalTime;
	
	private Vector3f fallCubeTmpVec;
	
	public World(Context context) {		
		mDynamicsWorld = PhysicsUtil.getInitDynamicsWorld();
		
		Vector3f initVec = new Vector3f(0, 0, 3);
		mCubes[0] = new Cube(mDynamicsWorld, initVec);
		
		initVec.set(0, 0, 5);
		mCubes[1] = new Cube(mDynamicsWorld, initVec);
		
		initVec.set(0, 0, 7);
		mCubes[2] = new Cube(mDynamicsWorld, initVec);
	
		mGround = new Ground(mDynamicsWorld);
		mSky = new Sky(mDynamicsWorld);
	}

	public void onDrawFrame(GL10 gl) {
		
		if (isDark){
			RenderUtil.enableMaterial(gl, isDark);
		}else {
			RenderUtil.enableMaterial(gl, isDark);
		}
		
		drawObjcets(gl);
		
		if (isFall) {
			if (fallIntervalTime % 8 == 0)
				fallingCube();

			fallIntervalTime += 1f;
		}

		mDynamicsWorld.stepSimulation(0.33f);
	}
	
	private void drawObjcets(GL10 gl){
		
		// デフォルトのキューブを描画
		for (Cube cube : mCubes){
			cube.draw(gl);
		}

		// キューブ雨を描画
		for(int i = 0; i < mFallBullets.length && isFalling(i); i++){
			mFallBullets[i].draw(gl);
		}
		
		// 地面を描画
		mGround.draw(gl);
		
		// 空を描画
		mSky.draw(gl);

		// キューブ弾を描画
		for(int i = 0; i < mCubeBullets.length && isShoot(i); i++){
			mCubeBullets[i].draw(gl);
		}

	}
	
	private boolean isFalling(int i){
		return currentFallBullet != 0 && mFallBullets[i] != null;
	}
	
	private boolean isShoot(int i){
		return isShoot && mCubeBullets[i] != null;
	}

	// キューブ弾の発射メソッド
	public void shootCube(Vector3f linVel, Vector3f eye) {
		// キューブ弾の上限を超えたら最古のインスタンスを再利用する
		if (currentCubeBullet >= MAX_CUBE_BULLETS) currentCubeBullet = 0;
		
		if(mCubeBullets[currentCubeBullet] == null){
			mCubeBullets[currentCubeBullet] = new Cube(mDynamicsWorld, eye);
		}else {
			mCubeBullets[currentCubeBullet].setPosition(eye.x, eye.y, eye.z);
		}
		
		linVel.normalize();
		linVel.scale(30f);

		mCubeBullets[currentCubeBullet].shootCube(linVel);

		currentCubeBullet++;
		isShoot = true;
	}

	// キューブ雨メソッド
	public void fallingCube(){
		// キューブ雨の上限を超えたら最古のインスタンスを再利用する
		if (currentFallBullet >= MAX_FALLING_CUBE) currentFallBullet = 0;
		
		if(random == null)
			random = new Random();

		float fallX = random.nextFloat() * 20.f;
		float fallY = random.nextFloat() * -10.f;
		float fallZ = random.nextFloat() * 10.f + 20f;
		
		if (random.nextBoolean())
			fallX = -fallX;
		if (random.nextBoolean())
			fallY = -fallY;
		
		if(fallCubeTmpVec == null){
			fallCubeTmpVec = new Vector3f();
		}
		
		if(mFallBullets[currentFallBullet] == null){
			fallCubeTmpVec.set(fallX, fallY, fallZ);
			mFallBullets[currentFallBullet] = new Cube(mDynamicsWorld, fallCubeTmpVec);
			
			fallCubeTmpVec.set(0, 0, -100);
			mFallBullets[currentFallBullet].shootCube(fallCubeTmpVec);
		}else {
			mFallBullets[currentFallBullet].setPosition(fallX, fallY, fallZ);
			
			fallCubeTmpVec.set(0, 0, -100);
			mFallBullets[currentFallBullet].shootCube(fallCubeTmpVec);
		}
		
		currentFallBullet++;
	}

	public void rainSwitch() {
		isFall = !isFall;
	}

	public void darkSwitch() {
		isDark = !isDark;
		
		MyRenderer myRenderer = PhysxWorldActivity.getMyRenderer();
		myRenderer.setDark(isDark);
	}
	
	public DynamicsWorld getDynamicsWorld(){
		return mDynamicsWorld;
	}
	
	public void initCubePosition(){
		mCubes[0].setPosition(0, 0, 3);
		mCubes[1].setPosition(0, 0, 5);
		mCubes[2].setPosition(0, 0, 7);
	}
	
	public void worldInit(GL10 gl, Context context) {	
		Cube.init(gl, context);
		
		mGround.init(gl, context);
		mSky.init(gl, context);
		
		RenderUtil.enableLight(gl);
	}

}
