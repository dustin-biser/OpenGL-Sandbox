package utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderUtils {
	/**
	 * Reads in a shader file, compiles it, and returns a shader ID if successful.
	 * @param filename - path String to shader file.
	 * @param type - Specifies the type of shader to be created. Must be one of
	 * GL_VERTEX_SHADER​, GL_TESS_CONTROL_SHADER​, GL_TESS_EVALUATION_SHADER​,
	 * GL_GEOMETRY_SHADER​, or GL_FRAGMENT_SHADER​.
	 * @return shader ID of the loaded shader program.
	 */
	public static int loadShader(String filename, int type) {
		StringBuilder shaderSource = new StringBuilder();
		int shaderID = 0;
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error in ShaderUtils.loadShader(String, int).  Could not read shader file: " + filename);
			e.printStackTrace();
			System.exit(-1);
		}
		
		shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			int maxInfoLength = 1024;
			StringBuilder message = new StringBuilder();
			message.append("Error in ShaderUtils.loadShader(String, int).  Could not compile shader: " + filename + "\n");
			message.append(GL20.glGetShaderInfoLog(shaderID, maxInfoLength));
			
			System.err.println(message);
			System.exit(-1);
		}
		
		GLUtils.exitOnGLError("ShaderUtils.loadShader(String, int)");
		
		return shaderID;
	}	
	
	/**
	 * Links the shader program referenced by <code>programId</code>, and reports any
	 * errors if found to the standard error output stream.
	 * @param programId - handle to the shader program.
	 */
	public static void linkProgram(int programId){
		GL20.glLinkProgram(programId);
		
		if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			int maxInfoLength = 1024;
			StringBuilder message = new StringBuilder();
			message.append("Could not link shader program:\n");
			message.append(GL20.glGetProgramInfoLog(programId, maxInfoLength));
			
			System.err.println(message);
			System.exit(-1);
		}
	}
}
