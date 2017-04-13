package tvfan.tv.ui.gdx.brand;

import org.json.JSONArray;
import org.json.JSONObject;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid.GridAdapter;

public class BrandIndexAdapter extends GridAdapter {
	private Page page;
	private JSONArray items;

	public BrandIndexAdapter(Page page) {
		this.page = page;
	}

	public void setData(JSONArray items) {
		this.items = items;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
		BrandGridItem item = null;

		if (convertActor == null) {
			item = new BrandGridItem(page);
		} else {
			item = (BrandGridItem) convertActor;
		}
		item.setScale(1f);
		JSONObject jo;
		try {
			jo = items.getJSONObject(position);
//			item.setText("中国熊猫");
//			item.update("http://images.ott.cibntv.net/2014/04/10/zhonguoxiongmao.jpg#");
			 item.setText(jo.optString("name").toString());
			 item.update(jo.optString("innerimg").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}

	@Override
	public int getCount() {
		 return items.length();
//		return 40;
	}

}
