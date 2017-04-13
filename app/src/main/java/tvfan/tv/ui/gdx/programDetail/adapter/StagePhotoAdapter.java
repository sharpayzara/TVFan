package tvfan.tv.ui.gdx.programDetail.adapter;

import org.json.JSONArray;

import tvfan.tv.ui.gdx.programDetail.item.StagePhotoGridItem;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid.GridAdapter;

public class StagePhotoAdapter extends GridAdapter {
	private Page page;
	private JSONArray items = new JSONArray();

	public StagePhotoAdapter(Page page) {
		this.page = page;
	}

	public void setData(JSONArray items) {
		this.items = items;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
		StagePhotoGridItem item = null;

		if (convertActor == null) {
			item = new StagePhotoGridItem(page);
		} else {
			item = (StagePhotoGridItem) convertActor;
		}

		item.setScale(1f);
		String imageurl;
		try {
			imageurl = (String) items.get(position);
//			 item.setText("中国熊猫");
//			 item.update("http://images.ott.cibntv.net/2014/04/10/zhonguoxiongmao.jpg#");
			item.update(imageurl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// item.update("http://images.ott.cibntv.net/2014/04/10/zhonguoxiongmao.jpg#");
		return item;
	}

	@Override
	public int getCount() {
		// return items.size();
		return items.length();
//		return 10;
	}

}
