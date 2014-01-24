package com.webprog.objects;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CapsuleShapeZ;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.webprog.render.World.Grobal;

// カメラ位置（eye）のCollisionShapeを作成・同期するクラス
public final class Myself{
	private RigidBody rigidBody;
	private Transform transform;
			
	public Myself(DynamicsWorld world, Vector3f eye) {
		// トランスフォーム（位置と回転）を初期化
		this.transform = new Transform();
		this.transform.setIdentity();
		this.transform.origin.set(eye);

		DefaultMotionState motionState = new DefaultMotionState(transform);
		
		// 視覚とダイナミクスワールドを同期
		createKinematicBody(motionState);

		// ワールドへ剛体を追加
		world.addRigidBody(rigidBody);
	}

	// kinematic剛体の作成メソッド
	private void createKinematicBody(DefaultMotionState motionState) {
		// CollisionShapeを作成
		CollisionShape shape = new CapsuleShapeZ(2f, 3f);

		// 剛体の慣性
		Vector3f localInertia = Grobal.tmpVec;
		localInertia.set(10f, 10f, 10f);
		
		// rbInfoを基に剛体を作成
		this.rigidBody = new RigidBody(3f, motionState, shape, localInertia);
		
		// kinematic剛体の設定
		this.rigidBody.setCollisionFlags(rigidBody.getCollisionFlags() | CollisionFlags.KINEMATIC_OBJECT);
		this.rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
	}
	
	// カメラ位置とkinematic剛体の位置を同期
	public void sync(GL10 gl, Vector3f eye, Vector3f look) {
		this.transform.origin.set(eye);
		this.rigidBody.getMotionState().setWorldTransform(this.transform);
	}
}