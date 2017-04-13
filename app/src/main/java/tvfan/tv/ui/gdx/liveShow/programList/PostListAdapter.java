package tvfan.tv.ui.gdx.liveShow.programList;

import java.util.List;

import tvfan.tv.dal.models.ProgramListItem;

import tvfan.tv.R;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.PagingGrid;
import com.luxtone.lib.widget.PagingGrid.PagingAdatper;

public class PostListAdapter extends PagingAdatper<ProgramListItem>{


	private Page page;
	private PagingGrid<ProgramListItem> mGrid; 
	private int pagecount = 0;
	private List<ProgramListItem> list;
	
	public PostListAdapter(Page page,PagingGrid<ProgramListItem> pgrid,int mpagecount){
		this.page=page;
		this.mGrid = pgrid;
		this.pagecount = mpagecount;
		this.list = this.mGrid.getDataList();
	}
	@Override
	public Actor getActor(int position, Actor convertActor) {
		PostGridHotItem item = null;

		if (convertActor == null) {
			item = new PostGridHotItem(page,400,300);
		} else {
			item = (PostGridHotItem) convertActor;
			((PostGridHotItem)item).setImage(R.drawable.list_mr);
		}
		try {
			item.setScale(1f);
			item.update(this.mGrid.getDataList().get(position).getPostImg(),
					this.mGrid.getDataList().get(position).getPostName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return item;
	}

	@Override
	public int getTotalCount() {
		// TODO Auto-generated method stub
		return this.pagecount;
	}
	
	
	public void setData(List<ProgramListItem> items) {
		this.list = items;
	}
}
