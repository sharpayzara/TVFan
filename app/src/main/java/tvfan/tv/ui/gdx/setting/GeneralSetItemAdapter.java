package tvfan.tv.ui.gdx.setting;
/**
 * 常规设置
 *
 * @author 曹利文
 *
 */
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid.GridAdapter;

import java.util.ArrayList;

public class GeneralSetItemAdapter extends GridAdapter {
	private Page page;
	private ArrayList<String> items = new ArrayList<String>();

	public GeneralSetItemAdapter(Page page) {
		this.page = page;
	}

	public void setData(ArrayList<String> items) {
		this.items = items;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
		GeneralSetItem item = null;

		if (convertActor == null) {
			item = new GeneralSetItem(page);
		} else {
			item = (GeneralSetItem) convertActor;
		}
		item.setScale(0.9f);
		item.setText(items.get(position).toString());
		item.setTag(position);
		return item;
	}

	@Override
	public int getCount() {
		return items.size();
	}


}
