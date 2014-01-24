package com.webprog.ui;

import javax.vecmath.Vector3f;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;

import com.webprog.objects.AbstractDynamicObject;
import com.webprog.render.Camera3D;
import com.webprog.render.World;

public class FieldMap extends View implements Runnable{
	public static final int CUBE_COLOR = Color.rgb(153, 51, 0);
	public static final int DOMINO_COLOR = Color.GRAY;
	
	private Canvas canvas;
	private Paint paint;
	private Rect rect;
	private Path vectorPath;
	
	private MapPoint[] points = new MapPoint[World.getAllObjSize()];
	
	private float arrowAngle;
	
	private class MapPoint{
		private float x, y;
		private int color;
		
		private MapPoint() {
		}
		
		private MapPoint(float x, float y, int color){
			this.x = x;
			this.y = y;
			this.color = color;
		}
	}
	
	public FieldMap(Context context) {
		super(context);
		this.paint = new Paint();
		this.rect = new Rect();
		this.vectorPath = new Path();
				
		new Handler().postDelayed(this, 500);
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		if(this.canvas == null) this.canvas = canvas;
		
		this.paint.setColor(Color.rgb(124, 173, 76));
		this.paint.setAlpha(220);
		
		final float centerX = canvas.getWidth() / 2;
		final float centerY = canvas.getHeight() / 2;
		
		final int width = canvas.getWidth();
		final int height = canvas.getHeight();
		
		// 背景を描画
		this.rect.set(0, 0, width, height);
		canvas.drawRect(this.rect, this.paint);
		
		// eyeのベクトルを描画
		this.paint.setColor(Color.BLUE);
		canvas.save();
		canvas.rotate(this.arrowAngle, centerX, centerY);
		canvas.drawPath(this.vectorPath, this.paint);
		canvas.restore();
				
		// オブジェクトの描画
		final float objLeft = canvas.getWidth() * 0.035f;
		final float objTop = canvas.getHeight() * 0.035f;
		for(int i = 0; i < this.points.length; i++){
			if(this.points[i] == null) continue;
			
			MapPoint mp = this.points[i];
			
			this.rect.left = (int) (mp.x - objLeft);
			this.rect.right = (int) (mp.x + objLeft);
			this.rect.top = (int) (mp.y - objTop);
			this.rect.bottom = (int) (mp.y + objTop);
		
			this.paint.setColor(mp.color);
			canvas.drawRect(this.rect, this.paint);
		}
	}
	
	public void syncMap(Camera3D cm, World world){		
		this.arrowAngle = -cm.getAngleDegH() - 90;
		
		Vector3f eye = cm.getEye();
		AbstractDynamicObject[] ado = world.getAllObjects();
		for(int i = 0; i < this.points.length; i++){
			if(ado[i] == null || !ado[i].alreadyAddWorld()) continue;
			
			Vector3f origin = ado[i].getTransform().origin;
			float x = origin.x;
			float y = origin.y;
			
			if(this.points[i] == null) this.points[i] = new MapPoint();
			this.convertPoint3Dto2D(this.points[i], eye, x, y, ado[i].getFieldMapColor());
		}
		
		this.invalidate();
	}
	
	private FieldMap addArrow(){
		final float center = this.canvas.getWidth() / 2;
		this.vectorPath.moveTo(center, center * 0.875f);
		this.vectorPath.lineTo(center * 1.125f, center * 1.125f);
		this.vectorPath.lineTo(center, center * 1.05f);
		this.vectorPath.lineTo(center * 0.875f, center * 1.125f);
		this.vectorPath.close();

		return this;
	}
	
	private MapPoint convertPoint3Dto2D(MapPoint mp, Vector3f eye, float convX, float convY, int color){
		final float x3D = convX;
		final float y3D = convY;
		
		final int maxScope3D = 50;
		final int maxScope2D = canvas.getWidth();
	
		final float cen3D = maxScope3D / 2;
		final float cen2D = maxScope2D / 2;
		
		mp.x = (((eye.x - x3D) * cen2D) / cen3D) + cen2D;
		mp.y = maxScope2D - ((((eye.y - y3D) * cen2D) / cen3D) +cen2D);
		mp.color = color;
		
		return mp;
	}

	@Override
	public void run() {
		this.addArrow().invalidate();
	}
}
