package com.webprog.tool;


public class CustomMath {	
	private static final double ONE_SIXTH = 1.0 / 6.0;
	private static final int FRAC_EXP = 8;
	private static final int LUT_SIZE = (1 << FRAC_EXP) + 1;
	private static final double FRAC_BIAS = Double.longBitsToDouble((0x433L - FRAC_EXP) << 52);
	private static final double[] ASIN_TAB = new double[LUT_SIZE];
	private static final double[] COS_TAB = new double[LUT_SIZE];

	private static final double[] SIN_CACHE = new double[361]; 
	private static final double[] COS_CACHE = new double[361];
	
	static {
		setSinCosCache();
		for (int ind = 0; ind < LUT_SIZE; ++ind) {
			double v = ind / (double) (1 << FRAC_EXP);
			double asinv = Math.asin(v);
			COS_TAB[ind] = COS_CACHE[(int)asinv];
			ASIN_TAB[ind] = asinv;
		}
	}

	enum Sign{
		PLUS_SIGN, MINUS_SIGN, ZERO
	}
	
	private CustomMath() {
	}

	public static double fastAtan2(double y, double x) {
		double d2 = x * x + y * y;

		if (Double.isNaN(d2) || (Double.doubleToRawLongBits(d2) < 0x10000000000000L)) {
			return Double.NaN;
		}

		boolean negY = y < 0.0;
		if (negY) {
			y = -y;
		}
		boolean negX = x < 0.0;
		if (negX) {
			x = -x;
		}
		boolean steep = y > x;
		if (steep) {
			double t = x;
			x = y;
			y = t;
		}

		double rinv = invSqrt(d2);
		x *= rinv;
		y *= rinv;

		double yp = FRAC_BIAS + y;
		int ind = (int) Double.doubleToRawLongBits(yp);

		double φ = ASIN_TAB[ind];
		double cφ = COS_TAB[ind];

		double sφ = yp - FRAC_BIAS;
		double sd = y * cφ - x * sφ;

		double d = (6.0 + sd * sd) * sd * ONE_SIXTH;
		double θ = φ + d;

		if (steep) {
			θ = Math.PI * 0.5 - θ;
		}
		if (negX) {
			θ = Math.PI - θ;
		}
		if (negY) {
			θ = -θ;
		}

		return θ;
	}

	private static double invSqrt(double x) {
		double xhalf = 0.5 * x;
		long i = Double.doubleToRawLongBits(x);
		i = 0x5FE6EB50C7B537AAL - (i >> 1);
		x = Double.longBitsToDouble(i);
		x = x * (1.5 - xhalf * x * x);
		return x;
	}
	
	// 20以上の数値に対してMath.sqrt()より高速
	public static double fastSqrt(final double a) {
        final long x = Double.doubleToLongBits(a) >> 32;
        double y = Double.longBitsToDouble((x + 1072632448) << 31);

        return y;
    }
	
	public static double fastSin(int idx){
		if(idx > 360) throw new IllegalArgumentException("Must be 0 ~ 360");
		return SIN_CACHE[idx];
	}
	
	public static double fastCos(int idx){
		if(idx > 360) throw new IllegalArgumentException("Must be 0 ~ 360");
		return COS_CACHE[idx];
	}
	
	private static void setSinCosCache(){
		if(SIN_CACHE[45] != 0 && COS_CACHE[45] != 0) return;
		for(int i = 0; i < 361; i++){
			// Math.toRadians()よりも手動の方が速い
			double angRad = Math.PI / 180 * i;
			SIN_CACHE[i] = Math.sin(angRad);
			COS_CACHE[i] = Math.cos(angRad);
		}
	}
	
	/**
	 * 極座標θをY軸の正を0度とする反時計回りの角度θにして返す
	 * 
	 * @param polarRad 極座標θのラジアン値
	 * @return ラジアン値
	 */
	public static double convertAnalogRad(double polarRad){
		switch (getSign(polarRad)) {
		case PLUS_SIGN: return (Math.PI / 180 * 360) - polarRad;
		case MINUS_SIGN: return Math.abs(polarRad);
		default: return 0;
		}
	}
	/**
	 * 極座標θをsin cos用のX軸の正を0度する反時計回りの角度θにして返す
	 * 
	 * @param polarRad 極座標θのラジアン値
	 * @return ラジアン値
	 */
	public static double convertSinCosRad(double polarRad){
		switch (getSign(polarRad)) {
		case PLUS_SIGN: return (Math.PI / 180 * 450) - polarRad;
		case MINUS_SIGN: return (Math.PI / 180 * 90) + Math.abs(polarRad);
		default: return 0;
		}
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
		case MINUS_SIGN: return (Math.PI / 180 * 360) + atan2Rad;
		default: return 0;
		}
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
