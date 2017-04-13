package tvfan.tv.ui.gdx.programHotList;

import tvfan.tv.dal.models.ProgramListItem;

import tvfan.tv.R;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.PagingGrid;
import com.luxtone.lib.widget.PagingGrid.PagingAdatper;

public class PostListHotAdapter extends PagingAdatper<ProgramListItem>{


	private Page page;
	private PagingGrid<ProgramListItem> mGrid; 
	private int pagecount = 0;

	public PostListHotAdapter(Page page,PagingGrid<ProgramListItem> pgrid,int mpagecount){
		this.page=page;
		this.mGrid = pgrid;
		this.pagecount = mpagecount;
	}
	@Override
	public Actor getActor(int position, Actor convertActor) {
		PostGridItem item = null;

		if (convertActor == null) {
			item = new PostGridItem(page,0,7);
		} else {
			item = (PostGridItem) convertActor;
			((PostGridItem)item).setImage(R.drawable.list_mr);
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
	
	

}
