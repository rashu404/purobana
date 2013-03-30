package com.webprog;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

class World implements GLSurfaceView.Renderer {
	Context mContext;
	WorldObject mObjects[];
	DynamicsWorld mDynamicsWorld;
	
	float eyeX = 7.f, eyeY = 2.f, eyeZ = 2.f;
	//X=5,Y=2,Z=5
	float rotateX = 10.f;
	
	private Cube mOb1;
	private int shootSwitch = 0;
	private Vector3f linVel;
	
	private float mFovy = 90;
	
	public interface WorldObject{
		public void init(GL10 gl, Context context);
		public void draw(GL10 gl);
	}
	
	public World(Context context){
		mContext = context;
		initPhysics();
		
		mObjects = new WorldObject[2 + 2];
		
		mObjects[0] = new Cube(mDynamicsWorld, new Vector3f(0, 0, 3));
		mObjects[1] = new Cube(mDynamicsWorld, new Vector3f(0, 0, 5));
		
		mObjects[2] = new Ground(mDynamicsWorld);
		mObjects[3] = new Sky(mDynamicsWorld);
	}

	private void initPhysics() {
		DbvtBroadphase broadphase = new DbvtBroadphase();
		
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		
		mDynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		mDynamicsWorld.setGravity(new Vector3f(0.0f, 0.0f, -10.0f));
		
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClearColor(1.0f, 1.0f, 0.83f, 1.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL10.GL_DEPTH_TEST);
				
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		GLU.gluLookAt(gl, eyeX, eyeY, eyeZ, 0, 0, 0, 0, 0, 1);
		//eyeX += 1f;
		
		gl.glRotatef(rotateX, 0, 0, 1);
		rotateX += 0.3f;
		
		if(shootSwitch >= 1){
			mOb1.draw(gl);
		}
		
		for(WorldObject mObject:mObjects){
			mObject.draw(gl);
		}
		
		mDynamicsWorld.stepSimulation(0.33f);
	}
	
	public void shootInit(){
		mOb1 = new Cube(mDynamicsWorld, new Vector3f(0, -10, 2));
		
		linVel = new Vector3f(0, 50, 0);
		
		mOb1.shootCube(linVel);
		
		shootSwitch = 1;
		
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, mFovy, (float)width / height, 1.f, 1000.f);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		for(WorldObject mObject:mObjects){
			mObject.init(gl, mContext);
		}
		Utils.enableLight(gl);
	}
}
