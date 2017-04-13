package tvfan.tv.ui.gdx.programDetail.dialog;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage;
import tvfan.tv.BasePage.ACTION_NAME;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.lib.Lg;
import tvfan.tv.ui.gdx.programDetail.adapter.DetailTagAdapter;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;
import android.os.Bundle;
import tvfan.tv.R;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.gdx.Dialog;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.Grid;
//import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent;

public class DetailTagDialog extends Dialog {

	private Page mpage;

	private RemoteData rd;

	private static final String TAG = "DetailTagDialog";
	
	private JSONArray programList;
	
	private String tag;
	private String tagParam;

	public DetailTagDialog(Page page,String tagStr,String abbr) {
		super(page);

		this.mpage = page;
		this.tag = tagStr;
		this.tagParam = abbr;
		rd = new RemoteData(page.getActivity());

		mBackImage = new Image(page);
		mBackImage.setSize(AppGlobalConsts.APP_WIDTH,
				AppGlobalConsts.APP_HEIGHT);
		mBackImage.setDrawable(new TextureRegionDrawable(page
				.findTextureRegion(R.drawable.default_background)));
		addActor(mBackImage);

		mTag = new TVFANLabel(page);
		mTag.setPosition((AppGlobalConsts.APP_WIDTH - 1540) * 0.5f, 930);
		mTag.setColor(Color.valueOf("f0f0f0"));
		mTag.setAlignment(Align.center);
		mTag.setTextSize(50);
		mTag.setText("标签\""+tag+"\"");
		addActor(mTag);

		mPageNum = new Label(page, false);
		mPageNum.setPosition(1700, 900);
		mPageNum.setColor(Color.valueOf("f0f0f0"));
		mPageNum.setAlignment(Align.right);
		mPageNum.setText(String.format(pageNum, "0", "0"));
		mPageNum.setTextSize(50);
		addActor(mPageNum);
		
		
		rd.getSearchProgram(tagParam,new Listener4JSONObject() {
			
			@Override
			public void onResponse(JSONObject response) {
				if(mpage.isDisposed()){
					return ;
				}
				if (response == null) {
					Lg.e(TAG,
							"detail page search related tag response is null");
					return;
				}
				try {
					programList = response.getJSONArray("programList");
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							addGrid(programList);
						}
					});

				} catch (Exception e) {
					Lg.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String errorMessage) {
				
			}
		});

	}
	
	private void addGrid(final JSONArray programList){
		mGrid = new Grid(mpage);
		mGrid.setSize(1540, 760f);
		mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
		mGrid.setRowNum(5);
		mGrid.setGapLength(10);
		mGrid.setCull(false);
		mGrid.setPosition((AppGlobalConsts.APP_WIDTH - 1540) * 0.5f, 120f);
		DetailTagAdapter adapter = new DetailTagAdapter(mpage);
		adapter.setData(programList);
		mGrid.setAdapter(adapter);
		mPageNum.setText(String.format(pageNum, 1, programList.length()));
		mGrid.setItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(Actor itemView, int position,
					AbsListView grid) {
				try {
					Bundle bundle = new Bundle();
					JSONObject obj = (JSONObject) programList.get(position);
					bundle.putString("programSeriesId", obj.optString("id"));
					((BasePage)mpage).doAction(ACTION_NAME.OPEN_DETAIL, bundle);
					
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("UID", AppGlobalVars.getIns().USER_ID);
					map.put("PROGRAM_ID",  obj.optString("id"));
					map.put("WAY_NAME", "标签-"+ obj.optString("name"));
					map.put("U_I_N",
							AppGlobalVars.getIns().USER_ID + "|"
									+  obj.optString("id") + "|"
									+ obj.optString("name"));
					MobclickAgent.onEvent(mpage.getActivity().getApplicationContext(),
							"event_detail", map);
					Lg.i("TAG", "标签："+ obj.optString("name"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

			@Override
			public void onSelectedChanged(int position, Actor actor) {

				int i = position/10+1;
				mPageNum.setText(String.format(pageNum, i, programList.length()%10==0?programList.length()/10:programList.length()/10+1));
			}
		});
		addActor(mGrid);
	}

	private Label mTag;
	private Label mPageNum;
	private Image mBackImage;
	private Grid mGrid;

	private String pageNum = "%s/%s";

}
