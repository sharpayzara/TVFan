package tvfan.tv.ui.gdx.programDetail.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import tvfan.tv.ui.gdx.programDetail.item.DetailTagItem;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid.GridAdapter;

public class DetailTagAdapter extends GridAdapter {
	private Page page;
	private JSONArray items;

	public DetailTagAdapter(Page page) {
		this.page = page;
	}

	public void setData(JSONArray items) {
		this.items = items;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
		DetailTagItem item = null;

		if (convertActor == null) {
			item = new DetailTagItem(page);
		} else {
			item = (DetailTagItem) convertActor;
		}
		item.setScale(1f);
		JSONObject jo;
		try {
			jo = items.getJSONObject(position);
			// item.setText("中国熊猫");
			// item.update("http://images.ott.cibntv.net/2014/04/10/zhonguoxiongmao.jpg#");
			item.setText(jo.optString("name").toString());
			String imageurl = jo.optString("image").toString();

			/*-------------------设置角标begin---------------------*/
			// 是否是付费
			String cornerPrice = jo.optString("cornerPrice", "0");
			// 高清，超清，预告片
			String cornerType = jo.optString("corner", "0");
			item.setLeftCornerImage(cornerPrice);
			item.setRightCornerImage(cornerType);
			/*-------------------设置角标end---------------------*/

			if (!imageurl.startsWith("http://")) {
				imageurl = "http://" + imageurl;
			}
			item.update(imageurl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}

	@Override
	public int getCount() {
		// return items.size();
		return items.length();
	}

}
