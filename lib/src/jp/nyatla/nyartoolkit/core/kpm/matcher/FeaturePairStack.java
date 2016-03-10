package jp.nyatla.nyartoolkit.core.kpm.matcher;

import jp.nyatla.nyartoolkit.core.kpm.freak.FreakFeaturePoint;
import jp.nyatla.nyartoolkit.core.kpm.keyframe.FreakMatchPointSetStack;
import jp.nyatla.nyartoolkit.core.types.stack.NyARObjectStack;

public class FeaturePairStack extends NyARObjectStack<FeaturePairStack.Item>
{
	public FeaturePairStack(int i_length)
	{
		super(i_length, FeaturePairStack.Item.class);
	}
	public class Item {
		public FreakFeaturePoint query;
		public FreakMatchPointSetStack.Item ref;
	}
	protected FeaturePairStack.Item createElement()
	{
		return new Item();
	}	
}