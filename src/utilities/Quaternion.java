package utilities;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import static utilities.RuntimeDefines.DEBUG_MODE;
import static utilities.MathUtils.*;

/**
 * Representation of a Quaternion of the form q = xi + yj + zk + w, with
 * imaginary parts x, y, z and scalar part w.
 * 
 * @author Dustin Biser
 * 
 */
public class Quaternion {
	// Imaginary parts.
	public float x = 0f;
	public float y = 0f;
	public float z = 0f;
	
	// Real part.
	public float w = 0f;
	
	//--------------------------------------------------------------------------
	/**
	 * Default constructor.
	 */
	public Quaternion() {
		
	}
	
	//--------------------------------------------------------------------------
	public Quaternion(float x, float y, float z, float w) {
		init(x, y, z, w);
	}
	
	//--------------------------------------------------------------------------
	public Quaternion(Vector4f vec) {
		init(vec.x, vec.y, vec.z, vec.w);
	}
	
	//--------------------------------------------------------------------------
	/**
	 * Constructs a Quaternion representing a ccw rotation by <code>angle</code>
	 * radians about the <code>axis</code> given.
	 * 
	 * @param axis - axis of rotation.
	 * @param angle - angle of rotation in radians.
	 */
	public Quaternion(Vector3f axis, float angle) {
		fromAxisAngle(axis, angle);
	}
	
	//--------------------------------------------------------------------------
	private void init(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	//--------------------------------------------------------------------------
	/**
	 * Sets this <code>Quaternion</code> so that it represents a ccw rotation by
	 * <code>angle</code> radians about the <code>axis</code> given.
	 * 
	 * @param axis - axis of rotation.
	 * @param angle - angle of rotation in radians.
	 */
	public void fromAxisAngle(Vector3f axis, float angle) {
		if (DEBUG_MODE) { validateVectorIsNonZero(axis); }
		
		// q = (sin(angle/2)u, cos(angle/2))  with u, a unit vector.
		
		w = (float) (1f / Math.sqrt((axis.x*axis.x + axis.y*axis.y + axis.z*axis.z)));
		
		x = axis.x * w;
		y = axis.y * w;
		z = axis.z * w;
		
		w =  (float) Math.sin(0.5f * angle);
		x *= w;
		y *= w;
		z *= w;
		
		w = (float) Math.cos(0.5f * angle);
	}
	
	//--------------------------------------------------------------------------
	@Override 
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		else if (!(other instanceof Quaternion)) {
			return false;
		}
		else if (this == other) {
			return true;
		}
	
		Quaternion o = (Quaternion) other;
		
		return MathUtils.floatEquals(x, o.x) &&
			   MathUtils.floatEquals(y, o.y) &&
			   MathUtils.floatEquals(z, o.z) &&
			   MathUtils.floatEquals(w, o.w);
	}
	
	//--------------------------------------------------------------------------
	@Override 
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Quaternion(");
		sb.append(x);
		sb.append(", ");
		sb.append(y);
		sb.append(", ");
		sb.append(z);
		sb.append(", ");
		sb.append(w);
		sb.append(")");
		
		return sb.toString();
	}
	
	//--------------------------------------------------------------------------
	public static void add(Quaternion lhs, Quaternion rhs, Quaternion dest) {
		dest.x = lhs.x + rhs.x;
		dest.y = lhs.y + rhs.y;
		dest.z = lhs.z + rhs.z;
		dest.w = lhs.w + rhs.w;
	}
	
	//--------------------------------------------------------------------------
	/**
	 * Add two Quaternions and return the result.
	 * 
	 * @param lhs
	 * @param rhs
	 * @return a new Quaternion which is the result of lhs added to rhs.
	 */
	public static Quaternion add(Quaternion lhs, Quaternion rhs) {
		Quaternion result = new Quaternion();
		add(lhs, rhs, result);
		
		return result;
	}
	
	//--------------------------------------------------------------------------
	public static void subtract(Quaternion lhs, Quaternion rhs, Quaternion dest) {
		dest.x = lhs.x - rhs.x;
		dest.y = lhs.y - rhs.y;
		dest.z = lhs.z - rhs.z;
		dest.w = lhs.w - rhs.w;
	}
	
	//--------------------------------------------------------------------------
	/**
	 * Subtract two Quaternions and return the result.
	 * 
	 * @param lhs
	 * @param rhs
	 * @return a new Quaternion which is the result of lhs subracted by rhs.
	 */
	public static Quaternion subtract(Quaternion lhs, Quaternion rhs) {
		Quaternion result = new Quaternion();
		subtract(lhs, rhs, result);
		
		return result;
	}
	
	//--------------------------------------------------------------------------
	/**
	 * Multiply two Quaternions and store the result in <code>dest</code>.
	 * 
	 * @param lhs
	 * @param rhs
	 * @param dest
	 */
	public static void mult(Quaternion lhs, Quaternion rhs, Quaternion dest) {
		dest.x = (lhs.y * rhs.z) - (lhs.z * rhs.y) + (rhs.w * lhs.x) + (lhs.w * rhs.x);
		dest.y = (lhs.z * rhs.x) - (lhs.x * rhs.z) + (rhs.w * lhs.y) + (lhs.w * rhs.y);
		dest.z = (lhs.x * rhs.y) - (lhs.y * rhs.x) + (rhs.w * lhs.z) + (lhs.w * rhs.z);
		dest.w = (lhs.w * rhs.w) - (lhs.x * rhs.x) - (lhs.y * rhs.y) - (lhs.z * rhs.z);
	}
	
	//--------------------------------------------------------------------------
	/**
	 * Multiply two Quaternions and return a new Quaternion representing the
	 * result.
	 * 
	 * @param lhs
	 * @param rhs
	 * @return a new <code>Quanternion</code> representing <code>lhs</code>
	 *         multiplied by <code>rhs</code>.
	 */
	public static Quaternion mult(Quaternion lhs, Quaternion rhs) {
		Quaternion result = new Quaternion();
		mult(lhs, rhs, result);
		
		return result;
	}
	
	//--------------------------------------------------------------------------
	public static void copy(Quaternion src, Quaternion dest) {
		dest.x = src.x;
		dest.y = src.y;
		dest.z = src.z;
		dest.w = src.w;
	}
	
	//--------------------------------------------------------------------------
	/**
	 * Conjugates the Quaternion.
	 * <br>
	 * Thus, given a Quaternion q = (v, w) with
	 * imaginary 3-component vector v, and scalar part w, q.conjugate() == (-v, w).
	 */
	public void conjugate() {
		x *= -1;
		y *= -1;
		z *= -1;
	}
	
	//--------------------------------------------------------------------------
	/**
	 * Computes the conjugate of <code>src</code> and stores it in
	 * <code>dest</code>.
	 * 
	 * @param src
	 * @param dest
	 */
	public static void conjugate(Quaternion src, Quaternion dest) {
		copy(src, dest);
		dest.conjugate();
	}
	
	//--------------------------------------------------------------------------
	public static Quaternion conjugate(Quaternion q) {
		Quaternion result = new Quaternion();
		conjugate(q, result);
		
		return result;
	}
	
	//--------------------------------------------------------------------------
	/**
	 * @return the Euclidean norm (i.e. 2-norm) squared for this Quaternion.
	 */
	public float normSquared( ) {
		return x*x + y*y + z*z + w*w;
	}
	
	//--------------------------------------------------------------------------
	/**
	 * @return the Euclidean norm (i.e. 2-norm) for this Quaternion.
	 */
	public float norm() {
		return (float)Math.sqrt(normSquared());
	}
	
	//--------------------------------------------------------------------------
	/**
	 * Multiply each component of the Quaternion by s.
	 * @param s - scale factor.
	 */
	public void scale(float s) {
		x *= s;
		y *= s;
		z *= s;
		w *= s;
	}
	
	//--------------------------------------------------------------------------
	/**
	 * Multiply each component of <code>src</code> by <code>s</code> and store
	 * in <code>dest</code>.
	 * 
	 * @param src - source Quaternion
	 * @param s	- scaling factor
	 * @param dest - destination Quaternion
	 */
	public static void scale(Quaternion src, float s, Quaternion dest) {
		Quaternion.copy(src, dest);
		dest.scale(s);
	}
	
	//--------------------------------------------------------------------------
	public static Quaternion scale(Quaternion src, float s) {
		Quaternion result = new Quaternion();
		scale(src, s, result);
		
		return result;
	}
	
	//--------------------------------------------------------------------------
	public void inverse() {
		conjugate();
		scale(1f / normSquared());
	}
	
	
	//--------------------------------------------------------------------------
	public static void inverse(Quaternion src, Quaternion dest) {
		conjugate(src, dest);
		dest.scale(1 / dest.normSquared());
	}
	
	//--------------------------------------------------------------------------
	public static Quaternion inverse(Quaternion src) {
		Quaternion result = new Quaternion();
		inverse(src, result);
		
		return result;
	}
	
	//--------------------------------------------------------------------------
	/**
	 * 
	 * @return a rotation matrix representation of this <code>Quaternion</code>.
	 */
	public Matrix4f toRotationMatrix() {
		float s = 2f / norm();
		
		Matrix4f result = new Matrix4f();
	
		result.m00 = 1 - s*(y*y + z*z);
		result.m01 = s*(x*y + w*z);
		result.m02 = s*(x*z - w*y);
		
		result.m10 = s*(x*y - w*z);
		result.m11 = 1 - s*(x*x + z*z);
		result.m12 = s*(y*z + w*x);
		
		result.m20 = s*(x*z + w*y);
		result.m21 = s*(y*z - w*x);
		result.m22 = 1 - s*(x*x + y*y);
		
		return result;
	}
	
	
	private void validateVectorIsNonZero(Vector3f vec) {
		if (vec.lengthSquared() < EPSILON) {
			throw new IllegalArgumentException(
					"Vector3f parameter cannot have zero length.");
		}
	}
}
