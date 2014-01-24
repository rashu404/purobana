package com.webprog.tool;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.webprog.render.World.Grobal;

public final class PhysicsUtil {
	private static Vector3f rayFrom, rayForward, hor, vertical, 
					dHor, dVert, rayToCenter, tmp1, tmp2, rayTo;
	
	private static float fov, tanfov, aspect;

	/**
	 * 初期化したダイナミクスワールドを返す
	 * 
	 * @return
	 */
	public static DynamicsWorld getInitDynamicsWorld() {
		DbvtBroadphase broadphase = new DbvtBroadphase();

		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

		Vector3f gravity = Grobal.tmpVec;
		gravity.set(0.0f, 0.0f, -10.0f);
		
		DynamicsWorld dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver,
				collisionConfiguration);
		dynamicsWorld.setGravity(gravity);

		return dynamicsWorld;
	}
	
	/**
	 * タップした方向へのベクトルを返す
	 * 
	 * @param x タップ位置X
	 * @param y タップ位置Y
	 * @param eye カメラの位置
	 * @param look カメラの視点
	 * @param up カメラの上方向
	 * @param width デバイスの横幅
	 * @param height デバイスの縦幅
	 * @return
	 */
	public static Vector3f getRayTo(int x, int y, Vector3f eye, Vector3f look, Vector3f up, float width, float height){
		float top = 1f;
		float bottom = -1f;
		float nearPlane = 1f;
		float tanFov = (top - bottom) * 0.5f / nearPlane;
		if(fov == 0) fov = 2f * (float) Math.atan(tanFov);
		
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

		if(tanfov == 0) tanfov = (float) Math.tan(0.5f * fov);
		
		float aspect = height / width;
		
		hor.scale(2f * farPlane * tanfov);
		vertical.scale(2f * farPlane * tanfov);
		
		if(PhysicsUtil.aspect != aspect){
			if (aspect < 1f) {
				hor.scale(1f / aspect);
			}
			else {
				vertical.scale(aspect);
			}
			
			PhysicsUtil.aspect = aspect;
		}
		
		rayToCenter.add(rayFrom, rayForward);
		
		if(dHor == null){
			dHor = new Vector3f(hor);
		}else {
			dHor.set(hor.x, hor.y, hor.z);
		}
		
		dHor.scale(1f / width);
		
		if(dVert == null){
			dVert = new Vector3f(vertical);
		}else {
			dVert.set(vertical.x, vertical.y, vertical.z);
		}
		
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
		
		hor = new Vector3f();
		vertical = new Vector3f(up.x, up.y, up.z);
		
		rayToCenter = new Vector3f();
		
		tmp1 = new Vector3f();
		tmp2 = new Vector3f();
		
		rayTo = new Vector3f();
	}

}
