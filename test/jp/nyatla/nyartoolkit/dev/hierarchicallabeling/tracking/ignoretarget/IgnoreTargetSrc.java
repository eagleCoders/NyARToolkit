package jp.nyatla.nyartoolkit.dev.hierarchicallabeling.tracking.ignoretarget;

import jp.nyatla.nyartoolkit.NyARException;
import jp.nyatla.nyartoolkit.core.types.stack.NyARObjectStack;
import jp.nyatla.nyartoolkit.dev.hierarchicallabeling.tracking.AreaTargetSrcPool;
import jp.nyatla.nyartoolkit.dev.hierarchicallabeling.tracking.contour.ContoureTargetList;

public class IgnoreTargetSrc extends NyARObjectStack<IgnoreTargetSrc.NyARIgnoreSrcItem>
{
	public static class NyARIgnoreSrcItem
	{
		public AreaTargetSrcPool.AreaTargetSrcItem area_src;
		public int match_index;
		public void attachToTarget(IgnoreTargetList.IgnoreTargetItem o_output)
		{
			o_output.ref_area.deleteMe();
			o_output.ref_area=this.area_src;
		}
	}
	private AreaTargetSrcPool _ref_area_pool;
	public IgnoreTargetSrc.NyARIgnoreSrcItem pushSrcTarget(AreaTargetSrcPool.AreaTargetSrcItem i_item)
	{
		IgnoreTargetSrc.NyARIgnoreSrcItem item=this.prePush();
		if(item==null){
			return null;
		}
		item.area_src=i_item;
		item.match_index=-1;
		return item;
	}
	protected NyARIgnoreSrcItem createElement()
	{
		return new NyARIgnoreSrcItem();
	}
	public IgnoreTargetSrc(int i_size,AreaTargetSrcPool i_area_pool) throws NyARException
	{
		this._ref_area_pool=i_area_pool;
		super.initInstance(i_size,NyARIgnoreSrcItem.class);
	}
	public void clear()
	{
		//所有するオブジェクトを開放してからクリア処理
		for(int i=this._length-1;i>=0;i--){
			if(this._items[i].area_src!=null){
				this._ref_area_pool.deleteObject(this._items[i].area_src);
			}
		}
		super.clear();
	}
}