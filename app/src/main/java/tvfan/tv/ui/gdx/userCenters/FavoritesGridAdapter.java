package tvfan.tv.ui.gdx.userCenters;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid.GridAdapter;

import java.util.ArrayList;
import java.util.List;

import tvfan.tv.dal.models.ProgramListItem;

/**
 * 专题总界面
 * 
 * @author 孫文龍
 * 
 */
public class FavoritesGridAdapter extends GridAdapter {
	private Page page;
	List<ProgramListItem> programList = new ArrayList<ProgramListItem>();;

	public FavoritesGridAdapter(Page page) {
		this.page = page;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
		FavoritesGridItem item = null;

		if (convertActor == null) {
			item = new FavoritesGridItem(page);
		} else {
			item = (FavoritesGridItem) convertActor;
		}
		try {
			ProgramListItem i = programList.get(position);
			item.update(i.getPostImg());
			item.setText(i.getPostName());
		/*	item.setLeftCornerImage(i.getCornerPrice());
			item.setRightCornerImage(i.getCornerType());*/
			if (i.getMark().equals("0")) {
				item.setNewImageGone(true);
			} else {
				item.setNewImageGone(false);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return item;
	}

	public void setData(List<ProgramListItem> list) {
		this.programList = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return programList.size();
	}

}
