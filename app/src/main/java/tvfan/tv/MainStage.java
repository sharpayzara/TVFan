package tvfan.tv;

import android.os.Bundle;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.OnFocusChangeListener;

public class MainStage extends BasePage implements OnClickListener, OnFocusChangeListener {

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		Bundle options = new Bundle();
		options.putString("catalogId", "10");
		options.putString("contentId", "7979");		
		doAction(ACTION_NAME.OPEN_PORTAL, options);
		//startGdxPage(tvfan.tv.ui.gdx.programList.Page.class, options);
		//startGdxPage(tvfan.tv.ui.gdx.programDetail.Page.class);
//		startGdxPage(tvfan.tv.ui.gdx.brand.Page.class);
//		startGdxPage(tvfan.tv.ui.gdx.programList.Page.class);
//		startGdxPage(tvfan.tv.ui.andr.news.Page.class);
//		startAndrPage(tvfan.tv.ui.andr.cibnplay.play.Page.class, bundle, getActivity());
//		startAndrPage(tvfan.tv.ui.andr.cibnplay.ykewplay.Page.class, bundle, getActivity());
//		startAndrPage(tvfan.tv.ui.andr.cibnplay.liveshowplay.Page.class, bundle, getActivity());
//		startAndrPage(tvfan.tv.ui.andr.cibnplay.p2p.Page.class, bundle, getActivity());
//		startAndrPage(tvfan.tv.ui.andr.cibnplay.liveplay.Page.class, bundle, getActivity());

	}

	@Override
	public void onFocusChanged(Actor arg0, boolean arg1) {
	}

	@Override
	public void onClick(Actor arg0) {
	}

}
