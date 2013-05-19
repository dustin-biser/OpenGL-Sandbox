package utilities;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import static org.lwjgl.opengl.GL11.*;

public class LwjglWindow {
	private String windowTitle = "LWJGL Window";
	protected boolean continueMainLoop = true;
	
	// Timing info in milliseconds.
	private float elapsedTime;
	private float lastFrameDuration;
	private float lastFrameTimeStamp;
	private float now;
	
	public void setWindowTitle(String windowTitle){
		this.windowTitle = windowTitle;
	}
	
	public final void start() {
		start(500, 500);
	}

	public final void start(int windowWidth, int windowHeight){
		PixelFormat pixelFormat = new PixelFormat();
		ContextAttribs contextAttribs = new ContextAttribs(4, 2)
			.withForwardCompatible(true) // Remove import of deprecated fixed functionality.
			.withProfileCore(true);      // Bring in support for only modern GL.
		
		try {
			Display.setDisplayMode(new DisplayMode(windowWidth, windowHeight));
			Display.setTitle(windowTitle);
			Display.create(pixelFormat, contextAttribs);
			
			if (!GLContext.getCapabilities().OpenGL42) {
				System.err.printf("Need at least OpenGL 4.2 to run program.");
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		Display.setResizable(true);
		
		long startTime = System.nanoTime();
		initialize();
		resize(windowWidth, windowHeight);
		
		while (!Display.isCloseRequested() && continueMainLoop) {
			elapsedTime = (float) ((System.nanoTime() - startTime) / 1000000.0);
			now = System.nanoTime();
			lastFrameDuration = (float)((now - lastFrameTimeStamp) / 1000000.0);
			lastFrameTimeStamp = now;
			
			Display.sync(60);
			Display.update();
			
			this.logicCycle();
			this.renderCycle();
			
			if(Display.wasResized()) {
				resize(Display.getWidth(), Display.getHeight());
			}
			
			if (displayFPS) {
				printFPS();
			}
		}
		
		this.cleanup();
	}
	
	private boolean displayFPS = false;
	
	public void enableFpsDisplay() {
		displayFPS = true;
	}
	
	public void disableFpsDisplay() {
		displayFPS = false;
	}
	
	private byte counter = 0;
	
	private void printFPS(){
		if (counter == 60) {
			counter = 0;
			System.out.println("FPS: " + 1000 / lastFrameDuration);
		}
		counter++;
	}
	
	protected void initialize() {
		// To be overridden.
	}
	
	protected void logicCycle(){
		while(Keyboard.next()){
			if(Keyboard.getEventKeyState()){
				if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE){
					// Set flag to break out of main loop.
					continueMainLoop = false;
				}
			}
		}
	}
	
	protected void renderCycle(){
		glClearColor(0.4f, 0.6f, 0.9f, 0f);
		glClear(GL_COLOR_BUFFER_BIT);
	}
	
	protected void resize(int width, int height) {
		glViewport(0, 0, width, height);
	}
	
	protected final float getElapsedTime(){
		return elapsedTime;
	}
	
	protected final float getLastFrameDuration(){
		return lastFrameDuration;
	}

	protected void cleanup(){
		Display.destroy();
	}
	
	protected final void leaveMainLoop() {
		continueMainLoop = false;
	}
	
	public static void main(String[] args){
		LwjglWindow window = new LwjglWindow();
		
		window.setWindowTitle("A Basic Lwjgl Window");
		window.start(400, 600);
	}
}