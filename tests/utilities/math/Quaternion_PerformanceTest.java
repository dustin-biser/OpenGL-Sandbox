package utilities.math;

import util.math.Quaternion;


/**
 * Class for testing performance optimizations regarding Quaternion operations.
 * 
 * @author Dustin Biser
 *
 */
public class Quaternion_PerformanceTest extends Quaternion {
	
    
    // Cache Coherence Multiplication 1
	private static void mult_cc1(Quaternion lhs, Quaternion rhs, Quaternion dest) {
		float x = (lhs.y * rhs.z) - (lhs.z * rhs.y) + (rhs.w * lhs.x) + (lhs.w * rhs.x);
		float y = (lhs.z * rhs.x) - (lhs.x * rhs.z) + (rhs.w * lhs.y) + (lhs.w * rhs.y);
		float z = (lhs.x * rhs.y) - (lhs.y * rhs.x) + (rhs.w * lhs.z) + (lhs.w * rhs.z);
		float w = (lhs.w * rhs.w) - (lhs.x * rhs.x) - (lhs.y * rhs.y) - (lhs.z * rhs.z);
		
		dest.x = x;
		dest.y = y;
		dest.z = z;
		dest.w = w;
	}
	
    // Cache Coherence Multiplication 2
	private static void mult_cc2(Quaternion lhs, Quaternion rhs, Quaternion dest) {
	    float lx = lhs.x;
	    float ly = lhs.y;
	    float lz = lhs.z;
	    float lw = lhs.w;
	    
	    float rx = rhs.x;
	    float ry = rhs.y;
	    float rz = rhs.z;
	    float rw = rhs.w;
	    
	    
		float x = (ly * rz) - (lz * ry) + (rw * lx) + (lw * rx);
		float y = (lz * rx) - (lx * rz) + (rw * ly) + (lw * ry);
		float z = (lx * ry) - (ly * rx) + (rw * lz) + (lw * rz);
		float w = (lw * rw) - (lx * rx) - (ly * ry) - (lz * rz);
		
		dest.x = x;
		dest.y = y;
		dest.z = z;
		dest.w = w;
	}

    public static void main(String args[]) {
        int numTrials = 40;
        int numIterations = 1000;

        long durationAvg = 0;

        for (int i = 0; i < numTrials; i++) {
            durationAvg += doTest3(numIterations);
        }
        durationAvg /= numTrials;
        System.out.println("Test: " + durationAvg + "\tns");

    }

    private static long doTest1(int numCycles) {
        Quaternion q1 = new Quaternion(1, 2, 3, 4);
        Quaternion q2 = new Quaternion(-1, -2, -3, -4);
        Quaternion q3 = new Quaternion();

        long startTime = System.nanoTime();
        for (int trial = 0; trial < numCycles; trial++) {
            Quaternion.mult(q1, q2, q3);
        }
        long duration = System.nanoTime() - startTime;

        return duration;
    }

    private static long doTest2(int numCycles) {
        Quaternion q1 = new Quaternion(1, 2, 3, 4);
        Quaternion q2 = new Quaternion(-1, -2, -3, -4);
        Quaternion q3 = new Quaternion();

        long startTime = System.nanoTime();
        for (int trial = 0; trial < numCycles; trial++) {
            Quaternion_PerformanceTest.mult_cc1(q1, q2, q3);
        }
        long duration = System.nanoTime() - startTime;

        return duration;
    }

    private static long doTest3(int numCycles) {
        Quaternion q1 = new Quaternion(1, 2, 3, 4);
        Quaternion q2 = new Quaternion(-1, -2, -3, -4);
        Quaternion q3 = new Quaternion();

        long startTime = System.nanoTime();
        for (int trial = 0; trial < numCycles; trial++) {
            Quaternion_PerformanceTest.mult_cc2(q1, q2, q3);
        }
        long duration = System.nanoTime() - startTime;

        return duration;
    }

}
