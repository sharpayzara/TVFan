package tvfan.tv.ui.gdx.programList;

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
		if(list == null){
			this.list = this.mGrid.getDataList();
		}
	}
	@Override
	public Actor getActor(int position, Actor convertActor) {
		PostGridItem item = null;

		if (convertActor == null) {
			item = new PostGridItem(page,0,7);

		} else {
			item = (PostGridItem) convertActor;
			((PostGridItem)item).setImage(R.drawable.placeholder);
			((PostGridItem)item).setLogoImage(R.drawable.placeholder_logo);
		}
		

		//角标 END
		try {
			item.setScale(1f);
			if(this.list!=null&&this.list.size()>0 /*&& !App.keyDown*/){
				item.update(this.list.get(position).getPostImg(),
						this.list.get(position).getPostName(),this.list.get(position).getScore());
			}
			
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
