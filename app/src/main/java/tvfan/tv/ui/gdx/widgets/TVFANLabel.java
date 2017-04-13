package tvfan.tv.ui.gdx.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.luxtone.lib.gdx.Page;

public class TVFANLabel extends Label{

	public TVFANLabel(Page page) {
		super(page);
		setShadowLayer(0, 0, 0, 0);//去除文字黑边
	}
	
}
