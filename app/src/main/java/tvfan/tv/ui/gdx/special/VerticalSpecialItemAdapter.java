package tvfan.tv.ui.gdx.special;

import java.util.ArrayList;
import java.util.List;

import tvfan.tv.dal.models.ProgramListItem;
import tvfan.tv.ui.gdx.programDetail.item.DetailGridItem;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid.GridAdapter;

/**
 * 專題匯總界面
 * 
 * @author 孫文龍
 * 
 */
public class VerticalSpecialItemAdapter extends GridAdapter {
	private Page page;
	List<ProgramListItem> programList = new ArrayList<ProgramListItem>();

	public VerticalSpecialItemAdapter(Page page) {
		this.page = page;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
		DetailGridItem item = null;

		if (convertActor == null) {
			item = new DetailGridItem(page);
		} else {
			item = (DetailGridItem) convertActor;
		}
		item.setText(programList.get(position).getPostName());
		item.update(programList.get(position).getPostImg());
		return item;
	}
	public void setData(List<ProgramListItem> list) {
		this.programList = list;
	}

	@Override
	public int getCount() {
		return programList.size();
	}

}
