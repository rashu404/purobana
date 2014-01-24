package com.webprog.render;

import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.content.Context;
import android.os.Handler;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.webprog.objects.AbstractDynamicObject;
import com.webprog.objects.Cube;
import com.webprog.objects.DominoPlate;
import com.webprog.objects.Ground;
import com.webprog.objects.Sky;
import com.webprog.tool.PhysicsUtil;
import com.webprog.tool.RenderUtil;

public final class World{
	private static final int CUBE_LENGTH = 2;
	private static final int MAX_CUBE_BULLET = 8;
	private static final int DOMINO_PLATE_LENGTH = 6;
	
	private DynamicsWorld dynamicsWorld;

	private Cube[] cubes = new Cube[CUBE_LENGTH];
	private Cube[] cubeBullets = new Cube[MAX_CUBE_BULLET];
	private DominoPlate[] dominoPlates = new DominoPlate[DOMINO_PLATE_LENGTH];
	
	private Ground ground;
	private Sky sky;
	
	private GL10 gl10;
	
	private int cShootBullet;
	private boolean isShoot, isDark;
	
	public static class Grobal{
		public static final Vector3f tmpVec = new Vector3f();
		public static final Vector3f tmpVec2 = new Vector3f();
	}
	
	public World() {
		this.dynamicsWorld = PhysicsUtil.getInitDynamicsWorld();
		this.ground = new Ground(dynamicsWorld);
		this.sky = new Sky(dynamicsWorld);
		
		new GenObjectHandler().schedule(200, 500);
		while(this.generateCubeBullets());
	}
	
	private boolean generateDefaultObjects(){
		boolean isDefObjNull = false;
		
		for(int i = 0; i < CUBE_LENGTH; i++){
			if(this.cubes[i] != null) continue;
			
			Vector3f genPos = Grobal.tmpVec;
			genPos.set(0, 0, i+i+3);
			this.cubes[i] = new Cube(dynamicsWorld, genPos);
				
			isDefObjNull = true;
			return isDefObjNull;
		}
		
		for(int i = 0; i < DOMINO_PLATE_LENGTH; i++){
			if(this.dominoPlates[i] != null) continue;
			
			Vector3f genPos = Grobal.tmpVec;
			genPos.set(-5, i * -4.25f, 4);
			this.dominoPlates[i] = new DominoPlate(dynamicsWorld, genPos);
			
			isDefObjNull = true;
			return isDefObjNull;
		}
		
		return isDefObjNull;
	}
	
	private boolean generateCubeBullets(){
		boolean isCubeNull = false;
		for(int i = 0; i < MAX_CUBE_BULLET; i++){
			if(this.cubeBullets[i] != null) continue;
		
			this.cubeBullets[i] = new Cube();	
			isCubeNull = true;
			break;
		}
		return isCubeNull;
	}
	
	public void onDraw(GL10 gl) {
		this.drawObjcets(gl);
		synchronized (this.dynamicsWorld) { this.dynamicsWorld.stepSimulation(0.33f); }
	}
	
	private void drawObjcets(GL10 gl){	
		Cube.bind(gl);
		for(int i = 0; i < MAX_CUBE_BULLET && this.isShoot && this.hasCBullet(i); i++) this.cubeBullets[i].draw(gl);
		for(int i = 0; i < CUBE_LENGTH && this.hasDefCube(i); i++)	this.cubes[i].draw(gl);
		Cube.unBind(gl);
		
		DominoPlate.bind(gl);
		for(int i = 0; i < DOMINO_PLATE_LENGTH && this.hasDomino(i); i++) this.dominoPlates[i].draw(gl);
		DominoPlate.unBind(gl);
		
		this.ground.draw(gl);
		this.sky.draw(gl);
	}

	private boolean hasDefCube(int i){
		return this.cubes[i] != null;
	}
	
	private boolean hasDomino(int i){
		return this.dominoPlates[i] != null;
	}
	
	private boolean hasCBullet(int i){
		return this.cubeBullets[i].alreadyAddWorld() && this.cubeBullets[i] != null;
	}

	public void shootCube(Vector3f linVel, Vector3f genPos) {		
		// キューブ弾の上限を超えたら最古のインスタンスを再利用する
		if (cShootBullet >= MAX_CUBE_BULLET) cShootBullet = 0;
		int csb = cShootBullet;
		
		if(cubeBullets[csb] == null){
			cubeBullets[csb] = new Cube(dynamicsWorld, genPos);
		}else {
			cubeBullets[csb].addWorld(this.dynamicsWorld);
			cubeBullets[csb].setPosition(genPos);		
		}
		
		linVel.normalize();
		linVel.scale(25f);

		cubeBullets[csb].shootCube(linVel);

		cShootBullet++;
		isShoot = true;
	}
	
	public void darkSwitch() {
		this.isDark = !this.isDark;
				
		if(this.isDark){
			this.setDark();
		}else {
			this.setNoon();
		}
	}
	
	private void setDark(){
		this.gl10.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		RenderUtil.enableDarkMaterial(this.gl10);
	}
	
	private void setNoon(){
		this.gl10.glClearColor(0.525f, 0.7f, 0.9f, 1.0f);
		RenderUtil.enableNoonMaterial(this.gl10);
	}
	
	public DynamicsWorld getDynamicsWorld(){
		return dynamicsWorld;
	}
	
	public void initDominoPosition(){
		Vector3f tmpVec = Grobal.tmpVec;
		tmpVec.set(0, 0, 0);
		int dLen = dominoPlates.length;
		for(int i = 0; i < dLen; i++){
			if(this.dominoPlates[i] == null) break;
			this.dominoPlates[i].initPosition(tmpVec, tmpVec, -5, i * -4.25f, 4);
		}
	}
	
	public AbstractDynamicObject[] getAllObjects(){
		AbstractDynamicObject[] allObjs = new AbstractDynamicObject[getAllObjSize()];
		
		int cnt = 0;
		for(int i = 0; i < CUBE_LENGTH; i++) allObjs[cnt++] = (AbstractDynamicObject)this.cubes[i];
		for(int i = 0; i < MAX_CUBE_BULLET; i++) allObjs[cnt++] = (AbstractDynamicObject)this.cubeBullets[i];
		for(int i = 0; i < DOMINO_PLATE_LENGTH; i++) allObjs[cnt++] = (AbstractDynamicObject)this.dominoPlates[i];
		
		return allObjs;		
	}
	
	public Cube[] getDefCubes(){
		return this.cubes;
	}
	
	public Cube[] getCubeBullets(){
		return this.cubeBullets;
	}
	
	public DominoPlate[] getDomino(){
		return this.dominoPlates;
	}
	
	public void startDomino(){
		final Vector3f linVel = Grobal.tmpVec2;
		linVel.set(0, -1, 1);
		
		final Vector3f genPos = Grobal.tmpVec;
		genPos.set(-5, 2, 4);
		
		this.shootCube(linVel, genPos);
	}
	
	public void initCubePosition(){		
		Vector3f tmpVec = Grobal.tmpVec;
		tmpVec.set(0f, 0f, 0f);
		
		int cLen = cubes.length;
		for(int i = 0; i < cLen; i++){
			if(cubes[i] == null) break;
			cubes[i].initPosition(tmpVec, tmpVec, 0, 0, i+i+3);
		}
	}
	
	public void worldInit(GL10 gl, Context context) {
		this.gl10 = gl;
		
		if(this.isDark){
			this.setDark();
		}else {
			this.setNoon();
		}
		Cube.init(gl, context);
		
		DominoPlate.init(gl, context);
		
		this.ground.init(gl, context);
		this.sky.init(gl, context);
		
		RenderUtil.enableLight(gl);
	}
	
	public static int getAllObjSize(){
		return CUBE_LENGTH + MAX_CUBE_BULLET + DOMINO_PLATE_LENGTH;
	}
	
	private class GenObjectHandler extends Handler implements Runnable{
		private long period;
		
		private void schedule(long delay, long period){
			this.period = period;
			this.postDelayed(this, delay);
		}
		
		@Override
		public void run() {
			boolean isDefObjNull = generateDefaultObjects();
			if(isDefObjNull) this.postDelayed(this, this.period);
		}
	}

}
