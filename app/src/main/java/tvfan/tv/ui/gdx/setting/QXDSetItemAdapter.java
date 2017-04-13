package tvfan.tv.ui.gdx.setting;
/**
 *  desc  启动设置界面
 *  @author  yangjh
 *  created at  16-4-19 下午3:56
 */
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid.GridAdapter;

import java.util.ArrayList;
import java.util.List;

import tvfan.tv.dal.models.GeneralSetBean;

public class QXDSetItemAdapter extends GridAdapter {
	private Page page;
	private List<GeneralSetBean> items = new ArrayList();

	public QXDSetItemAdapter(Page page) {
		this.page = page;
	}

	public void setData(List items) {
		this.items = items;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
		QDGeneralSetItem item = null;

		if (convertActor == null) {
			item = new QDGeneralSetItem(page);
		} else {
			item = (QDGeneralSetItem) convertActor;
		}
		item.setNextFocusLeft(position+"");
		item.setName(position+"");
		item.setScale(0.9f);
		if(position == 0){
			item.setMidText("启动设置");
		}else{
			item.setText(items.get(position - 1).getmName());
		}


		return item;
	}

	@Override
	public int getCount() {
		return items.size() + 1;
	}


}
