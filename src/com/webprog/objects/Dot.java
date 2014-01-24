package com.webprog.objects;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.webprog.tool.RenderUtil;

// 点の描画クラス
public class Dot {
	private float x, y, z;
	
	private int verticesCount;
	
	private FloatBuffer vertexBuffer;
	
	public Dot(float[] vertices) {
		this.verticesCount = vertices.length / 3;
		this.vertexBuffer = RenderUtil.allocateFloatBuffer(vertices);
	}

	public void draw(GL10 gl) {		
		gl.glPushMatrix();

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		
		gl.glTranslatef(x, y, z);
		
		gl.glPointSize(2f);
		
		gl.glDrawArrays(GL10.GL_POINTS, 0,  verticesCount);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		
		gl.glPopMatrix();
	}

	public void setPosition(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}	
}
