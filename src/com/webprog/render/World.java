package com.webprog.render;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.content.Context;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.webprog.objects.*;
import com.webprog.util.*;

public final class World {
	private static final int DEFAULT_CUBES = 3,
							 MAX_CUBE_BULLETS = 7,
			 				 MAX_FALLING_CUBE = 7;
	
	private DynamicsWorld mDynamicsWorld;

	private Cube[] mCubes = new Cube[DEFAULT_CUBES];
	private Cube[] mCubeBullets = new Cube[MAX_CUBE_BULLETS];
	private Cube[] mFallBullets = new Cube[MAX_FALLING_CUBE];
	
	private Ground mGround;
	private Sky mSky;

	private Random random;
	
	private int currentShootBullet;
	private int currentFallBullet;
	
	private boolean isShoot, isFall, isDark;

	private int fallIntervalTime;
	
	private Vector3f tmpVec;
	
	public World(Context context) {		
		mDynamicsWorld = PhysicsUtil.getInitDynamicsWorld();
		
		Vector3f initVec = new Vector3f(0, 0, 3);
		
		for(int i = 0; i < mCubes.length; i++){
			initVec.set(0, 0, i+i+3);
			mCubes[i] = new Cube(mDynamicsWorld, initVec);
		}
	
		mGround = new Ground(mDynamicsWorld);
		mSky = new Sky(mDynamicsWorld);
	}

	public void onDrawFrame(GL10 gl) {
		
		if (isDark){
			RenderUtil.enableDarkMaterial(gl);
		}else {
			RenderUtil.enableNoonMaterial(gl);
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
		if (currentShootBullet >= MAX_CUBE_BULLETS) currentShootBullet = 0;
		
		if(mCubeBullets[currentShootBullet] == null){
			mCubeBullets[currentShootBullet] = new Cube(mDynamicsWorld, eye);
		}else {
			mCubeBullets[currentShootBullet].setPosition(eye.x, eye.y, eye.z);
		}
		
		linVel.normalize();
		linVel.scale(30f);

		mCubeBullets[currentShootBullet].shootCube(linVel);

		currentShootBullet++;
		isShoot = true;
	}

	// キューブ雨メソッド
	public void fallingCube(){
		// キューブ雨の上限を超えたら最古のインスタンスを再利用する
		if (currentFallBullet >= MAX_FALLING_CUBE) currentFallBullet = 0;
		
		if(random == null) random = new Random();

		float fallX = random.nextFloat() * 20.f;
		float fallY = random.nextFloat() * -10.f;
		float fallZ = random.nextFloat() * 10.f + 20f;
		
		if (random.nextBoolean()) fallX = -fallX;
		if (random.nextBoolean()) fallY = -fallY;
		
		if(tmpVec == null) tmpVec = new Vector3f();
		
		if(mFallBullets[currentFallBullet] == null){
			tmpVec.set(fallX, fallY, fallZ);
			mFallBullets[currentFallBullet] = new Cube(mDynamicsWorld, tmpVec);
			
			tmpVec.set(0, 0, -100);
			mFallBullets[currentFallBullet].shootCube(tmpVec);
		}else {
			mFallBullets[currentFallBullet].setPosition(fallX, fallY, fallZ);
			
			tmpVec.set(0, 0, -100);
			mFallBullets[currentFallBullet].shootCube(tmpVec);
		}
		
		currentFallBullet++;
	}

	public void rainSwitch() {
		isFall = !isFall;
	}

	public void darkSwitch() {
		isDark = !isDark;
	}
	
	public boolean isDark(){
		return isDark;
	}
	
	public DynamicsWorld getDynamicsWorld(){
		return mDynamicsWorld;
	}
	
	public void initCubePosition(){
		if(tmpVec == null) tmpVec = new Vector3f();
		
		tmpVec.set(0f, 0f, 0f);
		
		for(int i = 0; i < mCubes.length; i++)
			mCubes[i].initPosition(tmpVec, tmpVec, 0, 0, i+i+3);
	}
	
	public void worldInit(GL10 gl, Context context) {	
		Cube.init(gl, context);
		
		mGround.init(gl, context);
		mSky.init(gl, context);
		
		RenderUtil.enableLight(gl);
	}

}
