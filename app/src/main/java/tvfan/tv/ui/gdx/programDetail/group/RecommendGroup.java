package tvfan.tv.ui.gdx.programDetail.group;

import org.json.JSONArray;
import org.json.JSONObject;

import tvfan.tv.BasePage;
import tvfan.tv.BasePage.ACTION_NAME;
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

public class RecommendGroup extends Group {

	private TVFANLabel mTitle;
	private Page page;
	private Grid grid;
	private DetailRelatedRecommendAdapter adapter;
	private CullGroup cullGroup;

	private static final String TAG = "RecommendGroup";
	JSONArray programList;
	JSONObject recommendObj;

	public RecommendGroup(Page page, JSONObject recommendObj) {
		super(page);
		setSize(1920.0f, 435.0f);
		this.page = page;
		this.recommendObj = recommendObj;
		initView();
	}

	private void initView() {

		mTitle = new TVFANLabel(page);
		mTitle.setPosition(80.0f, 367.0f);
		mTitle.setSize(150.0f, 30.0f);
		mTitle.setFocusAble(false);
		mTitle.setAlignment(Align.left);
		mTitle.setColor(Color.valueOf("ffffff"));
		mTitle.setTextSize(30);
		mTitle.setText("相关推荐");
		addActor(mTitle);

		cullGroup = new CullGroup(page);
		cullGroup.setPosition(80.0f, 60.0f);
		cullGroup.setSize(1920.0f, 410.0f);
		cullGroup.setCullingArea(new Rectangle(-165.0f, -60.0f, 1920.0f, 410.0f));
		addActor(cullGroup);

		try {
			programList = recommendObj.getJSONArray("programList");
			addGrid(programList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addGrid(final JSONArray programList) {
		grid = new Grid(page);
		grid.setPosition(0, 0);
		grid.setSize(1760, 285);
		grid.setRowNum(1);
		grid.setGapLength(15);
		grid.setCull(false);
		grid.setOrientation(Grid.ORIENTATION_LANDSPACE);
		grid.setName("grid");
		grid.setNextFocusRight("grid");
		adapter = new DetailRelatedRecommendAdapter(page);
		adapter.setData(programList);
		grid.setAdapter(adapter);
		grid.setItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(Actor actor, int position, AbsListView group) {
				try {
					Bundle bundle = new Bundle();
					JSONObject obj = (JSONObject) programList.get(position);
					bundle.putString("programSeriesId", obj.optString("id"));
					((BasePage)page).doAction(ACTION_NAME.OPEN_DETAIL, bundle);
					page.freeMemory();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		cullGroup.addActor(grid);
	}

	public Grid getGrid() {
		return grid;
	}

	public CullGroup getCullGroup() {
		return cullGroup;
	}
}
