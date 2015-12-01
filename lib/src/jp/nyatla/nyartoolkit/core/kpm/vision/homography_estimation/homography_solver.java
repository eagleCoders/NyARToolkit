package jp.nyatla.nyartoolkit.core.kpm.vision.homography_estimation;

import jp.nyatla.nyartoolkit.core.kpm.Point2d;
import jp.nyatla.nyartoolkit.core.kpm.vision.math.math_utils;
import jp.nyatla.nyartoolkit.core.kpm.vision.math.liner_algebr;
import jp.nyatla.nyartoolkit.core.kpm.vision.math.liner_solver;

public class homography_solver {

	/**
	 * Condition four 2D points such that the mean is zero and the standard
	 * deviation is sqrt(2).
	 */
	static boolean Condition4Points2d(Point2d xp1, Point2d xp2, Point2d xp3,
			Point2d xp4, float[] mus, // ms[2],sの3要素
			Point2d x1, Point2d x2, Point2d x3, Point2d x4) {
		float[] d1 = new float[2], d2 = new float[2], d3 = new float[2], d4 = new float[2];

		mus[0] = (x1.x + x2.x + x3.x + x4.x) / 4;
		mus[1] = (x1.y + x2.y + x3.y + x4.y) / 4;

		d1[0] = x1.x - mus[0];
		d1[1] = x1.y - mus[1];
		d2[0] = x2.x - mus[0];
		d2[1] = x2.y - mus[1];
		d3[0] = x3.x - mus[0];
		d3[1] = x3.y - mus[1];
		d4[0] = x4.x - mus[0];
		d4[1] = x4.y - mus[1];
/*
		mus[0] = (x1[0] + x2[0] + x3[0] + x4[0]) / 4;
		mus[1] = (x1[1] + x2[1] + x3[1] + x4[1]) / 4;

		d1[0] = x1[0] - mus[0];
		d1[1] = x1[1] - mus[1];
		d2[0] = x2[0] - mus[0];
		d2[1] = x2[1] - mus[1];
		d3[0] = x3[0] - mus[0];
		d3[1] = x3[1] - mus[1];
		d4[0] = x4[0] - mus[0];
		d4[1] = x4[1] - mus[1];

 */
		float ds1 = (float) Math.sqrt(d1[0] * d1[0] + d1[1] * d1[1]);
		float ds2 = (float) Math.sqrt(d2[0] * d2[0] + d2[1] * d2[1]);
		float ds3 = (float) Math.sqrt(d3[0] * d3[0] + d3[1] * d3[1]);
		float ds4 = (float) Math.sqrt(d4[0] * d4[0] + d4[1] * d4[1]);
		float d = (ds1 + ds2 + ds3 + ds4) / 4;

		if (d == 0) {
			return false;
		}

		float s = (float) ((1 / d) * math_utils.SQRT2);
		mus[2] = s;
		xp1.x = d1[0] * s;
		xp1.y = d1[1] * s;
		xp2.x = d2[0] * s;
		xp2.y = d2[1] * s;
		xp3.x = d3[0] * s;
		xp3.y = d3[1] * s;
		xp4.x = d4[0] * s;
		xp4.y = d4[1] * s;

		return true;
	}

	/**
	 * Add a point to the homography constraint matrix.
	 */
	static void AddHomographyPointContraint(float A[], int A_ptr, Point2d x, Point2d xp) {
		A[A_ptr + 0] = -x.x;//[0];
		A[A_ptr + 1] = -x.y;//[1];
		A[A_ptr + 2] = -1;
		// ZeroVector3(A+3);
		A[A_ptr + 3] = 0;
		A[A_ptr + 4] = 0;
		A[A_ptr + 5] = 0;

		A[A_ptr + 6] = xp.x * x.x;//xp[0] * x[0];
		A[A_ptr + 7] = xp.y * x.y;//xp[0] * x[1];
		A[A_ptr + 8] = xp.x;//xp[0];

		// ZeroVector3(A+9);
		A[A_ptr + 9] = 0;
		A[A_ptr + 10] = 0;
		A[A_ptr + 11] = 0;

		A[A_ptr + 12] = -x.x;//-x[0];
		A[A_ptr + 13] = -x.y;//-x[1];
		A[A_ptr + 14] = -1;
		A[A_ptr + 15] = xp.y * x.x;//xp[1] * x[0];
		A[A_ptr + 16] = xp.y * x.y;//xp[1] * x[1];
		A[A_ptr + 17] = xp.y;//xp[1];
	}

	/**
	 * Construct the homography constraint matrix from 4 point correspondences.
	 */
	static void Homography4PointsInhomogeneousConstraint(
			float[] A,// [72],
			Point2d x1, Point2d x2, Point2d x3, Point2d x4, Point2d xp1,
			Point2d xp2, Point2d xp3, Point2d xp4) {
		AddHomographyPointContraint(A, 0, x1, xp1);
		AddHomographyPointContraint(A, 18, x2, xp2);
		AddHomographyPointContraint(A, 36, x3, xp3);
		AddHomographyPointContraint(A, 54, x4, xp4);
	}

	/**
	 * Solve for the homography given four 2D point correspondences.
	 */
	static boolean SolveHomography4PointsInhomogenous(float[] H, Point2d x1,
			Point2d x2, Point2d x3, Point2d x4, Point2d xp1, Point2d xp2,
			Point2d xp3, Point2d xp4) {
		float[] A = new float[72];
		Homography4PointsInhomogeneousConstraint(A, x1, x2, x3, x4, xp1, xp2,
				xp3, xp4);
		if (!liner_solver.SolveNullVector8x9Destructive(H, A)) {
			return false;
		}
		if (Math.abs(liner_algebr.Determinant3x3(H)) < 1e-5) {
			return false;
		}
		return true;
	}

	/**
	 * Solve for the homography given 4 point correspondences.
	 */
	static // boolean SolveHomography4Points(float H[9],
	// float x1[2],
	// float x2[2],
	// float x3[2],
	// float x4[2],
	// float xp1[2],
	// float xp2[2],
	// float xp3[2],
	// float xp4[2]) {
	boolean SolveHomography4Points(float[] H, Point2d x1, Point2d x2,
			Point2d x3, Point2d x4, Point2d xp1, Point2d xp2, Point2d xp3,
			Point2d xp4) {
		float[] Hn = new float[9];

		// T s, sp;
		// T t[2], tp[2];

		Point2d x1p = new Point2d(), x2p = new Point2d(), x3p = new Point2d(), x4p = new Point2d();
		Point2d xp1p = new Point2d(), xp2p = new Point2d(), xp3p = new Point2d(), xp4p = new Point2d();
		float[] ts = new float[3];
		float[] tps = new float[3];
		//
		// Condition the points
		//

		if (!Condition4Points2d(x1p, x2p, x3p, x4p, ts, x1, x2, x3, x4)) {
			return false;
		}
		if (!Condition4Points2d(xp1p, xp2p, xp3p, xp4p, tps, xp1, xp2, xp3, xp4)) {
			return false;
		}

		//
		// Solve for the homography
		//

		if (!SolveHomography4PointsInhomogenous(Hn, x1p, x2p, x3p, x4p, xp1p,
				xp2p, xp3p, xp4p)) {
			return false;
		}

		//
		// Denomalize the computed homography
		//

		DenormalizeHomography(H, Hn, ts, tps);

		return true;
	}

	/**
	 * Denomalize the homograhy H.
	 * 
	 * Hp = inv(Tp)*H*T
	 * 
	 * where T and Tp are the noramalizing transformations for the points that
	 * were used to compute H.
	 */
	/**
	 * void DenormalizeHomography(T Hp[9], const T H[9], T s, const T t[2], T
	 * sp, const T tp[2]) {
	 */
	static void DenormalizeHomography(float[] Hp, float[] H, float[] ts, float tps[]) {
		float sp = tps[2];
		float a = H[6] * tps[0];
		float b = H[7] * tps[0];
		float c = H[0] / sp;
		float d = H[1] / sp;
		float apc = a + c;
		float bpd = b + d;

		float e = H[6] * tps[1];
		float f = H[7] * tps[1];
		float g = H[3] / sp;
		float h = H[4] / sp;
		float epg = e + g;
		float fph = f + h;

		float s = ts[2];
		float stx = s * ts[0];
		float sty = s * ts[1];

		Hp[0] = s * apc;
		Hp[1] = s * bpd;
		Hp[2] = H[8] * tps[0] + H[2] / sp - stx * apc - sty * bpd;

		Hp[3] = s * epg;
		Hp[4] = s * fph;
		Hp[5] = H[8] * tps[1] + H[5] / sp - stx * epg - sty * fph;

		Hp[6] = H[6] * s;
		Hp[7] = H[7] * s;
		Hp[8] = H[8] - Hp[6] * ts[0] - Hp[7] * ts[1];
	}
}
