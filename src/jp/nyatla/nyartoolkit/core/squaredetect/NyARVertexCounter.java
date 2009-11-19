/* 
 * PROJECT: NyARToolkit
 * --------------------------------------------------------------------------------
 * This work is based on the original ARToolKit developed by
 *   Hirokazu Kato
 *   Mark Billinghurst
 *   HITLab, University of Washington, Seattle
 * http://www.hitl.washington.edu/artoolkit/
 *
 * The NyARToolkit is Java edition ARToolKit class library.
 * Copyright (C)2008-2009 Ryo Iizuka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * For further information please contact.
 *	http://nyatla.jp/nyatoolkit/
 *	<airmail(at)ebony.plala.or.jp> or <nyatla(at)nyatla.jp>
 * 
 */
package jp.nyatla.nyartoolkit.core.squaredetect;

/**
 * get_vertex関数を切り離すためのクラス
 * 
 */
final public class NyARVertexCounter
{
	public final int[] vertex = new int[10];// 6まで削れる

	public int number_of_vertex;

	private double thresh;

	private int[] x_coord;

	private int[] y_coord;

	public boolean getVertex(int[] i_x_coord, int[] i_y_coord,int i_coord_len,int st, int ed, double i_thresh)
	{
		this.number_of_vertex = 0;
		this.thresh = i_thresh;
		this.x_coord = i_x_coord;
		this.y_coord = i_y_coord;
		return get_vertex(st, ed,i_coord_len);
	}

	/**
	 * static int get_vertex( int x_coord[], int y_coord[], int st, int ed,double thresh, int vertex[], int *vnum) 関数の代替関数
	 * 
	 * @param x_coord
	 * @param y_coord
	 * @param st
	 * @param ed
	 * @param thresh
	 * @return
	 */
	private boolean get_vertex(int st, int ed,int i_coord_len)
	{
		//メモ:座標値は65536を超えなければint32で扱って大丈夫なので変更。
		//dmaxは4乗なのでやるとしてもint64じゃないとマズイ
		int v1 = 0;
		final int[] lx_coord = this.x_coord;
		final int[] ly_coord = this.y_coord;
		final int a = ly_coord[ed] - ly_coord[st];
		final int b = lx_coord[st] - lx_coord[ed];
		final int c = lx_coord[ed] * ly_coord[st] - ly_coord[ed] * lx_coord[st];
		double dmax = 0;
		if(st<ed){
			//stとedが1区間
			for (int i = st + 1; i < ed; i++) {
				final double d = a * lx_coord[i] + b * ly_coord[i] + c;
				if (d * d > dmax) {
					dmax = d * d;
					v1 = i;
				}
			}
		}else{
			//stとedが2区間
			for (int i = st + 1; i < i_coord_len; i++) {
				final double d = a * lx_coord[i] + b * ly_coord[i] + c;
				if (d * d > dmax) {
					dmax = d * d;
					v1 = i;
				}
			}
			for (int i = 0; i < ed; i++) {
				final double d = a * lx_coord[i] + b * ly_coord[i] + c;
				if (d * d > dmax) {
					dmax = d * d;
					v1 = i;
				}
			}
		}

		
		if (dmax / (double)(a * a + b * b) > thresh) {
			if (!get_vertex(st, v1,i_coord_len)) {
				return false;
			}
			if (number_of_vertex > 5) {
				return false;
			}
			vertex[number_of_vertex] = v1;// vertex[(*vnum)] = v1;
			number_of_vertex++;// (*vnum)++;

			if (!get_vertex(v1, ed,i_coord_len)) {
				return false;
			}
		}
		return true;
	}
}