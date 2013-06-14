package com.webprog.phyx.objects;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.util.Log;

import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.webprog.phyx.manager.GrobalVariables;
import com.webprog.phyx.utils.Utils;

public class World {

	public World() {
		initPhysics();

		GrobalVariables.mObjects = new GLObjectInterface[2 + 2];

		GrobalVariables.mObjects[0] = new Cube(GrobalVariables.mDynamicsWorld,
				new Vector3f(0, 0, 3));
		GrobalVariables.mObjects[1] = new Cube(GrobalVariables.mDynamicsWorld,
				new Vector3f(0, 0, 5));

		GrobalVariables.mObjects[2] = new Ground(GrobalVariables.mDynamicsWorld);
		GrobalVariables.mObjects[3] = new Sky(GrobalVariables.mDynamicsWorld);
		// mObjects[4] = new Mountain(mDynamicsWorld, new Vector3f(-5, -25,
		// 10));

		GrobalVariables.bullets = 10;

		GrobalVariables.mCubeBullets = new Cube[GrobalVariables.bullets];

		GrobalVariables.mCubePos = new Vector3f(5, 3, 4);
	}

	private void initPhysics() {
		DbvtBroadphase broadphase = new DbvtBroadphase();

		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(
				collisionConfiguration);

		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		GrobalVariables.mDynamicsWorld = new DiscreteDynamicsWorld(dispatcher,
				broadphase, solver, collisionConfiguration);
		GrobalVariables.mDynamicsWorld.setGravity(new Vector3f(0.0f, 0.0f,
				-10.0f));
	}

	public void draw(GL10 gl) {
		Utils.enableMaterial(gl, GrobalVariables.dark);

		if (GrobalVariables.isTouch) {
			GrobalVariables.eyeX += 0.5f;
			GrobalVariables.lookX += 0.5f;
		}

		for (GLObjectInterface mObject : GrobalVariables.mObjects) {
			mObject.draw(gl);
		}
		if (GrobalVariables.shootSwitch)
			GrobalVariables.mOb1.draw(gl);

		if (GrobalVariables.shootNum >= 0) {
			for (Cube mBullet : GrobalVariables.mCubeBullets) {
				if (mBullet != null) {
					mBullet.draw(gl);
				}
			}
		}

		if (GrobalVariables.rainSwitch == true) {
			if (GrobalVariables.iTime % 8 == 0) {
				fallingCube();
			}

			GrobalVariables.iTime += 1f;
		}

		try {
			GrobalVariables.mDynamicsWorld.stepSimulation(0.33f);
		} catch (NullPointerException e) {
			Log.d("NullPo", "ぬるぽ回避");
		} catch (ArrayIndexOutOfBoundsException e2) {
			Log.d("ArrayIndex", "ArrayIndex回避");
		}

		// fpsCounter.logFrame();

	}

	public void shootInit() {
		GrobalVariables.mOb1 = new Cube(GrobalVariables.mDynamicsWorld,
				new Vector3f(0, -10, 2));

		Vector3f linVel = new Vector3f(0, 50, 0);

		GrobalVariables.mOb1.shootCube(linVel);

		GrobalVariables.shootSwitch = true;
	}

	public void shootInit(Vector3f linVel) {
		if (GrobalVariables.shootNum == -1)
			GrobalVariables.shootNum = 0;
		else if (GrobalVariables.shootNum >= 0
				&& GrobalVariables.shootNum < GrobalVariables.bullets - 1)
			GrobalVariables.shootNum++;
		else {
			for (int i = 0; i < GrobalVariables.bullets - 1; i++) {
				GrobalVariables.mDynamicsWorld
						.removeRigidBody(GrobalVariables.mCubeBullets[i]
								.getRigidBody());
				GrobalVariables.mCubeBullets[i] = null;
			}
			GrobalVariables.shootNum = 0;

		}

		GrobalVariables.mCubeBullets[GrobalVariables.shootNum] = new Cube(
				GrobalVariables.mDynamicsWorld, new Vector3f(
						GrobalVariables.eyeX, GrobalVariables.eyeY,
						GrobalVariables.eyeZ));

		linVel.normalize();
		linVel.scale(35f);

		GrobalVariables.mCubeBullets[GrobalVariables.shootNum]
				.shootCube(linVel);

		// posSwitch = true;

	}

	public void fallingCube() {
		if (GrobalVariables.shootNum == -1)
			GrobalVariables.shootNum = 0;
		else if (GrobalVariables.shootNum >= 0
				&& GrobalVariables.shootNum < GrobalVariables.bullets - 1)
			GrobalVariables.shootNum++;
		else {
			for (int i = 0; i < GrobalVariables.bullets - 1; i++) {
				GrobalVariables.mDynamicsWorld
						.removeRigidBody(GrobalVariables.mCubeBullets[i]
								.getRigidBody());
				GrobalVariables.mCubeBullets[i] = null;
			}
			GrobalVariables.shootNum = 0;

		}

		Random random = new Random();

		float fallX = random.nextFloat() * 20.f;
		float fallY = random.nextFloat() * -10.f;
		float fallZ = random.nextFloat() * 10.f + 1f;

		if (random.nextBoolean())
			fallX = -fallX;
		if (random.nextBoolean())
			fallY = -fallY;

		GrobalVariables.mCubeBullets[GrobalVariables.shootNum] = new Cube(
				GrobalVariables.mDynamicsWorld, new Vector3f(fallX, fallY,
						fallZ));
		GrobalVariables.mCubeBullets[GrobalVariables.shootNum]
				.shootCube(new Vector3f(0, 0, -100));
	}

	public Vector3f getRayTo(int x, int y) {
		float top = 1f;
		float bottom = -1f;
		float nearPlane = 1f;
		float tanFov = (top - bottom) * 0.5f / nearPlane;
		float fov = 2f * (float) Math.atan(tanFov);

		Vector3f rayFrom = new Vector3f(GrobalVariables.eyeX,
				GrobalVariables.eyeY, GrobalVariables.eyeZ);
		Vector3f rayForward = new Vector3f();
		rayForward.sub(new Vector3f(GrobalVariables.lookX,
				GrobalVariables.lookY, GrobalVariables.lookZ), new Vector3f(
				GrobalVariables.eyeX, GrobalVariables.eyeY,
				GrobalVariables.eyeZ));
		rayForward.normalize();
		float farPlane = 10000f;
		rayForward.scale(farPlane);

		Vector3f vertical = new Vector3f(GrobalVariables.upX,
				GrobalVariables.upY, GrobalVariables.upZ);

		Vector3f hor = new Vector3f();
		hor.cross(rayForward, vertical);
		hor.normalize();
		vertical.cross(hor, rayForward);
		vertical.normalize();

		float tanfov = (float) Math.tan(0.5f * fov);

		float aspect = GrobalVariables.height / GrobalVariables.width;

		hor.scale(2f * farPlane * tanfov);
		vertical.scale(2f * farPlane * tanfov);

		if (aspect < 1f) {
			hor.scale(1f / aspect);
		} else {
			vertical.scale(aspect);
		}

		Vector3f rayToCenter = new Vector3f();
		rayToCenter.add(rayFrom, rayForward);
		Vector3f dHor = new Vector3f(hor);
		dHor.scale(1f / GrobalVariables.width);
		Vector3f dVert = new Vector3f(vertical);
		dVert.scale(1.f / (float) GrobalVariables.height);

		Vector3f tmp1 = new Vector3f();
		Vector3f tmp2 = new Vector3f();
		tmp1.scale(0.5f, hor);
		tmp2.scale(0.5f, vertical);

		Log.d("tmp1", "X=" + tmp1.x + "Y=" + tmp1.y + "Z=" + tmp1.z);
		Log.d("tmp2", "X=" + tmp2.x + "Y=" + tmp2.y + "Z=" + tmp2.z);

		Vector3f rayTo = new Vector3f();
		rayTo.sub(rayToCenter, tmp1);

		Log.d("rayTo1", "X=" + rayTo.x + "Y=" + rayTo.y + "Z=" + rayTo.z);

		rayTo.add(tmp2);

		tmp1.scale(x, dHor);
		tmp2.scale(y, dVert);

		rayTo.add(tmp1);
		rayTo.sub(tmp2);
		return rayTo;
	}

	public void darkSwitch() {
		if (GrobalVariables.dark) {
			GrobalVariables.dark = false;
			GrobalVariables.bgColor = 1.0f;
			GrobalVariables.bgColorB = 0.83f;
		} else if (!GrobalVariables.dark) {
			GrobalVariables.dark = true;
			GrobalVariables.bgColor = 0.f;
			GrobalVariables.bgColorB = 0.f;
		}
	}

	public void translateX(boolean isTouch) {
		GrobalVariables.isTouch = isTouch;
	}
}