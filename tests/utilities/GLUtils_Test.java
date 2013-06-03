package utilities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import util.GLUtils;

public class GLUtils_Test {
	private float aspectRatio;
	private float fieldOfView;
	private float nearDistance;
	private float farDistance;
	
	private float left;
	private float right;
	private float bottom;
	private float top;
	
	private float width;
	private float height;
	

	@Before
	public void setUp() throws Exception {
		aspectRatio = 1f;
		fieldOfView = 45f;
		nearDistance = 1f;
		farDistance = 5f;
		
		left = -1f;
		right = 1f;
		bottom = -1f;
		top = 1f;
		
		width = 1f;
		height = 1f;
	}
	
    /////////////////////////////////////////////////////////////////////////////////
	// Test createProjectionMatrixFov(float, float, float, float)
    /////////////////////////////////////////////////////////////////////////////////

	@Test(expected=IllegalArgumentException.class)
	public void test_createProjectionMatrixFov_negative_nearDist() {
		nearDistance = -1f;
		GLUtils.createProjectionMatrixFov(aspectRatio, fieldOfView, nearDistance,
				farDistance);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_createProjectionMatrixFov_negative_farDist() {
		farDistance = -5f;
		GLUtils.createProjectionMatrixFov(aspectRatio, fieldOfView, nearDistance,
				farDistance);
	}

	@Test(expected=IllegalArgumentException.class)
	public void test_createProjectionMatrixFov_farDist_lessThan_nearDist() {
		nearDistance = 2f;
		farDistance = 1f;
		GLUtils.createProjectionMatrixFov(aspectRatio, fieldOfView, nearDistance,
				farDistance);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_createProjectionMatrixFov_farDist_zero() {
		farDistance = 0f;
		GLUtils.createProjectionMatrixFov(aspectRatio, fieldOfView, nearDistance,
				farDistance);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_createProjectionMatrixFov_nearDist_equald_farDist() {
		nearDistance = 1f;
		farDistance = 1f;
		GLUtils.createProjectionMatrixFov(aspectRatio, fieldOfView, nearDistance,
				farDistance);
	}
	
    /////////////////////////////////////////////////////////////////////////////////
	// Test createProjectionMatrix(float, float, float, float, float, float)
    /////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=IllegalArgumentException.class)
	public void test_createProjectionMatrix_negative_nearDist() {
		nearDistance = -1f;
		GLUtils.createProjectionMatrix(left, right, bottom, top, nearDistance,
				farDistance);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_createProjectionMatrix_negative_farDist() {
		farDistance = -5f;
		GLUtils.createProjectionMatrix(left, right, bottom, top, nearDistance,
				farDistance);
	}

	@Test(expected=IllegalArgumentException.class)
	public void test_createProjectionMatrix_farDist_lessThan_nearDist() {
		nearDistance = 2f;
		farDistance = 1f;
		GLUtils.createProjectionMatrix(aspectRatio, fieldOfView, nearDistance,
				farDistance);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_createProjectionMatrix_farDist_zero() {
		farDistance = 0f;
		GLUtils.createProjectionMatrix(aspectRatio, fieldOfView, nearDistance,
				farDistance);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_createProjectionMatrix_nearDist_equald_farDist() {
		nearDistance = 1f;
		farDistance = 1f;
		GLUtils.createProjectionMatrix(aspectRatio, fieldOfView, nearDistance,
				farDistance);
	}
	
    /////////////////////////////////////////////////////////////////////////////////
	// Test createProjectionMatrix(float, float, float, float)
    /////////////////////////////////////////////////////////////////////////////////
	
	@Test(expected=IllegalArgumentException.class)
	public void test_createProjectionMatrix_negative_nearDist2() {
		nearDistance = -1f;
		GLUtils.createProjectionMatrix(width, height, nearDistance, farDistance);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_createProjectionMatrix_negative_farDist2() {
		farDistance = -5f;
		GLUtils.createProjectionMatrix(width, height, nearDistance, farDistance);
	}

	@Test(expected=IllegalArgumentException.class)
	public void test_createProjectionMatrix_farDist_lessThan_nearDist2() {
		nearDistance = 2f;
		farDistance = 1f;
		GLUtils.createProjectionMatrix(left, right, bottom, top, nearDistance,
				farDistance);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_createProjectionMatrix_farDist_zero2() {
		farDistance = 0f;
		GLUtils.createProjectionMatrix(aspectRatio, fieldOfView, nearDistance,
				farDistance);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void test_createProjectionMatrix_nearDist_equald_farDist2() {
		nearDistance = 1f;
		farDistance = 1f;
		GLUtils.createProjectionMatrix(aspectRatio, fieldOfView, nearDistance,
				farDistance);
	}
}
