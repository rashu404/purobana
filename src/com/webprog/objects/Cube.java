package com.webprog.objects;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.vecmath.Vector3f;

import android.content.Context;

import com.webprog.R;
import com.webprog.util.RenderUtil;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class Cube{	
	private RigidBody mRigidBody;
	
	private static FloatBuffer mVertexBuffer;
	private FloatBuffer mColorBuffer, mNormalBuffer;
	private ByteBuffer mIndexBuffer;

	private static int texture;
	private static int vbo;
	
	private Transform mTransform;
			
	public Cube(DynamicsWorld world, Vector3f position) {		
		// 配列をバッファへ格納
		createGeometry();

		// トランスフォーム（位置と回転）を初期化
		mTransform = new Transform();
		mTransform.setIdentity();
		mTransform.origin.set(position);

		DefaultMotionState motionState = new DefaultMotionState(mTransform);
		
		// 視覚とダイナミクスワールドを同期
		createRigidBody(motionState);

		// ワールドへ剛体を追加
		world.addRigidBody(mRigidBody);
	}

	private void createGeometry() {
		float vertices[] = {
			//左から３つが頂点、残り２つがテクスチャ
			1.f, 1.f, 1.f, 0.0f, 0.0f,
			-1.f, 1.f, 1.f, 0.0f, 1.0f,
			-1.f, -1.f, 1.f, 1.0f, 1.0f,
			1.f, -1.f, 1.f, 1.0f, 0.0f,

			1.f, 1.f, -1.f, 0.0f, 0.0f,
			1.f, -1.f, -1.f, 1.0f, 0.0f,
			-1.f, -1.f, -1.f, 1.0f, 1.f,
			-1.f, 1.f, -1.f, 0.0f, 1.0f,
			
			1.f, 1.f, 1.f, 0.0f, 0.0f,
			1.f, -1.f, 1.f, 0.0f, 1.0f,
			1.f, -1.f, -1.f, 1.0f, 1.0f,
			1.f, 1.f, -1.f, 1.0f, 0.0f,
                
			-1.f, -1.f, 1.f, 0.0f, 0.0f,
			-1.f, -1.f, -1.f, 0.0f, 1.0f,
			1.f, -1.f, -1.f, 1.0f, 1.0f,
			1.f, -1.f, 1.f, 1.0f, 0.0f,
                
			-1.f, 1.f, 1.f, 0.0f, 0.0f,
			1.f, 1.f, 1.f, 0.0f, 1.0f,
			1.f, 1.f, -1.f, 1.0f, 1.0f,
			-1.f, 1.f, -1.f, 1.0f, 0.0f,
                
			-1.f, 1.f, -1.f, 0.0f, 0.0f,
			-1.f, -1.f, -1.f, 0.0f, 1.0f,
			-1.f, -1.f, 1.f, 1.0f, 1.0f,
			-1.f, 1.f, 1.f, 1.0f, 0.0f,
		};

		float colors[] = {
				1.f, 1.f, 1.f, 1.f,
				1.f, 1.f, 1.f, 1.f,
				1.f, 1.f, 1.f, 1.f,
				1.f, 1.f, 1.f, 1.f,

                1.f, 1.f, 0.f, 1.f,
                1.f, 1.f, 0.f, 1.f,
                1.f, 1.f, 0.f, 1.f,
                1.f, 1.f, 0.f, 1.f,
                
                1.f, 0.f, 1.f, 1.f,
                1.f, 0.f, 1.f, 1.f,
                1.f, 0.f, 1.f, 1.f,
                1.f, 0.f, 1.f, 1.f,
                
                0.f, 1.f, 1.f, 1.f,
                0.f, 1.f, 1.f, 1.f,
                0.f, 1.f, 1.f, 1.f,
                0.f, 1.f, 1.f, 1.f,
                
                1.f, 0.f, 0.f, 1.f,
                1.f, 0.f, 0.f, 1.f,
                1.f, 0.f, 0.f, 1.f,
                1.f, 0.f, 0.f, 1.f,
                
                0.f, 1.f, 0.f, 1.f,
                0.f, 1.f, 0.f, 1.f,
                0.f, 1.f, 0.f, 1.f,
                0.f, 1.f, 0.f, 1.f,
		};

		float normals[] = {
				0.f, 0.f, 1.f, 0.f, 0.f, 1.f, 0.f, 0.f, 1.f, 0.f, 0.f, 1.f,
                0.f, 0.f, -1.f, 0.f, 0.f, -1.f, 0.f, 0.f, -1.f, 0.f, 0.f, -1.f,
                1.f, 0.f, 0.f, 1.f, 0.f, 0.f, 1.f, 0.f, 0.f, 1.f, 0.f, 0.f,
                0.f, -1.f, 0.f, 0.f, -1.f, 0.f, 0.f, -1.f, 0.f, 0.f, -1.f, 0.f,
                0.f, 1.f, 0.f, 0.f, 1.f, 0.f, 0.f, 1.f, 0.f, 0.f, 1.f, 0.f,
                -1.f, 0.f, 0.f, -1.f, 0.f, 0.f, -1.f, 0.f, 0.f, -1.f, 0.f, 0.f
		};

		byte indices[] = new byte[6 * 2 * 3]; // 6 faces, 2 triangles per face, 3 indices per triangle

		for(byte i = 0; i < 6; i++) {
			indices[i * 6 + 0] = (byte) (i * 4 + 0);
			indices[i * 6 + 1] = (byte) (i * 4 + 1);
			indices[i * 6 + 2] = (byte) (i * 4 + 2);

			indices[i * 6 + 3 + 0] = (byte) (i * 4 + 0);
			indices[i * 6 + 3 + 1] = (byte) (i * 4 + 2);
			indices[i * 6 + 3 + 2] = (byte) (i * 4 + 3);
		}

		// それぞれのバッファを作成
		mVertexBuffer = RenderUtil.allocateFloatBuffer(vertices);
		mColorBuffer = RenderUtil.allocateFloatBuffer(colors);
		mNormalBuffer = RenderUtil.allocateFloatBuffer(normals);
		mIndexBuffer = RenderUtil.allocateByteBuffer(indices);

	}
	private void createRigidBody(DefaultMotionState motionState) {
		// 初期化用に使いまわすVector3fインスタンス
		Vector3f initVec = new Vector3f(1f, 1f, 1f);
		
		// CollisionShapeを作成
		CollisionShape shape = new BoxShape(initVec);

		// 剛体の作成情報を渡す
		initVec.set(10f, 10f, 10f);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(10.f, motionState, shape, initVec);

		// rbInfoを基に剛体を作成
		mRigidBody = new RigidBody(rbInfo);

		// 反発係数を加える
		//mRigidBody.setRestitution(1.75f);
	}

	public void draw(GL10 gl) {
		mRigidBody.getMotionState().getWorldTransform(mTransform);

		float m[] = new float[16];
		mTransform.getOpenGLMatrix(m);

		gl.glPushMatrix();
		gl.glMultMatrixf(m, 0);
		
		// カリングの有効化
		gl.glEnable(GL10.GL_CULL_FACE);
		
		// 裏面を描画しない
		gl.glFrontFace(GL10.GL_CCW);
		gl.glCullFace(GL10.GL_BACK);
	
		// 各配列を有効化
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		GL11 gl11 = (GL11) gl;		

		// VBOの関連付け
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, vbo);
		gl11.glBufferData(GL11.GL_ARRAY_BUFFER, mVertexBuffer.capacity() * 4, mVertexBuffer, GL11.GL_STATIC_DRAW);
		
		{
			// 頂点配列・テクスチャ配列をOpenGL ES上で定義
            gl11.glVertexPointer(3, GL10.GL_FLOAT, 4 * 5, 0);
            gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 4 * 5, 4 * 3);
        }

		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

		// テクスチャを有効化
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);

		{
			// カラー配列・法線配列をOpenGL ES上で定義
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer);
		}

		// インデックスバッファを元に描画
		gl.glDrawElements(GL10.GL_TRIANGLES, 6 * 2 * 3, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);

		// 各配列を無効化
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		// テクスチャを無効化
		gl.glDisable(GL10.GL_TEXTURE_2D);
		
		// カリングを無効化
		gl.glDisable(GL10.GL_CULL_FACE);
		
		gl.glPopMatrix();
	}

	public void shootCube(Vector3f linVel){
		mRigidBody.activate(true);
		mRigidBody.setLinearVelocity(linVel);
	}

	public void setPosition(float x, float y, float z){
		mTransform.setIdentity();
		mTransform.origin.set(x, y, z);
		mRigidBody.activate(true);
		mRigidBody.setWorldTransform(mTransform);
	}
	
	public static void init(GL10 gl, Context context){
		int tex = RenderUtil.returnTex(gl, context, R.drawable.mokume2);
		int vboId = RenderUtil.makeFloatVBO((GL11)gl, mVertexBuffer);
		
		texture = tex;
		vbo = vboId;
	}
}