package jp.nyatla.nyartoolkit.core.kpm.matcher.binaryfeature;

import jp.nyatla.nyartoolkit.core.kpm.freak.FreakFeaturePoint;
import jp.nyatla.nyartoolkit.core.kpm.freak.FreakFeaturePointStack;
import jp.nyatla.nyartoolkit.core.kpm.keyframe.FreakMatchPointSetStack;
import jp.nyatla.nyartoolkit.core.kpm.keyframe.Keyframe;
import jp.nyatla.nyartoolkit.core.kpm.matcher.FeaturePairStack;
import jp.nyatla.nyartoolkit.core.kpm.matcher.InverseHomographyMat;
import jp.nyatla.nyartoolkit.core.types.NyARDoublePoint2d;


public class BinaryFeatureMatcher
{
	// Threshold on the 1st and 2nd best matches
	final protected double mThreshold;

	public BinaryFeatureMatcher() {
		this.mThreshold = 0.7f;
	}
	/**
	 * Match two feature stores.
	 * 
	 * @return Number of matches
	 */
	public int match(FreakFeaturePointStack i_query, Keyframe i_key_frame,FeaturePairStack i_maches)
	{
		FreakMatchPointSetStack ref=i_key_frame.getFeaturePointSet();
		if (i_query.getLength() == 0 || ref.getLength() == 0) {
			return 0;
		}
		// mMatches.reserve(features1.size());
		for (int i = 0; i < i_query.getLength(); i++) {
			int first_best = Integer.MAX_VALUE;
			int second_best = Integer.MAX_VALUE;
			int best_index = Integer.MAX_VALUE;

			// Search for 1st and 2nd best match
			FreakFeaturePoint p1 = i_query.getItem(i);
			for (int j = 0; j < ref.getLength(); j++) {
				// Both points should be a MINIMA or MAXIMA
				if (p1.maxima != ref.getItem(j).maxima) {
					continue;
				}

				// ASSERT(FEATURE_SIZE == 96, "Only 96 bytes supported now");
				int d = i_query.getItem(i).descripter.hammingDistance(ref.getItem(j).descripter);
				if (d < first_best) {
					second_best = first_best;
					first_best = d;
					best_index = (int) j;
				} else if (d < second_best) {
					second_best = d;
				}
			}

			// Check if FIRST_BEST has been set
			if (first_best != Integer.MAX_VALUE) {
				// If there isn't a SECOND_BEST, then always choose the FIRST_BEST.
				// Otherwise, do a ratio test.
				if (second_best == Integer.MAX_VALUE) {
					// mMatches.push_back(match_t((int)i, best_index));
					FeaturePairStack.Item t = i_maches.prePush();
					t.query=i_query.getItem(i);
					t.ref=ref.getItem(best_index);
				} else {
					// Ratio test
					double r = (double) first_best / (double) second_best;
					if (r < mThreshold) {
						FeaturePairStack.Item t = i_maches.prePush();
						t.query=i_query.getItem(i);
						t.ref=ref.getItem(best_index);
						// mMatches.push_back(match_t((int)i, best_index));
					}
				}
			}
		}
		// ASSERT(mMatches.size() <= features1->size(), "Number of matches should be lower");
		return i_maches.getLength();
	}





	/**
	 * Match two feature stores given a homography from the features in store 1 to store 2. The THRESHOLD is a spatial
	 * threshold in pixels to restrict the number of feature comparisons.
	 * 
	 * @return Number of matches
	 */
	public int match(FreakFeaturePointStack i_query, FreakMatchPointSetStack i_ref, InverseHomographyMat i_hinv,double tr,FeaturePairStack i_maches)
	{
		
		if (i_query.getLength()*i_ref.getLength() == 0) {
			return 0;
		}

		double tr_sqr = tr*tr;
		FreakFeaturePoint[] query_buf=i_query.getArray();
		FreakMatchPointSetStack.Item[] ref_buf=i_ref.getArray();
		int q_len=i_query.getLength();
		int r_len=i_ref.getLength();

		NyARDoublePoint2d tmp = new NyARDoublePoint2d();
		for (int i = 0; i < q_len; i++) {
			int first_best = Integer.MAX_VALUE;// std::numeric_limits<unsigned int>::max();
			int second_best = Integer.MAX_VALUE;// std::numeric_limits<unsigned int>::max();
			int best_index = Integer.MAX_VALUE;// std::numeric_limits<int>::max();

			FreakFeaturePoint fptr1 = query_buf[i];

			// Map p1 to p2 space through H
			i_hinv.multiplyPointHomographyInhomogenous(fptr1.x, fptr1.y, tmp);
			

			// Search for 1st and 2nd best match
			for (int j = 0; j < r_len; j++) {
				FreakFeaturePoint fptr2 = ref_buf[j];

				// Both points should be a MINIMA or MAXIMA
				if (fptr1.maxima != fptr2.maxima) {
					continue;
				}
				double tx=(tmp.x - fptr2.x);
				double ty=(tmp.y - fptr2.y);
				// Check spatial constraint
				if ((tx*tx)+(ty*ty) > tr_sqr) {
					continue;
				}

				int d = fptr1.descripter.hammingDistance(fptr2.descripter);
				if (d < first_best) {
					second_best = first_best;
					first_best = d;
					best_index = (int) j;
				} else if (d < second_best) {
					second_best = d;
				}
			}

			// Check if FIRST_BEST has been set
			if (first_best != Integer.MAX_VALUE) {

				// If there isn't a SECOND_BEST, then always choose the FIRST_BEST.
				// Otherwise, do a ratio test.
				if (second_best == Integer.MAX_VALUE) {
					FeaturePairStack.Item t = i_maches.prePush();
					t.query=fptr1;
					t.ref=ref_buf[best_index];
				} else {
					// Ratio test
					double r = (double) first_best / (double) second_best;
					if (r < this.mThreshold) {
						FeaturePairStack.Item t = i_maches.prePush();
						t.query=fptr1;
						t.ref=ref_buf[best_index];
					}
				}
			}
		}
		return i_maches.getLength();
	}
}
