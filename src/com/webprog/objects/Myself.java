package com.webprog.objects;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.linearmath.*;

/**
 *  カメラ位置（eye）のCollisionShapeを作成・同期するクラス
 */
public final class Myself{	
	private RigidBody mRigidBody;
	private Transform mTransform;
			
	public Myself(DynamicsWorld world, Vector3f eye) {
		// トランスフォーム（位置と回転）を初期化
		mTransform = new Transform();
		mTransform.setIdentity();
		mTransform.origin.set(eye);

		DefaultMotionState motionState = new DefaultMotionState(mTransform);
		
		// 視覚とダイナミクスワールドを同期
		createRigidBody(motionState);

		// ワールドへ剛体を追加
		world.addRigidBody(mRigidBody);
	}

	// kinematic剛体の作成メソッド
	private void createRigidBody(DefaultMotionState motionState) {
		// CollisionShapeを作成
		CollisionShape shape = new CapsuleShapeZ(1f, 3f);

		// 剛体の作成情報を渡す
		Vector3f localInertia = new Vector3f(10f, 10f, 10f);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(3f, motionState, 
				shape, localInertia);
	
		// rbInfoを基に剛体を作成
		mRigidBody = new RigidBody(rbInfo);
		
		// kinematic剛体の設定
		mRigidBody.setCollisionFlags(mRigidBody.getCollisionFlags() 
				| CollisionFlags.KINEMATIC_OBJECT);
		mRigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
	}

	/**
	 * カメラ位置とkinematic剛体の位置を同期
	 * 
	 * @param gl
	 * @param eye
	 */
	public void sync(GL10 gl, Vector3f eye) {
		mTransform.origin.set(eye);
		mRigidBody.getMotionState().setWorldTransform(mTransform);
	}
}