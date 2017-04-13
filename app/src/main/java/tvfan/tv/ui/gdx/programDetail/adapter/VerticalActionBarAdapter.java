package tvfan.tv.ui.gdx.programDetail.adapter;

import java.util.ArrayList;

import tvfan.tv.ui.gdx.programDetail.item.VerticalActionBarItem;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.Grid.GridAdapter;

public class VerticalActionBarAdapter extends GridAdapter{
	private Page page;
	private ArrayList<String> data ;

	public VerticalActionBarAdapter(Page page){
		this.page = page;
	}
	
	public void setData(ArrayList<String> data ){
		this.data = data;
	}
	
	@Override
	public Actor getActor(int position, Actor convertActor) {
		VerticalActionBarItem item = null;

		if (convertActor == null) {
			item = new VerticalActionBarItem(page);
		} else {
			item = (VerticalActionBarItem) convertActor;
		}
		item.setText(data.get(position));
		item.setTextSize(50);
		return item;
	}

	@Override
	public int getCount() {
		return data.size();
	}


}
