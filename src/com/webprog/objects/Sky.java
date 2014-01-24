package com.webprog.objects;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.vecmath.Vector3f;

import android.content.Context;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.webprog.R;
import com.webprog.render.World.Grobal;
import com.webprog.tool.RenderUtil;

public final class Sky {
	private int textureId;
	
	private int bufferDataId;
	private int indexDataId;

	public Sky(DynamicsWorld world) {
		RigidBody rigidBody = this.createRigidBody();
		world.addRigidBody(rigidBody);
	}

	private void createGeometry(GL11 gl11) {
		short vertices[] = {
				-1000, -1000, 50, 0, 0,
				-1000, 1000, 50, 0, 1,
				1000, -1000, 50, 1, 0,
				1000, 1000, 50, 1, 1,
		};
		
		byte indices[] = { 0, 1, 2, 3, };

		this.bufferDataId = RenderUtil.makeShortVBO(gl11, vertices);
		this.indexDataId = RenderUtil.makeByteIndexVBO(gl11, indices);
	}

	private RigidBody createRigidBody() {
		Vector3f planeNormal = Grobal.tmpVec;
		planeNormal.set(0.f, 0.f, 1.f);
		
		CollisionShape shape = new StaticPlaneShape(planeNormal, 0);

		DefaultMotionState motionState = new DefaultMotionState();

		return new RigidBody(0, motionState, shape);
	}

	public void draw(GL10 gl) {		
		gl.glPushMatrix();

		// カリングを有効化し、裏面を描画しない
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glFrontFace(GL10.GL_CCW);
		gl.glCullFace(GL10.GL_BACK);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		GL11 gl11 = (GL11) gl;
		
		// VBOの関連付け
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, bufferDataId);
		
		gl11.glVertexPointer(3, GL10.GL_SHORT, 2 * 5, 0);
		gl11.glTexCoordPointer(2, GL10.GL_SHORT, 2 * 5, 2 * 3);
		
		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, indexDataId);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
		
		gl11.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4, GL10.GL_UNSIGNED_BYTE, 0);
		
		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glDisable(GL10.GL_TEXTURE_2D);
		
		gl.glDisable(GL10.GL_CULL_FACE);

		gl.glPopMatrix();
	}

	public void init(GL10 gl, Context context) {
		this.createGeometry((GL11)gl);
		this.textureId = RenderUtil.loadTex(gl, context, R.drawable.blue_sky);
	}

}