package com.webprog.objects;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

/**
 *  カメラ位置（eye）のCollisionShapeを作成・同期するクラス
 */
public class Myself{	
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
		// 初期化用に使いまわすインスタンス
		Vector3f initVec = new Vector3f(1f, 1f, 3f);
		
		// CollisionShapeを作成
		CollisionShape shape = new BoxShape(initVec);

		// 剛体の作成情報を渡す
		initVec.set(10f, 10f, 10f);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(3f, motionState, shape, initVec);
		
		// rbInfoを基に剛体を作成
		mRigidBody = new RigidBody(rbInfo);
		
		// kinematic剛体の設定
		mRigidBody.setCollisionFlags(mRigidBody.getCollisionFlags() | CollisionFlags.KINEMATIC_OBJECT);
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