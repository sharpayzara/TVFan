package tvfan.tv.ui.gdx.widgets;

import tvfan.tv.ui.gdx.programHotList.PostGridHotItem;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.luxtone.lib.gdx.Page;

public class HotGroup extends Group {

	private Label gdTitle;
	PostGridHotItem pgh,pgh2,pgh3,pgh4;
	
	
	public HotGroup(Page page) {
		super(page);
		initData(page);
	}

	private void initData(Page page) {
		
		gdTitle = new Label(getPage());
		gdTitle.setText("热播推荐");
		gdTitle.setColor(Color.WHITE);
		gdTitle.setAlpha(0.6f);
		gdTitle.setTextSize(50);
		gdTitle.setSize(300, 60);
		gdTitle.setPosition(0, 724);
		addActor(gdTitle);
		
		pgh = new PostGridHotItem(page, 490, 650);
		pgh.setFocusAble(true);
		pgh.update("http://images.ott.cibntv.net/2015/04/16/huijiadeluyk1.jpg#350*470", "测试测试");
		pgh.setPosition(0, 0);
		pgh.setName("hotfirst");
		pgh.setNextFocusLeft("MeneListItem0");
		addActor(pgh);
		
		pgh2 = new PostGridHotItem(page, 730, 380);
		pgh2.setFocusAble(true);
		pgh2.update("http://images.ott.cibntv.net/2015/04/16/huijiadeluyk1.jpg#350*470", "测试测试测试测试测试测试测试测试");
		pgh2.setPosition(500, 270);
		addActor(pgh2);
		
		pgh3 = new PostGridHotItem(page, 360, 260);
		pgh3.setFocusAble(true);
		pgh3.update("http://images.ott.cibntv.net/2015/04/16/huijiadeluyk1.jpg#350*470", "测试测试测试测试测试测试测试测试测试测试测试测试");
		pgh3.setPosition(500, 0);
		addActor(pgh3);
		
		pgh4 = new PostGridHotItem(page, 360, 260);
		pgh4.setFocusAble(true);
		pgh4.update("http://images.ott.cibntv.net/2015/04/16/huijiadeluyk1.jpg#350*470", "ceshi1xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		pgh4.setPosition(870, 0);
		addActor(pgh4);
	}

	
	private void setLeftFocus(String actor) {
		pgh.setNextFocusLeft(actor);
	}
}
