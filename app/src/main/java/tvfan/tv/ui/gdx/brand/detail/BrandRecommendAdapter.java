package tvfan.tv.ui.gdx.brand.detail;

import java.util.ArrayList;

import org.json.JSONObject;

import tvfan.tv.ui.gdx.programDetail.item.DetailGridItem;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid.GridAdapter;

public class BrandRecommendAdapter extends GridAdapter {
	private Page page;
	private ArrayList<JSONObject> items = new ArrayList<JSONObject>();

	public BrandRecommendAdapter(Page page) {
		this.page = page;
	}

	public void setData(ArrayList<JSONObject> items) {
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
		try{
		item.setText(items.get(position).optString("name"));
		item.update(items.get(position).optString("image"));
		

		}catch(Exception e){
			e.printStackTrace();
		}
		return item;
	}

	@Override
	public int getCount() {
		return items.size();
	}


}
