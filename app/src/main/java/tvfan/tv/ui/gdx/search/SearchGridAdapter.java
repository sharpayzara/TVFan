package tvfan.tv.ui.gdx.search;

import tvfan.tv.dal.models.ProgramListItem;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.PagingGrid;
import com.luxtone.lib.widget.PagingGrid.PagingAdatper;

public class SearchGridAdapter extends PagingAdatper<ProgramListItem> {

	private Page page;
	private int totalnumber;
	private PagingGrid<ProgramListItem> mGrid;

	public SearchGridAdapter(Page page, PagingGrid<ProgramListItem> pgrid,
			int totalnumber) {
		this.page = page;
		this.mGrid = pgrid;
		this.totalnumber = totalnumber;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
		SearchGridItem item = null;

		if (convertActor == null) {
			item = new SearchGridItem(page, 0, 7);
		} else {
			item = (SearchGridItem) convertActor;
		}
		try {
			item.setScale(1f);
			item.update(this.mGrid.getDataList().get(position).getPostImg());
			item.setText(this.mGrid.getDataList().get(position).getPostName());
			item.setLeftCornerImage(this.mGrid.getDataList().get(position)
					.getCornerPrice());
			item.setRightCornerImage(this.mGrid.getDataList().get(position)
					.getCornerType());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return item;
	}

	@Override
	public int getTotalCount() {
		// TODO Auto-generated method stub
		return totalnumber;
	}

	public void setTotalCount(int totalnumber) {
		this.totalnumber = totalnumber;
	}

}
