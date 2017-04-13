package tvfan.tv.ui.gdx.programHotList;

import java.util.List;

import tvfan.tv.dal.models.ProgramMenus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid.GridAdapter;

public class MenuListHotAdapter extends GridAdapter{
	
	private Page page;
	private List<ProgramMenus> menulist;
	private MenuListItem item = null;
	public MenuListHotAdapter(Page page){
		this.page=page;
	}
	
	
	public void setMenulist(List<ProgramMenus> menulist) {
		this.menulist = menulist;
	}

	@Override
	public int getCount() {
		// 获取总长度
		int count = 0;
		if(menulist!=null){
			count = menulist.size();
		}
		return count;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
		
		if (convertActor == null) {
			item = new MenuListItem(page);
		} else {
			item = (MenuListItem) convertActor;
		}		
		if(menulist!=null&&menulist.size()>0){
			item.setText(menulist.get(position).getName());
		}
		item.setScale(1f);
		return item;
	}
	
	public void setFocusImgbg(boolean display){
		((MenuListItem)item).setFocusImgBg(display);
	}

}
