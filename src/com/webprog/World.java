package com.webprog;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;

import android.R.integer;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.text.style.BulletSpan;
import android.util.Log;

class World implements GLSurfaceView.Renderer {
	Context mContext;
	WorldObject mObjects[];
	Cube mCubeBullets[];
	Cube mOb1;
	DynamicsWorld mDynamicsWorld;
	GL10 gl10;
	
	FPSCounter fpsCounter;
	
	private int bullets;
	
	private float eyeX = 2.f, eyeY = 10.f, eyeZ = 3.f;
	private float lookX = 0.f, lookY = 0, lookZ = 1;
	private float upX = 0, upY = 0, upZ = 1;
	
	float rotateX = 0.0f;
	
	private int shootNum = -1;
	private boolean shootSwitch = false;
	
	private float bgColor = 1.0f, bgColorB = 0.83f; 
	
	private boolean dark = false;
	private boolean isTouch = false;
	
	private float width, height;
	
	Vector3f mCubePos;
	boolean posSwitch = false;
	
	int iTime = 0;
	boolean rainSwitch = false;
	
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
		//mObjects[4] = new Mountain(mDynamicsWorld, new Vector3f(-5, -25, 10));
		
		bullets = 10;
		
		mCubeBullets = new Cube[bullets];
		
		mCubePos = new Vector3f(5, 3, 4);
		
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
		//gl.glClearColor(0f, 0f, 0f, 1.0f); //夜
		gl.glClearColor(bgColor, bgColor, bgColorB, 1.0f); //昼
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glEnable(GL10.GL_DEPTH_TEST);
			
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		if(posSwitch == false)
			GLU.gluLookAt(gl, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
		else 
			GLU.gluLookAt(gl, mCubePos.x + 2, mCubePos.y + 2, mCubePos.z + 2, mCubePos.x, mCubePos.y, mCubePos.z, upX, upY, upZ);
		
		
		//gl.glRotatef(rotateX, 0, 0, 1);
		//rotateX += 0.75f;
		
		Utils.enableMaterial(gl, dark);
		
		if(isTouch){
			eyeX += 0.5f;
			lookX += 0.5f;
		}
		
		for(WorldObject mObject:mObjects){
			mObject.draw(gl);
		}
		
		if(shootSwitch)
			mOb1.draw(gl);
		
		if(shootNum >= 0){
			for(Cube mBullet:mCubeBullets){
				if(mBullet != null){
						mBullet.draw(gl);
				}
			}
		}
		
		if(rainSwitch == true){
			if(iTime % 8 == 0){
				fallingCube();
			}
			
			iTime += 1f;
		}
				
		try{
			mDynamicsWorld.stepSimulation(0.33f);
		}catch(NullPointerException e){
			Log.d("NullPo", "ぬるぽ回避");
		}catch(ArrayIndexOutOfBoundsException e2){
			Log.d("ArrayIndex", "ArrayIndex回避");
		}
		
		//fpsCounter.logFrame();
		
	}
	public void shootInit(){
		mOb1 = new Cube(mDynamicsWorld, new Vector3f(0, -10, 2));

		Vector3f linVel = new Vector3f(0, 50, 0);

		mOb1.shootCube(linVel);
		
		shootSwitch = true;
	}
	
	public void shootInit(Vector3f linVel){
		if(shootNum == -1)
			shootNum = 0;
		else if(shootNum >= 0 && shootNum < bullets - 1)
			shootNum++;
		else {
			for(int i = 0; i < bullets - 1; i++){
				mDynamicsWorld.removeRigidBody(mCubeBullets[i].getRigidBody());
				mCubeBullets[i] = null;
			}
			shootNum = 0;
			
		}
		
		mCubeBullets[shootNum] = new Cube(mDynamicsWorld, new Vector3f(eyeX, eyeY, eyeZ));
		
		linVel.normalize();
		linVel.scale(35f);
		
		mCubeBullets[shootNum].shootCube(linVel);
		
		//posSwitch = true;
		
	}
	public void fallingSwitch(boolean rainSwitch){
		this.rainSwitch = rainSwitch;
	}
	
	public void fallingCube(){		
		if(shootNum == -1)
			shootNum = 0;
		else if(shootNum >= 0 && shootNum < bullets - 1)
			shootNum++;
		else {
			for(int i = 0; i < bullets - 1; i++){
				mDynamicsWorld.removeRigidBody(mCubeBullets[i].getRigidBody());
				mCubeBullets[i] = null;
			}
			shootNum = 0;
			
		}
		
		Random random = new Random();
		
		float fallX = random.nextFloat() * 20.f;
		float fallY = random.nextFloat() * -10.f;
		float fallZ = random.nextFloat() * 10.f + 1f;
		
		if(random.nextBoolean())
			fallX = -fallX;
		if(random.nextBoolean())
			fallY = -fallY;
		
		mCubeBullets[shootNum] = new Cube(mDynamicsWorld, new Vector3f(fallX, fallY, fallZ));
		mCubeBullets[shootNum].shootCube(new Vector3f(0, 0, -100));
	}
	
	public Vector3f getRayTo(int x, int y){
		float top = 1f;
		float bottom = -1f;
		float nearPlane = 1f;
		float tanFov = (top - bottom) * 0.5f / nearPlane;
		float fov = 2f * (float) Math.atan(tanFov);

		Vector3f rayFrom = new Vector3f(eyeX, eyeY, eyeZ);
		Vector3f rayForward = new Vector3f();
		rayForward.sub(new Vector3f(lookX, lookY, lookZ), new Vector3f(eyeX, eyeY, eyeZ));
		rayForward.normalize();
		float farPlane = 10000f;
		rayForward.scale(farPlane);

		Vector3f vertical = new Vector3f(upX, upY, upZ);

		Vector3f hor = new Vector3f();
		hor.cross(rayForward, vertical);
		hor.normalize();
		vertical.cross(hor, rayForward);
		vertical.normalize();

		float tanfov = (float) Math.tan(0.5f * fov);
		
		float aspect = height / width;
		
		hor.scale(2f * farPlane * tanfov);
		vertical.scale(2f * farPlane * tanfov);
		
		if (aspect < 1f) {
			hor.scale(1f / aspect);
		}
		else {
			vertical.scale(aspect);
		}
		
		Vector3f rayToCenter = new Vector3f();
		rayToCenter.add(rayFrom, rayForward);
		Vector3f dHor = new Vector3f(hor);
		dHor.scale(1f / width);
		Vector3f dVert = new Vector3f(vertical);
		dVert.scale(1.f / (float) height);

		Vector3f tmp1 = new Vector3f();
		Vector3f tmp2 = new Vector3f();
		tmp1.scale(0.5f, hor);
		tmp2.scale(0.5f, vertical);

		Log.d("tmp1", "X=" + tmp1.x + "Y=" + tmp1.y + "Z=" + tmp1.z);
		Log.d("tmp2", "X=" + tmp2.x + "Y=" + tmp2.y + "Z=" + tmp2.z);
		
		Vector3f rayTo = new Vector3f();
		rayTo.sub(rayToCenter, tmp1);
		
		Log.d("rayTo1", "X=" + rayTo.x + "Y=" + rayTo.y + "Z=" + rayTo.z);
		
		rayTo.add(tmp2);

		tmp1.scale(x, dHor);
		tmp2.scale(y, dVert);

		rayTo.add(tmp1);
		rayTo.sub(tmp2);
		return rayTo;
	}
	
	public void darkSwitch(){
		if(dark){
			dark = false;
			bgColor = 1.0f;
			bgColorB = 0.83f; 
		}else if(!dark){
			dark = true;
			bgColor = 0.f;
			bgColorB = 0.f;
		}
	}
	
	public void translateX(boolean isTouch){
		this.isTouch = isTouch;
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		float mFovy = 90;
		
		this.width = (float) width;
		this.height = (float) height;
		
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
		
		gl10 = gl;
	}
}
