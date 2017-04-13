package tvfan.tv.ui.gdx.programDetail.adapter;

import java.util.ArrayList;

import tvfan.tv.dal.models.ProgramSourceBean;
import tvfan.tv.lib.GdxPageImageBatchLoader;
import tvfan.tv.ui.gdx.programDetail.item.VideoSourceGridItem;
import android.text.TextUtils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.widget.Grid.GridAdapter;

public class VideoSourceAdapter extends GridAdapter {
	private Page page;
	private ArrayList<ProgramSourceBean> sourceList;//节目切换源的相关数据
	private VideoSourceGridItem item = null;
	private GdxPageImageBatchLoader gpibl;//使用PageImageLoader进行图片加载的工具类
	public VideoSourceAdapter(Page page, ArrayList<ProgramSourceBean> sourceList) {
		this.page = page;
		this.sourceList = sourceList;
		gpibl = new GdxPageImageBatchLoader(page);
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
			
			final ProgramSourceBean source = sourceList.get(position);
		
		if (convertActor == null) {
			item = new VideoSourceGridItem(page);
		} else {
			item = (VideoSourceGridItem) convertActor;
		}

		item.setName("sourceItem");
		item.setNextFocusDown("sourceItem");
		item.setNextFocusLeft("sourceItem");
		item.setNextFocusRight("sourceItem");
		item.setNextFocusUp("sourcesBtn");
		item.setText(source.getCpName());
		gpibl.Get(source.getCpPic(), item.getIconImage());
		return item;
	}
	
	@Override
	public int getCount() {
		return sourceList.size();
	}
}
