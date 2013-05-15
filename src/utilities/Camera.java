package utilities;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	private Vector3f upDirection = new Vector3f(0f, 1f, 0f);
	private Vector3f forwardDirection = new Vector3f(0f, 0f, -1f);
	private Vector3f centerPosition = new Vector3f(0f, 0f, -1f);
	private Vector3f eyePosition = new Vector3f(0f, 0f, 0f);
	
	
	// Temp variables.  Held in memory to lessen GC impact.
	private Vector3f f = new Vector3f();
	private Vector3f u = new Vector3f();
	private Vector3f s = new Vector3f();
	private Matrix4f viewMatrix;
	
	
	public void setPosition(float x, float y, float z) {
		validateArgs(x, y, z);
		
		eyePosition.x = x;
		eyePosition.y = y;
		eyePosition.z = z;
	}
	
	public void setPosition(Vector3f position) {
		setPosition(position.x, position.y, position.z);
	}
	
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
		validateArgs(eyeX, eyeY, eyeZ);
		validateArgs(centerX, centerY, centerZ);
		validateArgs(upX, upY, upZ);
		
		centerPosition.x = centerX;
		centerPosition.y = centerY;
		centerPosition.z = centerZ;
		
		eyePosition.x = eyeX;
		eyePosition.y = eyeY;
		eyePosition.z = eyeZ;
		
		// f = center - eye
		Vector3f.sub(centerPosition, eyePosition, f);
		f.normalise();
		
		u.x = upX;
		u.y = upY;
		u.z = upZ;
		u.normalise();
		
		forwardDirection = f;
		upDirection = u;
		
		// TODO - Handle the case when center ~= eye.
		
		// TODO - Handle the case when forwardDirection || upDirection
	}
//		validateArgs(x, y, z);
//		
//		// Handle the case when targetPoint ~= current position.
//		if ( ( Math.abs(x - position.x) < 5 * Math.ulp(x) ) &&
//			 ( Math.abs(y - position.y) < 5 * Math.ulp(y) ) && 
//		     ( Math.abs(z - position.z) < 5 * Math.ulp(z) ) ) {
//			
//			// Keep forwardDirection and position the same, but move targetPoint to
//			// the point one unit from current position in the forwardDirection.
//			targetPoint = Vector3f.add(position, forwardDirection, null);
//			
//			return;
//		}
//		
//		// Set the new targetPoint.
//		targetPoint.x = x;
//		targetPoint.y = y;
//		targetPoint.z = z;
//		
//		// Proposed forward direction.
//		f = Vector3f.sub(targetPoint, position, null);
//		f.normalise();
//		
//		// Handle the case when proposed forward direction is parallel
//		// to current up direction.
//		upDirection.normalise();
//		forwardDirection.normalise();
//		x = Vector3f.dot(f, upDirection);
//		if ( ( Math.abs(x - 1) < 5 * Math.ulp(x) ) ||
//		     ( Math.abs(x + 1) < 5 * Math.ulp(x) ) ) {
//			s = Vector3f.cross(forwardDirection, upDirection, null);
//			upDirection = Vector3f.cross(s, f, null);
//		}
//		
//		forwardDirection = f;
	
	// TODO - Implement.
	public void rotate(Vector3f axis, float angle) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}
	
	public Matrix4f getViewMatrix(){
		f = forwardDirection;
		u = upDirection;
		
		// s = f x u
		Vector3f.cross(f, u, s);
		
		if (viewMatrix == null){
			viewMatrix = new Matrix4f();
		}
		
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
		
		// Eye Position.
		s.x = (-1) * eyePosition.x;
		s.y = (-1) * eyePosition.y;
		s.z = (-1) * eyePosition.z;
		
		viewMatrix.translate(s);
		
		// Reset last component in case it was modified.
		viewMatrix.m33 = 1f;
		
		return viewMatrix;
	}
	
	private void validateArgs(float f1, float f2, float f3){
		assert(!Float.isNaN(f1));
		assert(!Float.isNaN(f2));
		assert(!Float.isNaN(f3));
		
		assert(!Float.isInfinite(f1));
		assert(!Float.isInfinite(f2));
		assert(!Float.isInfinite(f3));
	}
}