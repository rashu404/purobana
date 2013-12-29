package com.webprog.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;

// objファイルを解析して描画用の配列を返すクラス
public class ObjLoader{
	
	enum LineKey{
		VERTICES, INDICES, UVS, NORMALS, UNKNOWN
	}
	
	private float[] vertices, uvs, normals;
	private short[] indices;
	
	public ObjLoader(Context context, String fileName) {
		
		try {
			loadObj(context.getAssets(), fileName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public float[] getVertices(){
		return this.vertices;
	}
	
	public short[] getIndices(){
		return this.indices;
	}
	
	public float[] getUVs(){
		return this.uvs;
	}
	
	public float[] getNormals(){
		return this.normals;
	}
	
	// objファイルをロードする
	private void loadObj(AssetManager assetManager, String fileName) throws IOException{
		InputStream in = null;
		in = assetManager.open(fileName);
			
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		List<String> vertexList = new ArrayList<String>();
		List<String> indexList = new ArrayList<String>();
		List<String> uvList = new ArrayList<String>();
		List<String> normalList = new ArrayList<String>();
		List<String> uvIndexList = new ArrayList<String>();
		
		String line = null;
		
		while((line = reader.readLine()) != null){
			addLineElement(line, vertexList, indexList, uvList, normalList, uvIndexList);
		}
		
		this.vertices = listToFloatArray(vertexList);
		this.indices = listToShortArray(indexList);
		this.uvs = listToFloatArray(uvList);
		this.normals = listToFloatArray(normalList);
		
	}
	
	// 行から読み込んだ該当する要素をListに追加
	private void addLineElement(String line, List<String> vertexList, List<String> indexList, List<String> uvList, List<String> normalList, List<String> uvIndexList){
		switch (getLineKey(line)) {
		case VERTICES:
			
			this.splitVertexLine(vertexList, line);
			
			break;
		
		case INDICES:
			
			this.splitIndexLine(indexList, uvIndexList, line);
			
			break;
			
		case UVS:
			
			this.splitUVLine(uvList, line);
				
			break;
			
		case NORMALS:
			
			this.splitNormalLine(normalList, line);
			
			break;
			
		default:
			break;
		}
		
	}
	
	// 頂点情報の行を分割してListにaddする
	private void splitVertexLine(List<String> vertexList, String line) {
		String[] tokens = line.split(" ");
		
		vertexList.add(tokens[1]);
		vertexList.add(tokens[2]);
		vertexList.add(tokens[3]);
	}
	
	// インデックス情報の行を分割してListにaddする
	private void splitIndexLine(List<String> indexList, List<String> uvIndexList, String line){
		String[] tokens = line.split(" ");
		
		this.addIndex(indexList, uvIndexList, tokens[1]);
		this.addIndex(indexList, uvIndexList, tokens[2]);
		this.addIndex(indexList, uvIndexList, tokens[3]);
	}
	
	// テクスチャ配列の行を分割してListにaddする
	private void splitUVLine(List<String> uvList, String line){
		String[] tokens = line.split(" ");
		
		uvList.add(tokens[1]);
		uvList.add(tokens[2]);
	}
	
	// 法線配列の行を抽出
	private void splitNormalLine(List<String> normalList, String line){
		String[] tokens = line.split(" ");
		
		normalList.add(tokens[1]);
		normalList.add(tokens[2]);
		normalList.add(tokens[3]);
	}
	
	// １行を読み込み、一致したキーを取得
	private LineKey getLineKey(String line){
		LineKey ret = null;
		
		if(line.startsWith("v ")){
			ret = LineKey.VERTICES;
		}else if (line.startsWith("f ")) {
			ret = LineKey.INDICES;
		}else if (line.startsWith("vt ")) {
			ret = LineKey.UVS;
		}else if (line.startsWith("vn ")){
			ret = LineKey.NORMALS;
		}else {
			ret = LineKey.UNKNOWN;
		}
		
		return ret;
	}
	
	// indexLinesに/で分割したインデックスを追加する
	private void addIndex(List<String> indexLines, List<String> uvIndexList, String token){
		String[] tmp = token.split("/");
		indexLines.add(getIndex(tmp[0]));
		uvIndexList.add(getIndex(tmp[1]));
	}
	
	// objファイルの「f 」行の数値を-1したインデックスを取得
	private String getIndex(String token){
		int tmp = Integer.parseInt(token);
		
		return String.valueOf(tmp - 1);
	}
	
	// リストをfloat配列に変換
	private float[] listToFloatArray(List<String> list) {
		float[] result = new float[list.size()];

		for (int i = 0; i < list.size(); i++) {
			result[i] = Float.valueOf(list.get(i));
		}

		return result;
	}
	
	// Shortリストをshort配列に変換
	private short[] listToShortArray(List<String> list){
		short[] result = new short[list.size()];

		for (int i = 0; i < list.size(); i++) {
			result[i] = Short.valueOf(list.get(i));
		}

		return result;
	}
}
