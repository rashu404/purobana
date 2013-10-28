package com.webprog.util;

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
