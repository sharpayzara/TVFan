package tvfan.tv.ui.gdx.programList;

import java.util.List;

import tvfan.tv.dal.models.ProgramMenus;
import tvfan.tv.R;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid.GridAdapter;

public class MenuListAdapter extends GridAdapter{
	
	private Page page;
	private List<ProgramMenus> menulist;
	private MenuListItem item = null;
	private String mHeadTitle;//当前界面的具体标题（是什么分类“电视剧、电影、动漫”）
	public MenuListAdapter(Page page){
		this.page=page;
	}

	public void setMenulist(List<ProgramMenus> menulist, String mHeadTitle) {
		this.menulist = menulist;
		this.mHeadTitle = mHeadTitle;
		
	}
	
	
	/**
	 * 动漫的节目列表删除集合中筛选相关的数据
	 */
	private void setList(){
		if("动漫".equals(mHeadTitle)||"短视频".equals(mHeadTitle)||"音乐".equals(mHeadTitle)||"游戏".equals(mHeadTitle)||"微电影".equals(mHeadTitle)){
			menulist.remove(1);
		}
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
		
		/*
		 * 动漫节目列表的时候对左侧栏的修改，改回原来的直接删除下面的，放开上面的就可以了
		 */
		if(position == 0){
			item = new MenuListItem(page, R.mipmap.list_search_selected);
		}else{
			if("短视频".equals(mHeadTitle)||"音乐".equals(mHeadTitle)||"游戏".equals(mHeadTitle)||"微电影".equals(mHeadTitle)){
				item = new MenuListItem(page);
			}else{
				if(position == 1){
					item = new MenuListItem(page, R.mipmap.list_filter_normal);
				} else 
				item = new MenuListItem(page);
			}
		}
		if(menulist!=null&&menulist.size()>0){
			item.setText(menulist.get(position).getName());
		}
		item.setScale(1f);
		return item;
		/*------------------------------------------------------*/
	}
	
	public void setFocusImgbg(boolean display){
		((MenuListItem)item).setFocusImgBg(display);
	}

}
