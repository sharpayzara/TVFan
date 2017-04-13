package tvfan.tv.ui.gdx.programDetail.group;

import org.json.JSONArray;
import org.json.JSONObject;

import tvfan.tv.BasePage;
import tvfan.tv.BasePage.ACTION_NAME;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.ui.gdx.programDetail.adapter.DetailRelatedRecommendAdapter;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;
import android.os.Bundle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

public class PersonGroup extends Group {

	private TVFANLabel mTitle;
	private Page page;
	private Grid grid;
	private DetailRelatedRecommendAdapter adapter;
	private CullGroup cullGroup;
	private RemoteData rd;
	
	JSONArray programList;
	private String seriesId;
	private String movieName;
	private JSONObject personObj;

	private static final String TAG = "PersonGroup";

	public PersonGroup(Page page,String seriesId,String movieName,JSONObject personObj) {
		super(page);
		setSize(1770, 600);
		this.page = page;
		this.personObj = personObj;
		this.seriesId = seriesId;
		this.movieName = movieName;
		rd = new RemoteData(page.getActivity());
		initView();
	}

	private void initView() {

		cullGroup = new CullGroup(page);
		cullGroup.setPosition(120, 0);
		cullGroup.setSize(1770, 600);
		cullGroup.setCullingArea(new Rectangle(-330, -60, 2250, 600));
		addActor(cullGroup);

		mTitle = new TVFANLabel(page);
		mTitle.setPosition(0, 480);
		mTitle.setSize(100, 30);
		mTitle.setFocusAble(false);
		mTitle.setAlignment(Align.left);
//		mTitle.setAlpha(0.6f);
		mTitle.setTextSize(50);
		mTitle.setText("相关人物");
		mTitle.setColor(Color.valueOf("f0f0f0"));
		cullGroup.addActor(mTitle);

		try {
			programList = personObj.getJSONArray("personList");
			addGrid(programList);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		/*
		rd.getDetailRelatedPerson(seriesId,movieName,new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if(page.isDisposed()){
					return ;
				}
				if (response == null) {
					Lg.e(TAG,
							"detail page get detail related person response is null");
					return;
				}
				try {
					programList = response.getJSONArray("personList");
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
		});*/

	}

	private void addGrid(JSONArray personList) {
		grid = new Grid(page);
		grid.setPosition(0, 0);
		grid.setSize(1560, 470);
		grid.setRowNum(1);
		grid.setGapLength(10);
		grid.setCull(false);
		grid.setOrientation(Grid.ORIENTATION_LANDSPACE);
		adapter = new DetailRelatedRecommendAdapter(page);
		adapter.setData(personList);
		grid.setAdapter(adapter);
		grid.setItemClickListener(itemClickListener);
		cullGroup.addActor(grid);

		grid.setName("grid");
		grid.setNextFocusRight("grid");
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(Actor actor, int position, AbsListView grid) {
			try {
				Bundle bundle = new Bundle();
				JSONObject obj = (JSONObject) programList.get(position);
				bundle.putString("personId", obj.optString("id"));
				bundle.putString("personName", obj.optString("name"));
				bundle.putString("seriesId",seriesId);
				((BasePage)page).doAction(ACTION_NAME.OPEN_PERFORMER, bundle);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public Grid getGrid() {
		return grid;
	}

	public CullGroup getCullGroup() {
		return cullGroup;
	}

}
