package util;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	private Vector3f centerPosition = new Vector3f(0f, 0f, -1f);
	private Vector3f eyePosition = new Vector3f(0f, 0f, 0f);
	
	// Vectors s, u, -f form a right-handed orthonormal basis for the Camera
	// coordinate system, who's components are given in terms of the world space
	// axes.
	private Vector3f s = new Vector3f(1f, 0f, 0f);	// side vector
	private Vector3f u = new Vector3f(0f, 1f, 0f);	// up vector
	private Vector3f f = new Vector3f(0f, 0f, -1f);	// forward vector
	
	
	//--------------------------------------------------------------------------
	public void setPosition(float x, float y, float z) {
		validateArgs(x, y, z);
		
		eyePosition.x = x;
		eyePosition.y = y;
		eyePosition.z = z;
	}
	
	//--------------------------------------------------------------------------
	public void setPosition(Vector3f position) {
		setPosition(position.x, position.y, position.z);
	}
	
	//--------------------------------------------------------------------------
	/**
	 * Translates the Camera using world space axes.
	 * @param x - movement magnitude along world space x-axis.
	 * @param y - movement magnitude along world space y-axis.
	 * @param z - movement magnitude along world space z-axis.
	 */
	public void translate(float x, float y, float z) {
		validateArgs(x, y , z);
		
		eyePosition.x += x;
		eyePosition.y += y;
		eyePosition.z += z;
	}
	
	/**
	 * Translates the Camera using world space axes.
	 * @param vec - Vector with movement magnitudes along world space x, y, and
	 *            z axes.
	 */
	//--------------------------------------------------------------------------
	public void translate(Vector3f vec) {
		translate(vec.x, vec.y, vec.z);
	}
	
	//--------------------------------------------------------------------------
	/**
	 * Translates the camera relative to itself.
	 * @param right - length of translation in the right-direction.
	 * @param up - length of translation in the up-direction.
	 * @param forward - length of translation in the forward-direction.
	 */
	public void translateRelative(float right, float up, float forward) {
		validateArgs(right, up, forward);
	
		// Decompose vectors s, u, f into its world space components x, y, z,
		// and translate the camera right-units in the s direction, up-units in
		// the u direction, and forward-units in the f direction.
		eyePosition.x += (right * s.x) + (up * u.x) + (forward * f.x);
		eyePosition.y += (right * s.y) + (up * u.y) + (forward * f.y);
		eyePosition.z += (right * s.z) + (up * u.z) + (forward * f.z);
	}
	
	//--------------------------------------------------------------------------
	public void translateRelative(Vector3f vec) {
		translateRelative(vec.x, vec.y, vec.z);
	}
	
	//--------------------------------------------------------------------------
	/**
	 * @return a new {@link Vector3f} representing the current eye position of
	 *         the Camera.
	 */
	public Vector3f getPosition() {
		return new Vector3f(eyePosition.x, eyePosition.y, eyePosition.z);
	}
	
	//--------------------------------------------------------------------------
	/**
	 * Position the camera at (eyeX, eyeY, eyeZ) in world space, so that it is
	 * facing with the point (centerX, centerY, centerZ) in the center of its
	 * view, and with up vector given by (upX, upY, upZ).
	 * 
	 * @param eyeX
	 * @param eyeY
	 * @param eyeZ
	 * @param centerX
	 * @param centerY
	 * @param centerZ
	 * @param upX
	 * @param upY
	 * @param upZ
	 */
	public void lookAt(float eyeX, float eyeY, float eyeZ,
					   float centerX, float centerY, float centerZ,
					   float upX, float upY, float upZ) {
		validateArgs(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
		
		centerPosition.x = centerX;
		centerPosition.y = centerY;
		centerPosition.z = centerZ;
		
		eyePosition.x = eyeX;
		eyePosition.y = eyeY;
		eyePosition.z = eyeZ;
		
		// f = centerPosition - eyePosition.
		Vector3f.sub(centerPosition, eyePosition, f);
		f.normalise();
		
		u.x = upX;
		u.y = upY;
		u.z = upZ;
		
		// s = f x u
		Vector3f.cross(f, u, s);
		s.normalise();
		
		// u = s x f
		Vector3f.cross(s, f, u);
		
		
		// TODO - Handle the case when center ~= eye.
		
		// TODO - Handle the case when forwardDirection || upDirection
	}
	
	//--------------------------------------------------------------------------
	/**
	 * Position the Camera at <code>eyePos</code> in world space, so that it is
	 * facing with the point <code>centerPos</code> in the center of its
	 * view, and with up direction given by <code>upDir</code>.
	 * 
	 * @param eyePos - world space position of Camera.
	 * @param centerPos - world space target the Camera is pointed at.
	 * @param upDir - world space up direction for Camera.
	 */
	public void lookAt(Vector3f eyePos, Vector3f centerPos, Vector3f upDir) {
		lookAt(eyePos.x, eyePos.y, eyePos.z, centerPos.x, centerPos.y,
				centerPos.z, upDir.x, upDir.y, upDir.z);
	}
	
	// Units of Least Precision of 1.0f * 5.
	private float epsilon = 5 * 1.1920929E-7f;
	
	//--------------------------------------------------------------------------
	public void lookAt(float centerX, float centerY, float centerZ) {
		validateArgs(centerX,  centerY,  centerZ);
		
		centerPosition.x = centerX;
		centerPosition.y = centerY;
		centerPosition.z = centerZ;
		
		// f = center - eye.
		Vector3f.sub(centerPosition, eyePosition, f);
		
		// If center ~= eyePosition, do nothing.
		if ( f.lengthSquared() < epsilon){
			return;
		}
		
		f.normalise();
		
		// The following projects u onto the plane defined by the point eyePosition,
		// and the normal f.  The goal is to rotate u so that it is orthogonal to f,
		// while attempting to keep u's orientation fairly close to its previous state.
		{
			// Borrow s vector for calculation, so we don't have to allocate a new vector.
			// s = eye + u
			Vector3f.add(eyePosition, u, s);
			
			// t = f dot u
			float t = Vector3f.dot(f, u);
			
			// Move point s in the normal direction, f, by t units so that it is
			// on the plane.
			if ( t < 0) {
				t *= -1;
			}	
			s.x += t*f.x;
			s.y += t*f.y;
			s.z += t*f.z;
			
			// u = s - eye.
			Vector3f.sub(s, eyePosition, u);
			u.normalise();
		}
		
		// Update s vector given new f and u vectors.
		// s = f x u
		Vector3f.cross(f, u, s);
		s.normalise();
		
		// If f and u are no longer orthogonal, make them so.
		if ( Vector3f.dot(f, u) > epsilon ) {
			// u = f x s
			Vector3f.cross(s, f, u);
			u.normalise();
		}
	}
	
	//--------------------------------------------------------------------------
	public void lookAt(Vector3f centerPos){
		lookAt(centerPos.x, centerPos.y, centerPos.z);
	}
	
	//--------------------------------------------------------------------------
	// TODO - Implement.
	public void rotate(Vector3f axis, float angle) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}
	
	
	//--------------------------------------------------------------------------
	/**
	 * Gets a new {@link Matrix4f} view matrix representation for this Camera.
	 * In OpenGL parlance the view matrix transforms points from World Space to
	 * Camera Space.
	 * 
	 * @return a new view matrix
	 */
	public Matrix4f getViewMatrix(){
		Matrix4f viewMatrix = new Matrix4f();
		
		// Transformation of 1st basis vector.
		viewMatrix.m00 = s.x;
		viewMatrix.m01 = u.x;
		viewMatrix.m02 = -1 * f.x;
		viewMatrix.m03 = 0f;
		
		// Transformation of 2nd basis vector.
		viewMatrix.m10 = s.y;
		viewMatrix.m11 = u.y;
		viewMatrix.m12 = -1 * f.y;
		viewMatrix.m13 = 0f;
		
		// Transformation of 3rd basis vector.
		viewMatrix.m20 = s.z;
		viewMatrix.m21 = u.z;
		viewMatrix.m22 = -1 * f.z;
		viewMatrix.m23 = 0f;
		
		// Inverse translation of Eye Position.  Borrowing vector s for this calculation
		// so a new Vector does not have to be allocated.
		s.x = (-1) * eyePosition.x;
		s.y = (-1) * eyePosition.y;
		s.z = (-1) * eyePosition.z;
		viewMatrix.translate(s);
		
		// Setting s back to its previous value.
		// s = f x u
		Vector3f.cross(f, u, s);
		s.normalise();
		
		// Reset last component in case it was modified.
		viewMatrix.m33 = 1f;
		
		return viewMatrix;
	}
	
	//--------------------------------------------------------------------------
	private void validateArgs(float... floatArgs) {
		for(float f : floatArgs) {
			assert(!Float.isNaN(f));
			assert(!Float.isInfinite(f));
		}
	}
}