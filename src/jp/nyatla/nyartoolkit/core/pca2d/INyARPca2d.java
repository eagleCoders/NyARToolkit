/* 
 * PROJECT: NyARToolkit
 * --------------------------------------------------------------------------------
 * This work is based on the original ARToolKit developed by
 *   Hirokazu Kato
 *   Mark Billinghurst
 *   HITLab, University of Washington, Seattle
 * http://www.hitl.washington.edu/artoolkit/
 *
 * The NyARToolkit is Java version ARToolkit class library.
 * Copyright (C)2008 R.Iizuka
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this framework; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * For further information please contact.
 *	http://nyatla.jp/nyatoolkit/
 *	<airmail(at)ebony.plala.or.jp>
 * 
 */
package jp.nyatla.nyartoolkit.core.pca2d;

import jp.nyatla.nyartoolkit.NyARException;
import jp.nyatla.nyartoolkit.core.param.*;
import jp.nyatla.nyartoolkit.core.types.NyARDoublePoint2d;
import jp.nyatla.nyartoolkit.core.types.matrix.NyARDoubleMatrix22;

public interface INyARPca2d
{
	/**
	 * 通常のPCA
	 * @param i_x
	 * @param i_y
	 * @param i_start
	 * @param i_number_of_point
	 * @param o_evec
	 * @param o_ev
	 * @param o_mean
	 * @throws NyARException
	 */
	public void pca(double[] i_x,double[] i_y,int i_start,int i_number_of_point,NyARDoubleMatrix22 o_evec, NyARDoublePoint2d o_ev,NyARDoublePoint2d o_mean) throws NyARException;
	/**
	 * カメラ歪み補正つきのPCA
	 * @param i_x
	 * @param i_y
	 * @param i_start
	 * @param i_number_of_point
	 * @param i_factor
	 * @param o_evec
	 * @param o_mean
	 * @throws NyARException
	 */
	public void pcaWithDistortionFactor(int[] i_x,int[] i_y,int i_start,int i_number_of_point,NyARCameraDistortionFactor i_factor,NyARDoubleMatrix22 o_evec,NyARDoublePoint2d o_ev, NyARDoublePoint2d o_mean) throws NyARException;
}