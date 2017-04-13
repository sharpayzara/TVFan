package tvfan.tv.ui.gdx.ranking;
/**
 * 個人中心-左側用戶界面
 * 
 * @author 孫文龍
 * 
 */
import java.util.List;

import tvfan.tv.dal.models.ProgramMenus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid.GridAdapter;

public class HotMenuListAdapter extends GridAdapter{
	
	private Page page;
	private List<ProgramMenus> menulist;
	private HotMenuListItem item = null;
	public HotMenuListAdapter(Page page){
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
			item = new HotMenuListItem(page,position);
		} else {
			item = (HotMenuListItem) convertActor;
		}		
		if(menulist!=null&&menulist.size()>0){
			item.setText(menulist.get(position).getName());
		}
		item.setScale(1f);
		return item;
	}
	
	public void setFocusImgbg(boolean display){
		((HotMenuListItem)item).setFocusImgBg(display);
	}

}
