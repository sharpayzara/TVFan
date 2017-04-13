package tvfan.tv.ui.gdx.programDetail.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import tvfan.tv.ui.gdx.programDetail.item.DetailGridItem;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid.GridAdapter;

public class DetailRelatedRecommendAdapter extends GridAdapter {
	private Page page;
	private JSONArray items;

	public DetailRelatedRecommendAdapter(Page page) {
		this.page = page;
	}

	public void setData(JSONArray items) {
		this.items = items;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
		DetailGridItem item = null;

		if (convertActor == null) {
			item = new DetailGridItem(page);
		} else {
			item = (DetailGridItem) convertActor;
		}
		item.setScale(1f);
		item.setNextFocusUp("playBtn");
		JSONObject jo;
		try {
			jo = items.getJSONObject(position);
			item.setText(jo.optString("name").toString());
			item.update(jo.optString("image").toString());
			item.setScore1(jo.optString("score".toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}

	@Override
	public int getCount() {
		if(items.length() > 12)
			return 12;
		return items.length();
		// return 40;
	}

}
