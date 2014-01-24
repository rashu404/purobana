package com.webprog.objects;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public abstract class AbstractDynamicObject {
	protected int fieldMapColor;
	
	protected RigidBody rigidBody;
	protected Transform transform;
	
	protected float[] matrix = new float[16];
	
	protected boolean alreadyAddWorld;
	
	protected void syncDefMotionState(Vector3f position) {
		float x = 0, y = 0, z = 0;
		if(position != null){
			x = position.x;
			y = position.y;
			z = position.z;
		}
		
		this.transform = new Transform();
		this.transform.setIdentity();
		this.transform.origin.set(x, y, z);

		DefaultMotionState motionState = new DefaultMotionState(this.transform);
		
		this.createRigidBody(motionState);
	}
	
	protected abstract void createRigidBody(DefaultMotionState motionState);
	
	public void draw(GL10 gl){
		this.rigidBody.getMotionState().getWorldTransform(this.transform);

		this.transform.getOpenGLMatrix(matrix);
	}
	
	public void setPosition(float x, float y, float z){
		this.transform.setIdentity();
		this.transform.origin.set(x, y, z);
		this.rigidBody.activate(true);
		this.rigidBody.setWorldTransform(this.transform);
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
		this.transform.setIdentity();
		this.transform.origin.set(x, y, z);
		
		this.rigidBody.setLinearVelocity(defLinVel);
		this.rigidBody.setAngularVelocity(defRotate);
		this.rigidBody.activate(true);
		this.rigidBody.setWorldTransform(this.transform);
	}
	
	public boolean alreadyAddWorld(){
		return this.alreadyAddWorld;
	}
	
	public void addWorld(DynamicsWorld world){
		if(!this.alreadyAddWorld) {			
			synchronized(world) { world.addRigidBody(this.rigidBody); }
		}
		
		this.alreadyAddWorld = true;
	}
	
	public RigidBody getRigidBody(){
		return this.rigidBody;
	}
	
	public Transform getTransform() {
		return this.transform;
	}
	
	public int getFieldMapColor(){
		return this.fieldMapColor;
	}
}