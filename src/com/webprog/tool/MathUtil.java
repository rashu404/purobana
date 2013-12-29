package com.webprog.tool;

public class MathUtil {
	
	enum Sign{
		PLUS_SIGN, MINUS_SIGN, ZERO
	}
	
	private MathUtil() {
	}
	
	/**
	 * 極座標θをY軸の正を0度とする反時計回りの角度θにして返す
	 * 
	 * @param polarRad 極座標θのラジアン値
	 * @return ラジアン値
	 */
	public static double convertAnalogRad(double polarRad){
		switch (getSign(polarRad)) {
		case PLUS_SIGN:
			
			return Math.toRadians(360) - polarRad;
			
		case MINUS_SIGN:
			
			return Math.abs(polarRad);
			
		default:
			break;
		}
		
		return 0;
	}
	/**
	 * 極座標θをsin cos用のX軸の正を0度する反時計回りの角度θにして返す
	 * 
	 * @param polarRad 極座標θのラジアン値
	 * @return ラジアン値
	 */
	public static double convertSinCosRad(double polarRad){
		switch (getSign(polarRad)) {
		case PLUS_SIGN:
			
			return Math.toRadians(450) - polarRad;
			
		case MINUS_SIGN:
			
			return Math.toRadians(90) + Math.abs(polarRad);
			
		default:
			break;
		}
		
		return 0;
	}
	
	/**
	 * Math.atan2()で求めた極座標θを360度系に変換して返す
	 * 
	 * @param atan2Rad Math.atan2()の返り値
	 * @return ラジアン値
	 */
	public static double convertAtan2To360AngRad(double atan2Rad){
		switch (getSign(atan2Rad)) {
		case PLUS_SIGN: return atan2Rad;
		case MINUS_SIGN: return Math.toRadians(360) + atan2Rad;
		default:break;
		}
		return 0;
	}
	
	// double値の符号を取得
	private static Sign getSign(double n){
		if(n > 0){
			return Sign.PLUS_SIGN;
		}else if (n < 0) {
			return Sign.MINUS_SIGN;
		}else {
			return Sign.ZERO;
		}
	}
}
