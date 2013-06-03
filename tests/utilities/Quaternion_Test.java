package utilities;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import util.math.Quaternion;
import static util.math.MathUtils.*;

public class Quaternion_Test {
	private Quaternion q;
	private Quaternion q1;
	private Quaternion q2;
	
	@Before
	public void setUp() throws Exception {
		q = null;
		q1 = null;
		q2 = null;
	}
	
	@After
	public void tearDown() throws Exception { }
	
	//--------------------------------------------------------------------------
	// Helper Method
	private boolean matrix4fEquals(Matrix4f a, Matrix4f b) {
		float aArray[] = {a.m00, a.m01, a.m02, a.m03,
						  a.m10, a.m11, a.m12, a.m13,
						  a.m20, a.m21, a.m22, a.m23,
						  a.m30, a.m31, a.m32, a.m33};
		
		float bArray[] = {b.m00, b.m01, b.m02, b.m03,
						  b.m10, b.m11, b.m12, b.m13,
						  b.m20, b.m21, b.m22, b.m23,
						  b.m30, b.m31, b.m32, b.m33};
		
		for(int i = 0; i < 16; i++) {
			if (floatEqualsUlp(aArray[i], bArray[i]) == false)
				return false;
		}
		
		return true;
	}

	//--------------------------------------------------------------------------
	@Test
	public void test_creationWithFloats() {
		q = new Quaternion(1f, 2f, 3f, 4f);
		
		assertEquals(1f, q.x, EPSILON);
		assertEquals(2f, q.y, EPSILON);
		assertEquals(3f, q.z, EPSILON);
		assertEquals(4f, q.w, EPSILON);
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_creationFromAxisAngle() {
		float angle = (float) Math.PI / 2f;
		float s = (float) Math.sin(0.5f * angle);
		float c = (float) Math.cos(0.5f * angle);
		Vector3f axis = new Vector3f(1,2,3);
		
		q = new Quaternion(axis, angle);
		
		axis.normalise();
		
		assertEquals(s * axis.x, q.x, EPSILON);
		assertEquals(s * axis.y, q.y, EPSILON);
		assertEquals(s * axis.z, q.z, EPSILON);
		assertEquals(c, q.w, EPSILON);
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_equals1() {
		q = new Quaternion(1f, 2f, 3f, 4f);
		
		assertTrue(q.equals(q));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_equals2() {
		q1 = new Quaternion(1f, 2f, 3f, 4f);
		q2 = new Quaternion(1f, 2f, 3f, 4f);
		
		assertTrue(q1.equals(q2));
		assertTrue(q2.equals(q1));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_equals3() {
		q1 = new Quaternion(1f, 2f, 3f, 4f);
		q2 = null;
		
		assertFalse(q1.equals(q2));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_equals4() {
		q1 = new Quaternion(1.0000001f, 2f, 3f, 4f);
		q2 = new Quaternion(1f, 2f, 3f, 4.0000001f);
		
		assertTrue(q1.equals(q2));
		assertTrue(q2.equals(q1));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_equals5() {
		q1 = new Quaternion(1.00001f, 2f, 3f, 4f);
		q2 = new Quaternion(1f, 2f, 3f, 4.00001f);
		
		assertFalse(q1.equals(q2));
		assertFalse(q2.equals(q1));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_add() {
		q = new Quaternion(1f, 2f, 3f, 4f);
		
		// q = q + q
		Quaternion.add(q, q, q);
		
		Quaternion expected = new Quaternion(2f, 4f, 6f, 8f);
		
		assertTrue(expected.equals(q));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_add2() {
		q1 = new Quaternion(1f, 2f, 3f, 4f);
		q2 = new Quaternion(-1f, -2f, -3f, -4f);
		q = new Quaternion();
		
		//q = q1 + q2
		Quaternion.add(q1, q2, q);
		assertTrue(q.equals(new Quaternion(0f, 0f, 0f, 0f)));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_add3() {
		q1 = new Quaternion(1f, 2f, 3f, 4f);
		q2 = new Quaternion(-1f, -2f, -3f, -4f);
		
		//q = q1 + q2
		q = Quaternion.add(q1, q2);
		assertTrue(q.equals(new Quaternion(0f, 0f, 0f, 0f)));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_subtract1() {
		q = new Quaternion(1f, 2f, 3f, 4f);
		
		// q = q - q.
		Quaternion.subtract(q, q, q);
		
		assertTrue(q.equals(new Quaternion(0f, 0f, 0f, 0f)));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_subtract2() {
		q1 = new Quaternion(1f, 2f, 3f, 4f);
		q2 = new Quaternion(0f, 0f, 0f, 0f);
		
		// q = q2 - q1
		q = Quaternion.subtract(q2, q1);
		
		assertTrue(q.equals(new Quaternion(-1f, -2f, -3f, -4f)));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_mult1() {
		q1 = new Quaternion(1f, 2f, 3f, 4f);
		q2 = new Quaternion(0f, 0f, 0f, 2f);  // Scalar in Quaternion form.
		
		Quaternion expected = new Quaternion(2f, 4f, 6f, 8f);
		
		q = Quaternion.mult(q1, q2);
		assertTrue(q.equals(expected));
		
		// Now reverse order.
		q = Quaternion.mult(q2, q1);
		assertTrue(q.equals(expected));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_mult2() {
		q1 = new Quaternion(1f, 2f, 3f, 4f);
		q2 = new Quaternion(0f, 0f, 0f, -1f);  // Scalar in Quaternion form.
		q = new Quaternion();
		
		Quaternion expected = new Quaternion(-1f, -2f, -3f, -4f); 
				
		Quaternion.mult(q1, q2, q);
		assertTrue(q.equals(expected));
		
		// Now reverse order.
		Quaternion.mult(q2, q1, q);
		assertTrue(q.equals(expected));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_mult3() {
		q1 = new Quaternion(2f, 2f, 2f, 2f);
		q2 = new Quaternion(1f, 1f, 1f, 1f);
		
		Quaternion expected = new Quaternion(4f, 4f, 4f, -4f);
		
		q = Quaternion.mult(q1, q2);
		assertTrue(q.equals(expected));
		
		// Now reverse order.
		q = Quaternion.mult(q2, q1);
		assertTrue(q.equals(expected));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_mult4() {
		q1 = new Quaternion(1f, 2f, 3f, 4f);
		q2 = new Quaternion(0f, 0f, 0f, 0f);
		
		Quaternion expected = new Quaternion(0f, 0f, 0f, 0f);
		
		q = Quaternion.mult(q1, q2);
		assertTrue(q.equals(expected));
		
		// Now reverse order.
		q = Quaternion.mult(q2, q1);
		assertTrue(q.equals(expected));
	}

	//--------------------------------------------------------------------------
	@Test(expected=IllegalArgumentException.class)
	public void test_fromAxisAngle_throws1() {
		q = new Quaternion(new Vector3f(0f, 0f, 0f), 1f);
	}
	
	//--------------------------------------------------------------------------
	@Test(expected=IllegalArgumentException.class)
	public void test_fromAxisAngle_throws2() {
		q = new Quaternion(1f, 2f, 3f, 4f);
		
		q.fromAxisAngle(new Vector3f(0f, 0f, 0f), -1f);
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_fromAxisAngle1() {
		float angle = (float)(Math.PI);
		Vector3f axis = new Vector3f(0f, 1f, 0f);
		
		q = new Quaternion(axis, angle);
		Quaternion expected = new Quaternion(0f, 1f, 0f, (float)Math.cos(0.5f * angle));
		
		assertTrue(expected.equals(q));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_fromAxisAngle2() {
		float angle = (float)(Math.PI);
		Vector3f axis = new Vector3f(1f, 2f, 3f);
		
		q = new Quaternion(axis, angle);
		
		axis.normalise();
		Quaternion expected = new Quaternion(axis.x, axis.y, axis.z,
				(float) Math.cos(0.5f * angle));
		
		assertTrue(expected.equals(q));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_copy() {
		q1 = new Quaternion(-1.01f, 200.003f, 9000012f, 0.000001f);
		q2 = new Quaternion();
		
		Quaternion.copy(q1, q2);
		
		assertTrue(q1.equals(q2));
		assertTrue(q2.equals(q1));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_conjugate1() {
		q = new Quaternion(1f, 2f, 3f, 4f);
		q.conjugate();
		
		Quaternion expected = new Quaternion(-1f, -2f, -3f, 4f);
		
		assertTrue(expected.equals(q));
		assertTrue(q.equals(expected));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_conjugate2() {
		q1 = new Quaternion(1f, 2f, 3f, 4f);
		q2 = new Quaternion();
		Quaternion.conjugate(q1, q2);
		
		Quaternion expected = new Quaternion(-1f, -2f, -3f, 4f);
		
		assertTrue(expected.equals(q2));
		assertTrue(q2.equals(expected));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_conjugate3() {
		q1 = new Quaternion(1f, 2f, 3f, 4f);
		q2 = Quaternion.conjugate(q1);
		
		Quaternion expected = new Quaternion(-1f, -2f, -3f, 4f);
		
		assertTrue(expected.equals(q2));
		assertTrue(q2.equals(expected));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_norm() {
		q = new Quaternion(1f, 2f, 3f, 4f);
		float expected = (float)Math.sqrt(1f*1f + 2f*2f + 3f*3f + 4f*4f);
		
		assertEquals(expected, q.norm(), EPSILON);
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_normSquared() {
		q = new Quaternion(1f, 2f, 3f, 4f);
		float expected = (float)(1f*1f + 2f*2f + 3f*3f + 4f*4f);
		
		assertEquals(expected, q.normSquared(), EPSILON);
	}	
	
	//--------------------------------------------------------------------------
	@Test
	public void test_scale(){
		q = new Quaternion(1f, 2f, 3f, 4f);
		q.scale(2);
		
		Quaternion expected = new Quaternion(2f, 4f, 6f, 8f);
		
		assertTrue(expected.equals(q));
		assertTrue(q.equals(expected));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_scale2() {
		q1 = new Quaternion(1f, 2f, 3f, 4f);
		
		q2 = Quaternion.scale(q1, 2f);
		
		Quaternion expected = new Quaternion(2f, 4f, 6f, 8f);
		
		assertTrue(expected.equals(q2));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_scale3() {
		q1 = new Quaternion(1f, 2f, 3f, 4f);
		q2 = new Quaternion();
		
		Quaternion.scale(q1, 2f, q2);
		
		Quaternion expected = new Quaternion(2f, 4f, 6f, 8f);
		
		assertTrue(expected.equals(q2));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_inverse() {
		q = new Quaternion(3f, 2f, 1f, 1f);
		
		q.inverse();
		
		Quaternion expected = new Quaternion(3f, 2f, 1f, 1f);
		float r = 1f / expected.normSquared();
		expected.x *= -1f * r;
		expected.y *= -1f * r;
		expected.z *= -1f * r;
		expected.w *= r;
		
		assertTrue(expected.equals(q));
		assertTrue(q.equals(expected));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_inverse2() {
		q1 = new Quaternion(-3f, 2f, -1f, -1f);
		q2 = new Quaternion();
		
		Quaternion.inverse(q1, q2);
		
		Quaternion expected = new Quaternion(-3f, 2f, -1f, -1f);
		float r = 1f / expected.normSquared();
		expected.x *= -1 * r;
		expected.y *= -1 * r;
		expected.z *= -1 * r;
		expected.w *= r;
		
		assertTrue(expected.equals(q2));
		assertTrue(q2.equals(expected));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_inverse3() {
		q1 = new Quaternion(-30f, 2f, -1f, 0.0001f);
		
		q2 = Quaternion.inverse(q1);
		
		
		Quaternion expected = new Quaternion(-30f, 2f, -1f, 0.0001f);
		float r = 1f / expected.normSquared();
		expected.x *= -1 * r;
		expected.y *= -1 * r;
		expected.z *= -1 * r;
		expected.w *= r;
		
		assertTrue(expected.equals(q2));
		assertTrue(q2.equals(expected));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_inverse4() {
		q1 = new Quaternion(0.5f, 0.5f, 0.5f, 0.5f);
		
		q2 = Quaternion.inverse(q1);
		
		Quaternion expected = new Quaternion(0.5f, 0.5f, 0.5f, 0.5f);
		float r = 1f / expected.normSquared();
		expected.x *= -1 * r;
		expected.y *= -1 * r;
		expected.z *= -1 * r;
		expected.w *= r;
		
		assertTrue(expected.equals(q2));
		assertTrue(q2.equals(expected));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_inverse_zero_length() {
		q1 = new Quaternion(0.0f, 0.0f, 0.0f, 0.0f);
		
		q2 = Quaternion.inverse(q1);
		
		Quaternion expected = new Quaternion(0.0f, 0.0f, 0.0f, 0.0f);
		
		assertTrue(expected.equals(q2));
		assertTrue(q2.equals(expected));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_toRotationMatrix() {
		q = new Quaternion(1f, 0f, 0f, 0f);
		Matrix4f r = q.toRotationMatrix();
		
		Matrix4f expected = new Matrix4f();
		expected.m11 = -1f;
		expected.m22 = -1f;
		
		assertTrue(matrix4fEquals(expected, r));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_toRotationMatrix2() {
		q = new Quaternion(0f, 1f, 0f, 0f);
		Matrix4f r = q.toRotationMatrix();
		
		Matrix4f expected = new Matrix4f();
		expected.m00 = -1f;
		expected.m22 = -1f;
		
		assertTrue(matrix4fEquals(expected, r));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_toRotationMatrix3() {
		q = new Quaternion(0f, 0f, 1f, 0f);
		Matrix4f r = q.toRotationMatrix();
		
		Matrix4f expected = new Matrix4f();
		expected.m00 = -1f;
		expected.m11 = -1f;
		
		assertTrue(matrix4fEquals(expected, r));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_toRotationMatrix4() {
		q = new Quaternion(0f, 0f, 0f, 1f);
		Matrix4f r = q.toRotationMatrix();
		
		Matrix4f expected = new Matrix4f();
		
		assertTrue(matrix4fEquals(expected, r));
	}
	
	//--------------------------------------------------------------------------
	@Test
	public void test_toRotationMatrix5() {
		q = new Quaternion(1f, 2f, 3f, 4f);
		Matrix4f r = q.toRotationMatrix();
		
		Matrix4f expected = new Matrix4f();
		float s = 2f / q.norm();
		expected.m00 = 1 - s*(2*2 + 3*3);
		expected.m01 = s*(1*2 + 4*3);
		expected.m02 = s*(1*3 - 4*2);
		
		expected.m10 = s*(1*2 - 4*3);
		expected.m11 = 1 - s*(1*1 + 3*3);
		expected.m12 = s*(2*3 + 4*1);
		
		expected.m20 = s*(1*3 + 4*2);
		expected.m21 = s*(2*3 - 4*1);
		expected.m22 = 1 - s*(1*1 + 2*2);
		
		assertTrue(matrix4fEquals(expected, r));
	}
	
	//--------------------------------------------------------------------------
	/*
	 * Rotate x-basis vector CCW about z-basis vector.
	 */
	@Test
	public void test_rotate_Xaxis_CCW_about_Zaxis() {
		q1 = new Quaternion(new Vector3f(0f, 0f, 1f), (float)(0.5f * Math.PI));
		q2 = new Quaternion(1f, 0f, 0f, 0f);
		
		q1.rotate(q2);
		
		Quaternion expected = new Quaternion(0f, 1f, 0f, 0f);
		
		assertTrue(expected.equals(q2));
	}
	
	//--------------------------------------------------------------------------
	/*
	 * Rotate x-basis vector (with w-component) CCW about z-basis vector.
	 */
	@Test
	public void test_rotate_Xaxis_CCW_about_Zaxis_2() {
		q1 = new Quaternion(new Vector3f(0f, 0f, 1f), (float)(0.5f * Math.PI));
		q2 = new Quaternion(1f, 0f, 0f, 1f);
		
		q1.rotate(q2);
		
		Quaternion expected = new Quaternion(0f, 1f, 0f, 1f);
		
		assertTrue(expected.equals(q2));
	}
	
	//--------------------------------------------------------------------------
	/*
	 * Rotate x-basis vector CW about z-basis vector.
	 */
	@Test
	public void test_rotate_Xaxis_CW_about_Zaxis() {
		q1 = new Quaternion(new Vector3f(0f, 0f, 1f), -1f*(float)(0.5f * Math.PI));
		q2 = new Quaternion(1f, 0f, 0f, 1f);
		
		q1.rotate(q2);
		
		Quaternion expected = new Quaternion(0f, -1f, 0f, 1f);
		
		assertTrue(expected.equals(q2));
	}
	
	//--------------------------------------------------------------------------
	/*
	 * Rotate x-basis vector CW about z-basis vector (non-unit length).
	 */
	@Test
	public void test_rotate4_nonUnitLength() {
		q1 = new Quaternion(new Vector3f(0f, 0f, 5f), -1f*(float)(0.5f * Math.PI));
		q2 = new Quaternion(1f, 0f, 0f, 1f);
		
		q1.rotate(q2);
		
		Quaternion expected = new Quaternion(0f, -1f, 0f, 1f);
		
		assertTrue(expected.equals(q2));
	}
	
	//--------------------------------------------------------------------------
	/*
	 * Rotate x-basis vector CCW about y-basis vector.
	 */
	@Test
	public void test_rotate_Xaxis_CCW_about_Yaxis() {
		q1 = new Quaternion(new Vector3f(0f, 1f, 0f), (float)(0.5f * Math.PI));
		q2 = new Quaternion(1f, 0f, 0f, 1f);
		
		q1.rotate(q2);
		
		Quaternion expected = new Quaternion(0f, 0f, -1f, 1f);
		
		assertTrue(expected.equals(q2));
	}
	
	//--------------------------------------------------------------------------
	/*
	 * Rotate x-basis vector CW about y-basis vector.
	 */
	@Test
	public void test_rotate_Xaxis_CW_about_Yaxis() {
		q1 = new Quaternion(new Vector3f(0f, 1f, 0f), -1f*(float)(0.5f * Math.PI));
		q2 = new Quaternion(1f, 0f, 0f, 1f);
		
		q1.rotate(q2);
		
		Quaternion expected = new Quaternion(0f, 0f, 1f, 1f);
		
		assertTrue(expected.equals(q2));
	}
	
	//--------------------------------------------------------------------------
	/*
	 * Attempt to rotate x-basis vector CW about itself, resulting in no change.
	 */
	@Test
	public void test_rotate_Xasis_CW_about_itself() {
		q1 = new Quaternion(new Vector3f(1f, 0f, 0f), -1f*(float)(0.5f * Math.PI));
		q2 = new Quaternion(1f, 0f, 0f, 1f);
		Quaternion expected = new Quaternion(q2);
		
		q1.rotate(q2);
		
		assertTrue(expected.equals(q2));
	}
	
	//--------------------------------------------------------------------------
	/*
	 * Rotate y-basis vector CCW about x-basis vector.
	 */
	@Test
	public void test_rotate_Yaxis_CCW_about_Xaxis() {
		q1 = new Quaternion(new Vector3f(1f, 0f, 0f), (float)(0.5f * Math.PI));
		q2 = new Quaternion(0f, 1f, 0f, 2f);
		
		q1.rotate(q2);
		
		Quaternion expected = new Quaternion(0f, 0f, 1f, 2f);
		
		assertTrue(expected.equals(q2));
	}
	
	//--------------------------------------------------------------------------
	/*
	 * Rotate y-basis vector CW about x-basis vector.
	 */
	@Test
	public void test_rotate_Yaxis_CW_about_Xaxis() {
		q1 = new Quaternion(new Vector3f(1f, 0f, 0f), -1f*(float)(0.5f * Math.PI));
		q2 = new Quaternion(0f, 1f, 0f, 2f);
		
		q1.rotate(q2);
		
		Quaternion expected = new Quaternion(0f, 0f, -1f, 2f);
		
		assertTrue(expected.equals(q2));
	}
	
	//--------------------------------------------------------------------------
	/*
	 * Rotate y-basis vector CCW about z-basis vector.
	 */
	@Test
	public void test_rotate_Yaxis_CCW_about_Zaxis() {
		q1 = new Quaternion(new Vector3f(0f, 0f, 1f), (float)(0.5f * Math.PI));
		q2 = new Quaternion(0f, 1f, 0f, 2f);
		
		q1.rotate(q2);
		
		Quaternion expected = new Quaternion(-1f, 0f, 0f, 2f);
		
		assertTrue(expected.equals(q2));
	}
	
	//--------------------------------------------------------------------------
	/*
	 * Rotate y-basis vector CW about z-basis vector.
	 */
	@Test
	public void test_rotate_Yaxis_CW_about_Zaxis() {
		q1 = new Quaternion(new Vector3f(0f, 0f, 1f), -1f*(float)(0.5f * Math.PI));
		q2 = new Quaternion(0f, 1f, 0f, 2f);
		
		q1.rotate(q2);
		
		Quaternion expected = new Quaternion(1f, 0f, 0f, 2f);
		
		assertTrue(expected.equals(q2));
	}
	
	//--------------------------------------------------------------------------
    /*
     * Attempt to rotate quaternion CCW zero degrees about the zero-vector,
     * resulting in no change.
     */
	@Test
	public void test_rotate_zero_quaternion() {
		q1 = new Quaternion(0f, 0f, 0f, 0f);
		q2 = new Quaternion(1f, 2f, 3f, 4f);
		Quaternion expected = new Quaternion(q2);
		
		// Rotation of zero degrees.
		q1.rotate(q2);
		
		assertTrue(expected.equals(q2));
	}
	
	//--------------------------------------------------------------------------
	/*
	 * Rotate z-basis vector CCW about x-basis vector.
	 */
	@Test
	public void test_rotate_Zaxis_CCW_about_Xaxis() {
		q1 = new Quaternion(new Vector3f(1f, 0f, 0f), (float)(0.5f * Math.PI));
		q2 = new Quaternion(0f, 0f, 1f, 0f);
		
		q1.rotate(q2);
		
		Quaternion expected = new Quaternion(0f, -1f, 0f, 0f);
		
		assertTrue(expected.equals(q2));
	}
	
	//--------------------------------------------------------------------------
	/*
	 * Rotate z-basis vector CW about x-basis vector.
	 */
	@Test
	public void test_rotate_Zaxis_CW_about_Xaxis() {
		q1 = new Quaternion(new Vector3f(1f, 0f, 0f), -1f*(float)(0.5f * Math.PI));
		q2 = new Quaternion(0f, 0f, 1f, 0f);
		
		q1.rotate(q2);
		
		Quaternion expected = new Quaternion(0f, 1f, 0f, 0f);
		
		assertTrue(expected.equals(q2));
	}
	
	//--------------------------------------------------------------------------
	/*
	 * Rotate z-basis vector CCW about y-basis vector.
	 */
	@Test
	public void test_rotate_Zaxis_CCW_about_Yaxis() {
		q1 = new Quaternion(new Vector3f(0f, 1f, 0f), (float)(0.5f * Math.PI));
		q2 = new Quaternion(0f, 0f, 1f, 0f);
		
		q1.rotate(q2);
		
		Quaternion expected = new Quaternion(1f, 0f, 0f, 0f);
		
		assertTrue(expected.equals(q2));
	}
	
	//--------------------------------------------------------------------------
	/*
	 * Rotate z-basis vector CW about y-basis vector.
	 */
	@Test
	public void test_rotate_Zaxis_CW_about_Yaxis() {
		q1 = new Quaternion(new Vector3f(0f, 1f, 0f), -1f*(float)(0.5f * Math.PI));
		q2 = new Quaternion(0f, 0f, 1f, 0f);
		
		q1.rotate(q2);
		
		Quaternion expected = new Quaternion(-1f, 0f, 0f, 0f);
		
		assertTrue(expected.equals(q2));
	}
}
