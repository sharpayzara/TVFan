package tvfan.tv.ui.gdx.widgets;

import org.json.JSONArray;
import org.json.JSONException;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.R;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid;

/**
 * 首页底部的菜单栏的grid
 * @author ddd
 *
 */
public class NavBar extends Grid {

	class NavBarData extends GridAdapter {
		private JSONArray _listData;
		private Page _page;

		public NavBarData(Page page, JSONArray listData) {
			_listData = listData;
			_page = page;
		}

		@Override
		public Actor getActor(int arg0, Actor arg1) {
			if (arg1 == null) {
				NavBarItem nbi = null;
				try {
					nbi = new NavBarItem(_page, _listData.getJSONObject(arg0), arg0);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				nbi.toFront();
				arg1 = nbi;
			}
//			if (_selectedIdx == arg0) {
//				((NavBarItem)arg1).setShowImage(true);
//			} else {
//				((NavBarItem)arg1).setShowImage(false);
//			}
			return arg1;
		}

		@Override
		public int getCount() {
			return _listData.length();
		}

	}

	private int _selectedIdx = 0;
	private Image _cursor;
	private int _cursorX = 0, _cursorY = 0;

	public NavBar(Page page, JSONArray listData) {
		super(page);
		Group g = new Group(page);
//		g.setPosition((AppGlobalConsts.APP_WIDTH - 240 * listData.length()) / 2, 0);
		g.setPosition(120, 0);
		_cursor = new Image(page);
		_cursor.setDrawableResource(R.drawable.home_light_big);// foucsline
		_cursor.setSize(200f, 215f);
		_cursor.setPosition(_cursorX + 28, _cursorY);
		_cursor.setFocusAble(false);
		g.addActor(_cursor);
		g.toBack();
		page.addActor(g);
		setRowNum(listData.length());
		setAdapter(new NavBarData(page, listData));

	}

	@Override
	public void onFocusChanged(Actor actor, boolean hasFocus) {
		super.onFocusChanged(actor, hasFocus);
	}

	/**
	 * 设置首页菜单栏的选中
	 * @param selectedIdx
	 */
	public void setSelected(int selectedIdx) {
		if (_selectedIdx == selectedIdx)
			return;

		//进行选中背景图片的移动
		_cursor.addAction(Actions.moveBy((selectedIdx - _selectedIdx) * 240, 0, .2f));
		_selectedIdx = selectedIdx;
	}

	public void setItemFocus(boolean isFocus) {
		for (Actor a : getChildren())
			a.setFocusAble(isFocus);
	}

	public void setFocusImg(int rimg) {
		_cursor.setDrawableResource(rimg);
	}

	public void setCursorVisible(boolean isShow) {
		_cursor.setVisible(isShow);
	}

	public void showReddot(boolean isShow) {
		for (Actor a : getChildren()) {
			if (a instanceof NavBarItem)
				((NavBarItem) a).showReddot(isShow);
		}
	}
}
