package tvfan.tv.ui.gdx.programDetail.dialog;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.ui.gdx.programDetail.item.ScrollitemView;
import tvfan.tv.ui.gdx.widgets.VerticalScrollView;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.CullGroup;

public class PersonScrollView extends VerticalScrollView {
	private CullGroup cullGroup;
	public PersonScrollView(Page page) {
		super(page);
		setSize(1239, AppGlobalConsts.APP_HEIGHT);
//		cullGroup = new CullGroup(getPage());
//		cullGroup.setSize(826, 720);
//		cullGroup.setCullingArea(new Rectangle(0, 0, 826, 720));
//		addActor(cullGroup);
		
		for (int i = 0; i < 4; i++) {
			Actor a = getActor(1);
			a.setPosition(0, 600-i*450);
			addActor(a);
		}
	}

	
	private Actor getActor(int i) {
		ScrollitemView mScrollitemView = new ScrollitemView(getPage());
		mScrollitemView.setFocusAble(true);
		mScrollitemView.setFocusScale(AppGlobalConsts.FOCUSSCALE);
		mScrollitemView.setSize(1239, 450);
		return mScrollitemView;
	}}
