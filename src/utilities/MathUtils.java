package utilities;

public class MathUtils {
	
	/**
	 * Recommend for floating point comparisons between values not much larger
	 * than one in magnitude.
	 */
	public static final float EPSILON = 5 * Math.ulp(1f);
	
	/**
	 * Checks the equality of two floats taking into consideration their relative
	 * magnitudes.
	 * 
	 * @param a - first float
	 * @param b - second float
	 * @return true if the distance between floats a and b is within 5 ULPs of
	 *         their average, false otherwise.
	 */
	public static boolean floatEquals(float a, float b) {
		return  Math.abs(a - b) < 5 * Math.ulp(0.5f * (a + b));
	}
}
