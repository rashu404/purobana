package com.webprog.objects;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;

import com.webprog.tool.CustomMath;
import com.webprog.tool.ObjLoader;
import com.webprog.tool.RenderUtil;

public class Model {
	private float x, y, z;
	private float scaleSize = 1;
	
	private static int indicesLength;

	private static FloatBuffer vertexBuffer;
	private static ShortBuffer indexBuffer;
	private static FloatBuffer textureBuffer;
	private static FloatBuffer normalBuffer;
	
	private int texResId;
	private int texId;
	
	private float[] modelMatrix = new float[16];
	
	private static final String LOAD_OBJ_FILENAME = "sphere.obj";
	
	public Model(Context context, int texResId) {
		this.texResId = texResId;
		if( isBufferNull() ) this.loadObj(context, LOAD_OBJ_FILENAME);
	}

	public void draw(GL10 gl) {
		gl.glPushMatrix();
		
		((GL11) gl).glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelMatrix, 0);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);

		gl.glTranslatef(x, y, z);
		gl.glScalef(scaleSize, scaleSize, scaleSize);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texId);
		
		gl.glDrawElements(GL10.GL_TRIANGLES, indicesLength, GL10.GL_UNSIGNED_SHORT, indexBuffer);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		
		gl.glPopMatrix();
	}

	public void setPosition(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setScaleSize(float scaleSize){
		this.scaleSize = scaleSize;
	}
	
	public void rotate(float r, int angleHDeg){
		double sin = CustomMath.fastSin(angleHDeg);
		double cos = CustomMath.fastCos(angleHDeg);
	
		this.x= (float) (r * cos);
		this.y =(float) (r * sin);
	}
	
	public void setTexId(int texId){
		this.texId = texId;
	}
	
	public float[] getModelMatrix(){
		return this.modelMatrix;
	}
	
	public void init(Context context, GL10 gl){
		this.texId = RenderUtil.loadTex(gl, context, texResId);
	}
	
	private void loadObj(Context context, String objFileName){
		ObjLoader objLoader = new ObjLoader(context, objFileName);
		
		float[] mVertices  = objLoader.getVertices();
		short[] mIndices = objLoader.getIndices();
		float[] mTextures = objLoader.getUVs();
		float[] mNormals = objLoader.getNormals();
		
		indicesLength = mIndices.length;
		
		vertexBuffer = RenderUtil.allocateFloatBuffer(mVertices);
		indexBuffer = RenderUtil.allocateShortBuffer(mIndices);
		textureBuffer = RenderUtil.allocateFloatBuffer(mTextures);
		normalBuffer = RenderUtil.allocateFloatBuffer(mNormals);
	}
	
	private boolean isBufferNull(){
		return vertexBuffer == null && indexBuffer == null
				&& textureBuffer == null && normalBuffer == null;
	}

}
