package perspectiveBox;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import utilities.GLUtils;
import utilities.LwjglWindow;
import utilities.ShaderUtils;

public class PerspectiveBox extends LwjglWindow {
	
	public static void main(String[] args) {
		new PerspectiveBox().start();
	}
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	// GL identifiers.
	private int programId;
	private int vao;
	private int vboPositions;
	private int vboColors;
	private int vboIndices;
	
	private byte[] indices;
	
	// Matrix related data.
	private Matrix4f projectionMatrix;
	private FloatBuffer matrix4fBuffer;
	private int projectionMatrixLocation;
	private int offsetLocation;
	private Vector3f modelOffset;
	
	// Eye coordinate frustum dimensions
	private float frustumWidth = 2f;
	private float frustumHeight = 2f;
	private float frustumNearDistance = 0.5f;
	private float frustumFarDistance = 1.2f;
	
	@Override
	protected void initialize(){
		this.setupGL();
		this.setupShaders();
		this.setupMatrices();
		this.setupVertexBuffer();
		this.setupVertexArrayObject();
	}
	
	@Override
	protected void logicCycle(){
		this.processUserInput();
		this.updateMatrixUniforms();
	}
	
	@Override
	protected void renderCycle(){
		glClear(GL_COLOR_BUFFER_BIT);
		glUseProgram(programId);
		
		glBindVertexArray(vao);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_BYTE, 0);
		
		glBindVertexArray(0);
		glUseProgram(0);
		
		GLUtils.exitOnGLError("renderCycle");
	} 
	
	@Override
	protected void resize(int width, int height) {
		float aspectRatio = (((float) width) / height);
		float frustumXScale = 2 * frustumNearDistance / frustumWidth;
		float frustumYScale = 2 * frustumNearDistance / frustumHeight;
		
		if (width > height) {
			// Shrink the x scale in eye-coordinate space, so that when geometry is
			// projected to ndc-space, it is widened out to become square.
	    	projectionMatrix.m00 = frustumXScale / aspectRatio;
	    	projectionMatrix.m11 = frustumYScale;
		}
		else {
			// Shrink the y scale in eye-coordinate space, so that when geometry is
			// projected to ndc-space, it is widened out to become square.
	    	projectionMatrix.m00 = frustumXScale;
	    	projectionMatrix.m11 = frustumYScale * aspectRatio;
		}
    	
    	// Use entire window for rendering.
    	glViewport(0, 0, width, height);
	}
	
	@Override
	protected void cleanup(){
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(vboPositions);
		glDeleteBuffers(vboColors);
		glDeleteBuffers(vboIndices);
		
		glBindVertexArray(0);
		glDeleteVertexArrays(vao);
		
		glDeleteProgram(programId);
		
		Display.destroy();
	}
	
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	private void setupGL(){
		// Render only the front face of geometry.
		glEnable(GL_CULL_FACE);
	    glCullFace(GL_BACK);
	    glFrontFace(GL_CCW);
	    
		// Setup an XNA like background color
		glClearColor(0.4f, 0.6f, 0.9f, 0f);
	}
	
	private void setupShaders(){
		String vertexShaderFile = "src/perspectiveBox/shaders/MatrixPerspective.vert";
		int vertexShaderId = ShaderUtils.loadShader(vertexShaderFile, GL_VERTEX_SHADER);
		
		String fragmentShaderFile = "src/perspectiveBox/shaders/Standard.frag"; 
		int fragmentShaderId = ShaderUtils.loadShader(fragmentShaderFile, GL_FRAGMENT_SHADER);
		
		
		programId = glCreateProgram();
		glAttachShader(programId, vertexShaderId);
		glAttachShader(programId, fragmentShaderId);
		
		ShaderUtils.linkProgram(programId);
		
		// Bind vertex attribute locations.
		glBindAttribLocation(programId, 0, "in_Position");
		glBindAttribLocation(programId, 1, "in_Color");
		
		
		glUseProgram(programId);
		
		// Get location shader uniforms.
		projectionMatrixLocation = glGetUniformLocation(programId, "projectionMatrix");
		offsetLocation = glGetUniformLocation(programId, "offset");
		
		glValidateProgram(programId);
		
		GLUtils.exitOnGLError("setupShaders");
		
		glUseProgram(0);
	}
	
	private void setupMatrices(){
		// Setup "eye coordinate frustum" to "normalized device coordinate cube"
		// perspective projection matrix.
		projectionMatrix = GLUtils.createProjectionMatrix(frustumWidth, frustumHeight,
				frustumNearDistance, frustumFarDistance);
		
		matrix4fBuffer = BufferUtils.createFloatBuffer(16);
	}
	
	private void setupVertexBuffer(){
		final float vertexPositions[] = {
			// bottom face
			-1, -1,   1,
			-1, -1,  -1,
			 1, -1,  -1,
			 1, -1,   1,
		    
		    // top face
			-1,  1,  1,
			 1,  1,  1,
			 1,  1, -1,
			-1,  1, -1,
			
			// near face
			-1, -1,   1,
			 1, -1,   1,
			 1,  1,   1,
			-1,  1,   1,
		
			// far face
			 1, -1, -1,
			-1, -1, -1,
			-1,  1, -1,
			 1,  1, -1,
			 
		    // left face
			-1, -1, -1,
			-1, -1,  1,
			-1,  1,  1,
			-1,  1, -1,
			
			
			// right face
			 1, -1,  1,
			 1, -1, -1,
			 1,  1, -1,
			 1,  1,  1,
		};
		
		final float vertexColors[] = {
			// bottom face
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 1.0f,
			
		    // top face
			0.8f, 0.8f, 0.8f, 1.0f,
			0.8f, 0.8f, 0.8f, 1.0f,
			0.8f, 0.8f, 0.8f, 1.0f,
			0.8f, 0.8f, 0.8f, 1.0f,
			
			// near face
			0.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			
			// far face
			0.5f, 0.5f, 0.0f, 1.0f,
			0.5f, 0.5f, 0.0f, 1.0f,
			0.5f, 0.5f, 0.0f, 1.0f,
			0.5f, 0.5f, 0.0f, 1.0f,
			
			// left face
			1.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 0.0f, 1.0f,
			
			// right face
			0.0f, 1.0f, 1.0f, 1.0f,
			0.0f, 1.0f, 1.0f, 1.0f,
			0.0f, 1.0f, 1.0f, 1.0f,
			0.0f, 1.0f, 1.0f, 1.0f
			};
		
		indices = new byte[]{
				// bottom face
				0, 1, 2,
				2, 3, 0,
				
				// top face
				4, 5, 6,
				6, 7, 4,
				
				// near face
				8, 9, 10,
				10, 11, 8,
				
				// far face
				12, 13, 14,
				14, 15, 12,
				
				// left face
				16, 17, 18,
				18, 19, 16,
				
				// right face
				20, 21, 22,
				22, 23, 20
		};
		
		// Move box into scene.
		modelOffset = new Vector3f(2f, 2f, -2f);
		
		//-- Put position data into a FloatBuffer.
		FloatBuffer vertexPositionBuffer = BufferUtils
				.createFloatBuffer(vertexPositions.length);
		vertexPositionBuffer.put(vertexPositions);
		vertexPositionBuffer.flip();
		
		//-- Put color data into a FloatBuffer.
		FloatBuffer vertexColorBuffer = BufferUtils
				.createFloatBuffer(vertexColors.length);
		vertexColorBuffer.put(vertexColors);
		vertexColorBuffer.flip();
		
		//-- Put index data into a ByteBuffer.
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indices.length);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		
		//-- Map VBO to position data within OpenGL.
		vboPositions = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboPositions);
		glBufferData(GL_ARRAY_BUFFER, vertexPositionBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		//-- Map VBO to color data within OpenGL.
		vboColors = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboColors);
		glBufferData(GL_ARRAY_BUFFER, vertexColorBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		//-- Map VBO to  index data within OpenGL.
		vboIndices = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndices);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		
	}
	
	private void setupVertexArrayObject(){
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		glEnableVertexAttribArray(0);  // Enable Position vertex attributes.
		glEnableVertexAttribArray(1);  // Enable Colors vertex attributes.
		
		glBindBuffer(GL_ARRAY_BUFFER, vboPositions);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, vboColors);
		glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIndices);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	private void updateMatrixUniforms(){
		//-- Update OpenGL Matrices
		// Upload uniform shader data.
		glUseProgram(programId);
		
		projectionMatrix.store(matrix4fBuffer);
		matrix4fBuffer.flip();
		glUniformMatrix4(projectionMatrixLocation, false, matrix4fBuffer);
		
		glUseProgram(0);
		
		GLUtils.exitOnGLError("logicCycle");
	}
	
	private void processUserInput(){
		final float x_delta = 0.2f; 
		final float y_delta = 0.2f; 
		final float z_delta = 0.2f; 
				
		while(Keyboard.next()){
			if(Keyboard.getEventKeyState()){
				if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE){
					// Set flag to break out of main loop.
					continueMainLoop = false;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_LEFT){
					modelOffset.translate(-1*x_delta, 0, 0);
				}
				else if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT){
					modelOffset.translate(x_delta, 0, 0);
				}
				else if (Keyboard.getEventKey() == Keyboard.KEY_UP){
					modelOffset.translate(0, y_delta, 0);
				}
				else if (Keyboard.getEventKey() == Keyboard.KEY_DOWN){
					modelOffset.translate(0, -1*y_delta, 0);
				}
				else if (Keyboard.getEventKey() == Keyboard.KEY_P){
					modelOffset.translate(0, 0, z_delta);
				}
				else if (Keyboard.getEventKey() == Keyboard.KEY_M){
					modelOffset.translate(0, 0, -1*z_delta);
				}
			}
		}
		
		// Upload shader uniform data.
		glUseProgram(programId);
		glUniform3f(offsetLocation, modelOffset.x, modelOffset.y, modelOffset.z);
		
		glUseProgram(0);
		
		GLUtils.exitOnGLError("processUserInput");
	}
	
}
