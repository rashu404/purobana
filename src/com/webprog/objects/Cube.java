package com.webprog.objects;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.vecmath.Vector3f;

import android.content.Context;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.webprog.R;
import com.webprog.render.World.Grobal;
import com.webprog.tool.RenderUtil;
import com.webprog.ui.FieldMap;

public final class Cube extends AbstractDynamicObject{
	private static int textureId;
	
	private static int bufferDataId;
	private static int indexDataId;
	
	private static CollisionShape boxShape;
	
	public Cube() {		
		this.fieldMapColor = FieldMap.CUBE_COLOR;
		this.syncDefMotionState(null);
	}
	
	public Cube(DynamicsWorld world, Vector3f position) {
		this.fieldMapColor = FieldMap.CUBE_COLOR;
		this.syncDefMotionState(position);
		
		// ワールドへ剛体を追加
		synchronized (world) { world.addRigidBody(rigidBody); }
		this.alreadyAddWorld = true;
	}

	private static void createGeometry(GL11 gl11) {
		float vertices[] = {
			//左から３、２、３の順で頂点・UV・法線座標
			1.2f, 1.2f, 1.2f, 0.0f, 0.0f,0.f, 0.f, 1.f,
			-1.2f, 1.2f, 1.2f, 0.0f, 1.0f,0.f, 0.f, 1.f,
			-1.2f, -1.2f, 1.2f, 1.0f, 1.0f,0.f, 0.f, 1.f,
			1.2f, -1.2f, 1.2f, 1.0f, 0.0f,0.f, 0.f, 1.f,

			1.2f, 1.2f, -1.2f, 0.0f, 0.0f, 0.f, 0.f, -1.f,
			1.2f, -1.2f, -1.2f, 1.0f, 0.0f, 0.f, 0.f, -1.f,
			-1.2f, -1.2f, -1.2f, 1.0f, 1.0f, 0.f, 0.f, -1.f,
			-1.2f, 1.2f, -1.2f, 0.0f, 1.0f, 0.f, 0.f, -1.f,
			
			1.2f, 1.2f, 1.2f, 0.0f, 0.0f, 1.f, 0.f, 0.f,
			1.2f, -1.2f, 1.2f, 0.0f, 1.0f, 1.f, 0.f, 0.f,
			1.2f, -1.2f, -1.2f, 1.0f, 1.0f, 1.f, 0.f, 0.f,
			1.2f, 1.2f, -1.2f, 1.0f, 0.0f, 1.f, 0.f, 0.f,
                
			-1.2f, -1.2f, 1.2f, 0.0f, 0.0f, 0.f, -1.f, 0.f,
			-1.2f, -1.2f, -1.2f, 0.0f, 1.0f, 0.f, -1.f, 0.f,
			1.2f, -1.2f, -1.2f, 1.0f, 1.0f, 0.f, -1.f, 0.f,
			1.2f, -1.2f, 1.2f, 1.0f, 0.0f, 0.f, -1.f, 0.f,
                
			-1.2f, 1.2f, 1.2f, 0.0f, 0.0f, 0.f, 1.f, 0.f,
			1.2f, 1.2f, 1.2f, 0.0f, 1.0f, 0.f, 1.f, 0.f,
			1.2f, 1.2f, -1.2f, 1.0f, 1.0f, 0.f, 1.f, 0.f,
			-1.2f, 1.2f, -1.2f, 1.0f, 0.0f, 0.f, 1.f, 0.f,
                
			-1.2f, 1.2f, -1.2f, 0.0f, 0.0f, -1.f, 0.f, 0.f,
			-1.2f, -1.2f, -1.2f, 0.0f, 1.0f, -1.f, 0.f, 0.f,
			-1.2f, -1.2f, 1.2f, 1.0f, 1.0f, -1.f, 0.f, 0.f,
			-1.2f, 1.2f, 1.2f, 1.0f, 0.0f, -1.f, 0.f, 0.f,
		};

		byte indices[] = new byte[6 * 2 * 3];

		for(int i = 0; i < 6; i++) {
			indices[i * 6 + 0] = (byte) (i * 4 + 0);
			indices[i * 6 + 1] = (byte) (i * 4 + 1);
			indices[i * 6 + 2] = (byte) (i * 4 + 2);

			indices[i * 6 + 3 + 0] = (byte) (i * 4 + 0);
			indices[i * 6 + 3 + 1] = (byte) (i * 4 + 2);
			indices[i * 6 + 3 + 2] = (byte) (i * 4 + 3);
		}

		bufferDataId = RenderUtil.makeFloatVBO(gl11, vertices);
		indexDataId = RenderUtil.makeByteIndexVBO(gl11, indices);
	}
	
	@Override
	protected void createRigidBody(DefaultMotionState motionState) {
		// 初期化用に使いまわすVector3fインスタンス
		Vector3f initVec = Grobal.tmpVec;
		
		if(boxShape == null){
			initVec.set(1.2f, 1.2f, 1.2f);
			boxShape = new BoxShape(initVec);
		}
		
		// 剛体の慣性
		initVec.set(10f, 10f, 10f);

		this.rigidBody = new RigidBody(10.f, motionState, boxShape, initVec);
	}

	@Override
	public void draw(GL10 gl) {
		super.draw(gl);
		
		gl.glPushMatrix();
		gl.glMultMatrixf(this.matrix, 0);
				
		((GL11)gl).glDrawElements(GL10.GL_TRIANGLES, 6 * 2 * 3, GL10.GL_UNSIGNED_BYTE, 0);
		
		gl.glPopMatrix();
	}
	
	public static void bind(GL10 gl){
		// カリングを有効化し、裏面を描画しない
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glFrontFace(GL10.GL_CCW);
		gl.glCullFace(GL10.GL_BACK);
			
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		GL11 gl11 = (GL11) gl;		

		// VBOの関連付け
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, bufferDataId);
				
		gl11.glVertexPointer(3, GL10.GL_FLOAT, 4 * 8, 0);
		gl11.glTexCoordPointer(2, GL10.GL_FLOAT, 4 * 8, 4 * 3);
		gl11.glNormalPointer(GL10.GL_FLOAT, 4 * 8, 4 * 5);

		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, indexDataId);
				
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
	}
	
	public static void unBind(GL10 gl){
		gl.glDisable(GL10.GL_TEXTURE_2D);
		
		GL11 gl11 = (GL11) gl;
		
		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
		gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
				
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				
		gl.glDisable(GL10.GL_CULL_FACE);
	}

	public void shootCube(Vector3f linVel){
		RigidBody rb = rigidBody;
		rb.activate(true);
		rb.setLinearVelocity(linVel);
		Vector3f angVel = Grobal.tmpVec;
		angVel.set(0, 0, 0);
		rb.setAngularVelocity(angVel);
	}
	
	public static void init(GL10 gl, Context context){
		createGeometry((GL11) gl);
		textureId = RenderUtil.loadTex(gl, context, R.drawable.wood_box);
	}
}