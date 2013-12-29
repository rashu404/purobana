package com.webprog.objects;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.vecmath.Vector3f;

import android.content.Context;

import com.webprog.R;
import com.webprog.render.World.Grobal;
import com.webprog.tool.RenderUtil;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.linearmath.*;

public final class Cube{	
	private RigidBody rigidBody;
	private Transform transform;	
	
	private float[] matrix = new float[16];
	
	private static FloatBuffer vertexBuffer;
	private static FloatBuffer normalBuffer;
	private static ByteBuffer indexBuffer;

	private static int texture;
	private static int vbo;
	
	public Cube(DynamicsWorld world, Vector3f position) {		
		// 配列をバッファへ格納
		if( this.isBufferNull() ) createGeometry();
		
		// トランスフォーム（位置と回転）を初期化
		this.transform = new Transform();
		this.transform.setIdentity();
		this.transform.origin.set(position);

		DefaultMotionState motionState = new DefaultMotionState(transform);
		
		// 視覚とダイナミクスワールドを同期
		this.createRigidBody(motionState);

		// ワールドへ剛体を追加
		world.addRigidBody(rigidBody);
	}
	
	private boolean isBufferNull(){
		return vertexBuffer == null && normalBuffer == null && indexBuffer == null;
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
		vertexBuffer = RenderUtil.allocateFloatBuffer(vertices);
		normalBuffer = RenderUtil.allocateFloatBuffer(normals);
		indexBuffer = RenderUtil.allocateByteBuffer(indices);

	}
	private void createRigidBody(DefaultMotionState motionState) {
		// 初期化用に使いまわすVector3fインスタンス
		Vector3f initVec = Grobal.tmpVec;
		initVec.set(1f, 1f, 1f);
		
		// CollisionShapeを作成
		CollisionShape shape = new BoxShape(initVec);

		// 剛体の慣性をセット
		initVec.set(10f, 10f, 10f);

		// rbInfoを基に剛体を作成
		rigidBody = new RigidBody(10.f, motionState, shape, initVec);

		// 反発係数を加える
		//mRigidBody.setRestitution(1.75f);
	}

	public void draw(GL10 gl) {
		this.rigidBody.getMotionState().getWorldTransform(this.transform);

		this.transform.getOpenGLMatrix(matrix);

		gl.glPushMatrix();
		gl.glMultMatrixf(this.matrix, 0);
		
		// カリングの有効化
		gl.glEnable(GL10.GL_CULL_FACE);
		
		// 裏面を描画しない
		gl.glFrontFace(GL10.GL_CCW);
		gl.glCullFace(GL10.GL_BACK);
	
		// 各配列を有効化
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		GL11 gl11 = (GL11) gl;		

		// VBOの関連付け
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, vbo);
		gl11.glBufferData(GL11.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GL11.GL_STATIC_DRAW);
		
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
			// 法線配列をOpenGL ES上で定義
			gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);
		}

		// インデックスバッファを元に描画
		gl.glDrawElements(GL10.GL_TRIANGLES, 6 * 2 * 3, GL10.GL_UNSIGNED_BYTE, indexBuffer);

		// 各配列を無効化
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		// テクスチャを無効化
		gl.glDisable(GL10.GL_TEXTURE_2D);
		
		// カリングを無効化
		gl.glDisable(GL10.GL_CULL_FACE);
		
		gl.glPopMatrix();
	}

	public void shootCube(Vector3f linVel){
		RigidBody rb = rigidBody;
		rb.activate(true);
		rb.setLinearVelocity(linVel);
	}

	public void setPosition(float x, float y, float z){
		Transform tf = transform;
		tf.setIdentity();
		tf.origin.set(x, y, z);
		
		RigidBody rb = rigidBody;
		rb.activate(true);
		rb.setWorldTransform(transform);
	}
	
	public void setPosition(Vector3f pos){
		Transform tf = transform;
		tf.setIdentity();
		tf.origin.set(pos);
		
		RigidBody rb = rigidBody;
		rb.activate(true);
		rb.setWorldTransform(transform);
	}
	
	public void initPosition(Vector3f defLinVel, Vector3f defRotate, float x, float y, float z){
		Transform tf = transform;
		tf.setIdentity();
		tf.origin.set(x, y, z);
		
		RigidBody rb = rigidBody;
		rb.setLinearVelocity(defLinVel);
		rb.setAngularVelocity(defRotate);
		rb.activate(true);
		rb.setWorldTransform(transform);
	}
	
	public RigidBody getRigidBody(){
		return rigidBody;
	}
	
	public static void init(GL10 gl, Context context){
		texture = RenderUtil.loadTex(gl, context, R.drawable.mokume2);
		vbo = RenderUtil.makeFloatVBO((GL11)gl, vertexBuffer);
	}
}