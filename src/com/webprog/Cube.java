package com.webprog;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.webprog.R;
import android.content.Context;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class Cube implements World.WorldObject {
	RigidBody mRigidBody;
	private FloatBuffer mVertexBuffer, mColorBuffer, mNormalBuffer;
	private ByteBuffer mIndexBuffer;

	private int posBufferObject = 0;
	private int mTexture;

	public Cube(DynamicsWorld world, Vector3f position) {
		createGeometry();
		//バッファへ配列を格納
		
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(position);
		
		transform.setRotation(new Quat4f(0.f, 0.f, 0.f, 1.f));
		//トランスフォームの初期化
		
		DefaultMotionState motionState = new DefaultMotionState(transform);
		
		createRigidBody(motionState);
		//MotionStateで視覚とダイナミクスワールドを同期
		
		world.addRigidBody(mRigidBody);
		//ワールドへ剛体を追加
		
	}

	private void createGeometry() {
		float vertices[] = {
			//左から３つが頂点、残り２つがテクスチャ
			1.f, 1.f, 1.f, 0.0f, 0.0f,
			-1.f, 1.f, 1.f, 0.0f, 1.0f,
			-1.f, -1.f, 1.f, 1.0f, 1.0f,
			1.f, -1.f, 1.f, 1.0f, 0.0f,
				
			1.f, 1.f, -1.f, 0.0f, 0.0f,
			-1.f, 1.f, -1.f, 0.0f, 1.0f,
			-1.f, -1.f, -1.f, 1.0f, 1.0f,
			1.f, -1.f, -1.f, 1.0f, 0.0f,
                
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
		
		mVertexBuffer = Utils.allocateFloatBuffer(vertices);
		mColorBuffer = Utils.allocateFloatBuffer(colors);
		mNormalBuffer = Utils.allocateFloatBuffer(normals);
		mIndexBuffer = Utils.allocateByteBuffer(indices);
		
	}
	private void createRigidBody(DefaultMotionState motionState) {
		CollisionShape shape = new BoxShape(new Vector3f(1.f, 1.f, 1.f));
		//CollisionShapeを作成
		
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(10.f, motionState, shape, new Vector3f(10.f, 10.f, 10.f));
		//剛体の作成情報を渡す
		
		mRigidBody = new RigidBody(rbInfo);
		//rbInfoを基に剛体を作成
		
		mRigidBody.setRestitution(1.75f);
		//反発
	}

	@Override
	public void draw(GL10 gl) {
		Transform transform = new Transform();
		mRigidBody.getMotionState().getWorldTransform(transform);
		
		float m[] = new float[16];
		transform.getOpenGLMatrix(m);
		
		gl.glPushMatrix();
		gl.glMultMatrixf(m, 0);
		
		gl.glFrontFace(GL10.GL_CW);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		GL11 gl11 = (GL11) gl;
		{
			int[] buffers = new int[1];
			gl11.glGenBuffers(1, buffers, 0);
			posBufferObject = buffers[0];
			
			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, posBufferObject);
			gl11.glBufferData(GL11.GL_ARRAY_BUFFER, mVertexBuffer.capacity() * 4, mVertexBuffer, GL11.GL_STATIC_DRAW);
		}
		
		{
            gl11.glVertexPointer(3, GL10.GL_FLOAT, 4 * 5, 0);
            gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 4 * 5, 4 * 3);
        }
		
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture);
		 
		{
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer);
		}

		gl.glDrawElements(GL10.GL_TRIANGLES, 6 * 2 * 3, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		
		gl.glPopMatrix();
		
	}
	
	@Override
	public void init(GL10 gl, Context context) {
		mTexture = Utils.returnTex(gl, context, R.drawable.mokume2);
		//Utils.enableMaterial(gl);
	}
	
	public void shootCube(Vector3f linVel){
		mRigidBody.setLinearVelocity(linVel);
		mRigidBody.setAngularVelocity(new Vector3f(0f, 0f, 0f));
		
	}
	public RigidBody getRigidBody(){
		return mRigidBody;
	}
}
