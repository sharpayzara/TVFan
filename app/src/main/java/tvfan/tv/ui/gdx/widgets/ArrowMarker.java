package tvfan.tv.ui.gdx.widgets;

import tvfan.tv.R;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.luxtone.lib.gdx.Page;

public class ArrowMarker extends Group {

	private final String TAG = "TVFAN.EPG.ArrowMarker";

	private Image _left, _top, _right, _down;

	public enum Arrow {
		left,right,top,down
	};

	public ArrowMarker(Page page, Arrow[] OpenArrows, int[] pos) {
		super(page);
		for(int i = 0; i < OpenArrows.length; i++) {
			switch(OpenArrows[i]){
				case down:
					_down = new Image(page);
					_down.setDrawableResource(R.drawable.portal_arrow_down);
					_down.setPosition(pos[i*2], pos[i*2+1]);
					addActor(_down);
					break;
				case left:
					_left = new Image(page);
					_left.setDrawableResource(R.drawable.portal_arrow_left);
					_left.setPosition(pos[i*2], pos[i*2+1]);
					addActor(_left);
					break;
				case right:
					_right = new Image(page);
					_right.setDrawableResource(R.drawable.portal_arrow_right);
					_right.setPosition(pos[i*2], pos[i*2+1]);
					addActor(_right);
					break;
				case top:
					_top = new Image(page);
					_top.setDrawableResource(R.drawable.portal_arrow_up);
					_top.setPosition(pos[i*2], pos[i*2+1]);
					addActor(_top);
					break;
				default:
					break;
			}
		}
	}

	public void setVisible(Arrow arrow, boolean isShow) {
		switch(arrow){
			case down:
				_down.setVisible(isShow);
				break;
			case left:
				_left.setVisible(isShow);
				break;
			case right:
				_right.setVisible(isShow);
				break;
			case top:
				_top.setVisible(isShow);
				break;
			default:
				break;
		}
	}
}
