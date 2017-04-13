package tvfan.tv.ui.gdx.ranking;

import tvfan.tv.dal.models.ProgramListItem;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.PagingGrid;
import com.luxtone.lib.widget.PagingGrid.PagingAdatper;

/**
 * 排名
 * 
 * @author sunwenlong
 * 
 */
public class RankingItemAdapter extends PagingAdatper<ProgramListItem> {
	private Page page;
	private PagingGrid<ProgramListItem> mGrid;

	public RankingItemAdapter(Page page, PagingGrid<ProgramListItem> pgrid,
			int mpagecount) {
		this.page = page;
		this.mGrid = pgrid;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
		RankingGridItem item = null;

		if (convertActor == null) {
			item = new RankingGridItem(page);
		} else {
			item = (RankingGridItem) convertActor;
		}
		item.setScale(1f);
		item.setLeftCornerImage(mGrid.getDataList().get(position).getCornerPrice());
		item.setText(mGrid.getDataList().get(position).getPostName(),position);
		item.update(mGrid.getDataList().get(position).getPostImg());
		return item;
	}

	@Override
	public int getTotalCount() {
		// TODO Auto-generated method stub
		return 0;
	}

}
