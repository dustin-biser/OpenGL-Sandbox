package utilities;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;

public class GLUtils {
	public static void exitOnGLError(String errorMessage) {
		int errorValue = GL11.glGetError();
		
		if (errorValue != GL11.GL_NO_ERROR) {
			String errorString = GLU.gluErrorString(errorValue);
			System.err.println("Error in " + errorMessage + ": " + errorString);
			
			if (Display.isCreated()) Display.destroy();
			System.exit(-1);
		}
	}
	
	/**
	 * Creates a perspective projection matrix representing a pin hole camera
	 * model based frustum, with camera at the origin (0,0,0), facing down the
	 * -z direction. <br>
	 * <p>
	 * Restrictions on zNear and zFar are as follows:
	 * <li>  <code>zNear</code> and <code>zFar</code> must be <code>>= 0</code>.
	 * <li> <code>zFar</code> must be greater than <code>zNear</code>.
	 * 
	 * @param aspectRatio
	 *            - for both the near and far faces of the frustum.
	 * @param fieldOfView
	 *            - angle in degrees representing the angular spread of the
	 *            frustum as measured from the y-z plane.
	 * @param zNear
	 *            - distance from camera to the near z plane.
	 * @param zFar
	 *            - distance from camera to the far z plane.
	 * @return A Matrix4f representing the perspective projection matrix.
	 * 
	 */
	public static Matrix4f createProjectionMatrixFov(float fieldOfView, float aspectRatio,
			float zNear, float zFar){
		validateNearzFars(zNear, zFar);
		
		Matrix4f projectionMatrix = new Matrix4f();
		
		float y_scale = coTangent(degreesToRadians(fieldOfView / 2f));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = zFar - zNear;
		
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((zFar + zNear) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * zNear * zFar) / frustum_length);
		
		return projectionMatrix;
	}
	
	/**
	 * Creates a perspective projection matrix representing a pin hole camera
	 * model based frustum, with camera at the origin (0,0,0), facing down the
	 * -z direction. <br>
	 * <p>
	 * Restrictions on zNear and zFar are as follows:
	 * <li>  <code>zNear</code> and <code>zFar</code> must be <code>>= 0</code>.
	 * <li> <code>zFar</code> must be greater than <code>zNear</code>.
	 * 
	 * @param left - minimum x coordinate of the near frustum face.
	 * @param right - maximum x coordinate of the near frustum face.
	 * @param bottom - minimum y coordinate of the near frustum face.
	 * @param top - maximum y coordinate of the near frustum face.
	 * @param zNear - z distance to the near frustum face.
	 * @param zFar - z distance to the far frustum face.
	 * 
	 * @return A Matrix4f representing the perspective projection matrix.
	 */
	public static Matrix4f createProjectionMatrix(float left, float right, float bottom,
			float top, float zNear, float zFar) {
		validateNearzFars(zNear, zFar);
		
		Matrix4f projectionMatrix = new Matrix4f();
		
		float width = right - left;  						 // x range.
		float height = top - bottom; 			   			 // y range.
		float frustum_length = zFar - zNear;   // z range.
		
		projectionMatrix.m00 = 2 * zNear / width;
		projectionMatrix.m02 = (right + left) / width;
		projectionMatrix.m11 = 2 * zNear / height;
		projectionMatrix.m12 = (top + bottom) / height;
		projectionMatrix.m22 = -1 * (zFar + zNear) / frustum_length;
		projectionMatrix.m23 = -2 * zFar * zNear / frustum_length;
		projectionMatrix.m32 = -1;
		projectionMatrix.m33 = 0;
		
		return projectionMatrix;
	}
	
	/**
	 * Creates a perspective projection matrix representing a pin hole camera
	 * model based frustum, with camera at the origin (0,0,0), facing down the
	 * -z direction. <br>
	 * <p>
	 * Restrictions on zNear and zFar are as follows:
	 * <li>  <code>zNear</code> and <code>zFar</code> must be <code>>= 0</code>.
	 * <li> <code>zFar</code> must be greater than <code>zNear</code>.
	 * 
	 * @param width - near face frustum width.
	 * @param height - near face frustum height.
	 * @param zNear - z distance to the near frustum face.
	 * @param zFar - z distance to the far frustum face.
	 * 
	 * @return A Matrix4f representing the perspective projection matrix.
	 */
	public static Matrix4f createProjectionMatrix(float width, float height,
			float zNear, float zFar){
		validateNearzFars(zNear, zFar);
		
		float right = width / 2;
		float left = -1 * right;
		float top = height / 2;
		float bottom = -1 * top;
		
		return createProjectionMatrix(left, right, bottom, top, zNear,
				zFar);
	}
	
	/**
	 * Creates and returns a Matrix4f representing a orthographic projection matrix.
	 * 
	 * Restrictions on zNear and zFar are as follows:
	 * <li>  <code>zNear</code> and <code>zFar</code> must be <code>>= 0</code>.
	 * <li> <code>zFar</code> must be greater than <code>zNear</code>.
	 * 
	 * @param left - minimum x direction for the x bounding region. 
	 * @param right - maximum x direction for the x bounding region.
	 * @param bottom - minimum y direction for the y bounding region.
	 * @param top - maximum y direction for the y bounding region.
	 * @param zNear - z distance to the near face.
	 * @param zFar - z distance to the far face.
	 * 
	 * @return Matrix4f representing the orthographic projection matrix.
	 */
	public static Matrix4f createOrthoProjectionMatrix(float left, float right,
			float bottom, float top, float zNear, float zFar) {
		validateNearzFars(zNear, zFar);
		
		// Initialized to indenity.
		Matrix4f projectionMatrix = new Matrix4f();
		
		// Set scaling components for x, y, and z directions.
		projectionMatrix.m00 = 2 / (right - left);
		projectionMatrix.m11 = 2 / (top - bottom);
		projectionMatrix.m22 = -2 / (zFar - zNear);
		
		// Set translation components.
		projectionMatrix.m03 = -1 * (right + left) / (right - left);
		projectionMatrix.m13 = -1 * (top + bottom) / (top - bottom);
		projectionMatrix.m23 = -1 * (zFar + zNear) / (zFar - zNear);
		
		return projectionMatrix;
	}
	
	public static float coTangent(float radians){
		return (float)(1 / Math.tan(radians));
	}
	
	public static float degreesToRadians(float degrees){
		return degrees * (float)(Math.PI / 180f);
	}
	
	private static void validateNearzFars(float zNear, float zFar){
		if (zNear < 0){
			throw new IllegalArgumentException("Error: zNear cannot be negative");
		}
		if (zFar < 0){
			throw new IllegalArgumentException("Error: zFar cannot be negative");
		}
		if (zNear == zFar){
			throw new IllegalArgumentException("Error: zNear cannot equal zFar!");
		}
		if (zNear > zFar){
			throw new IllegalArgumentException("Error: zNear cannot be greater than zFar!");
		}
	}
	
}
