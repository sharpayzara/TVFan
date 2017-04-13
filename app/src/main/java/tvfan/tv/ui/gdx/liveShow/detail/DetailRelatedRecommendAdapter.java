package tvfan.tv.ui.gdx.liveShow.detail;

import org.json.JSONArray;
import org.json.JSONObject;

import tvfan.tv.ui.gdx.liveShow.programList.PostGridHotItem;

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
		PostGridHotItem item = null;
		
		if (convertActor == null) {
			item = new PostGridHotItem(page, 400, 300);
		} else {
			item = (PostGridHotItem) convertActor;
		}
		item.setScale(1f);
		try {
			JSONObject obj = (JSONObject) this.items.get(position);
			item.update(obj.getString("image"), obj.getString("name"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}

	@Override
	public int getCount() {
//		 return items.length();
		return this.items.length();
	}

}
