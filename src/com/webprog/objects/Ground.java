package com.webprog.objects;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.vecmath.Vector3f;

import android.content.Context;

import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.webprog.R;
import com.webprog.render.World.Grobal;
import com.webprog.tool.RenderUtil;

public final class Ground {
	private FloatBuffer vertexBuffer;
	private ByteBuffer normalBuffer;
	private ByteBuffer indexBuffer;

	private int texture;
	private int vbo;
	
	public Ground(DynamicsWorld world) {
		createGeometry();
		
		RigidBody rigidBody = createRigidBody();
		world.addRigidBody(rigidBody);
	}

	private void createGeometry() {
		float vertices[] = {
				-1000.f, -1000.f, 0.f, 0.0f, 0.0f, 
				-1000.f, 1000.f, 0.f, 0.0f, 300.0f,
				1000.f, -1000.f, 0.f, 300.0f, 0.0f, 
				1000.f, 1000.f, 0.f, 300.0f, 300.0f,
		};
		
		byte normals[] = {
				0, 0, 1,
				0, 0, 1,
				0, 0, 1,
				0, 0, 1,
		};

		byte indices[] = { 0, 1, 2, 3, };

		this.vertexBuffer = RenderUtil.allocateFloatBuffer(vertices);
		this.normalBuffer = RenderUtil.allocateByteBuffer(normals);
		this.indexBuffer = RenderUtil.allocateByteBuffer(indices);
	}

	// 静的剛体の作成
	private RigidBody createRigidBody() {
		Vector3f planeNormal = Grobal.tmpVec;
		planeNormal.set(0.f, 0.f, 1.f);
		
		// 静的剛体のCollisionShapeを作成
		CollisionShape shape = new StaticPlaneShape(planeNormal, 0);
		
		DefaultMotionState motionState = new DefaultMotionState();

		return new RigidBody(0, motionState, shape);
	}

	public void draw(GL10 gl) {
		gl.glPushMatrix();

		// カリング（指定した面を描画しない）を有効化
		gl.glEnable(GL10.GL_CULL_FACE);

		// 裏面を描画しない
		gl.glFrontFace(GL10.GL_CW);
		gl.glCullFace(GL10.GL_BACK);
		
		// 頂点配列・テクスチャ配列を有効化
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		GL11 gl11 = (GL11) gl;
		
		// VBOを関連付け
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, vbo);
		gl11.glBufferData(GL11.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GL11.GL_STATIC_DRAW);
		
		{
			// 頂点配列・テクスチャ配列をOpenGL ES上で定義
			gl11.glVertexPointer(3, GL10.GL_FLOAT, 4 * 5, 0);
			gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 4 * 5, 4 * 3);
		}

		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

		// テクスチャの有効化と関連付け
		gl.glEnable(GL11.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
		
		{
			// 法線配列をOpenGL ES上で定義
			gl.glNormalPointer(GL10.GL_UNSIGNED_BYTE, 0, normalBuffer);
		}
		
		// インデックスバッファを元に描画
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, indexBuffer);
		
		// 頂点配列・テクスチャ配列の無効化
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// テクスチャの無効化
		gl.glDisable(GL11.GL_TEXTURE_2D);
		
		// カリングの無効化
		gl.glDisable(GL10.GL_CULL_FACE);

		gl.glPopMatrix();
	}

	public void init(GL10 gl, Context context) {
		texture = RenderUtil.loadTex(gl, context, R.drawable.green_field);
		vbo = RenderUtil.makeFloatVBO((GL11)gl, vertexBuffer);
	}

}
