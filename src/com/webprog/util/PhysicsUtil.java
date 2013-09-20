package com.webprog.util;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;

public class PhysicsUtil {
	private static Vector3f rayFrom, rayForward, vertical, hor, rayToCenter, tmp1, tmp2, rayTo;

	public static DynamicsWorld getInitDynamicsWorld() {
		DbvtBroadphase broadphase = new DbvtBroadphase();

		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		DynamicsWorld mDynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver,
				collisionConfiguration);
		mDynamicsWorld.setGravity(new Vector3f(0.0f, 0.0f, -10.0f));

		return mDynamicsWorld;
	}
	public static Vector3f getRayTo(int x, int y, Vector3f eye, Vector3f look, Vector3f up, float width, float height){
		float top = 1f;
		float bottom = -1f;
		float nearPlane = 1f;
		float tanFov = (top - bottom) * 0.5f / nearPlane;
		float fov = 2f * (float) Math.atan(tanFov);
		
		if(rayFrom == null){
			initGetRayTo(eye, look, up);
		}

		// eyeからlookへの方向のベクトルを作成
		rayForward.sub(look, eye);
		rayForward.normalize();

		// 遠い方向へ向け、より正確なベクトルにする
		float farPlane = 10000f;
		rayForward.scale(farPlane);
		
		// rayForwardとverticalの外積を求める
		hor.cross(rayForward, vertical);
		hor.normalize();
		
		// horとrayForwardの外積を求める
		vertical.cross(hor, rayForward);
		vertical.normalize();

		float tanfov = (float) Math.tan(0.5f * fov);
		
		float aspect = height / width;
		
		hor.scale(2f * farPlane * tanfov);
		vertical.scale(2f * farPlane * tanfov);
		
		if (aspect < 1f) {
			hor.scale(1f / aspect);
		}
		else {
			vertical.scale(aspect);
		}
		
		rayToCenter.add(rayFrom, rayForward);
		
		Vector3f dHor = new Vector3f(hor);
		dHor.scale(1f / width);
		Vector3f dVert = new Vector3f(vertical);
		dVert.scale(1.f / (float) height);

		tmp1.scale(0.5f, hor);
		tmp2.scale(0.5f, vertical);

		rayTo.sub(rayToCenter, tmp1);
		
		rayTo.add(tmp2);

		tmp1.scale(x, dHor);
		tmp2.scale(y, dVert);

		rayTo.add(tmp1);
		rayTo.sub(tmp2);
		return rayTo;
	}
	
	private static void initGetRayTo(Vector3f eye, Vector3f look, Vector3f up){
		rayFrom = new Vector3f(eye.x, eye.y, eye.z);
		rayForward = new Vector3f();
		
		vertical = new Vector3f(up.x, up.y, up.z);
		hor = new Vector3f();
		
		rayToCenter = new Vector3f();
		
		tmp1 = new Vector3f();
		tmp2 = new Vector3f();
		
		rayTo = new Vector3f();
	}

}
