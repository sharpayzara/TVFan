package tvfan.tv.ui.gdx.liveShow.detail;

import org.json.JSONArray;
import org.json.JSONObject;

import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.lib.Lg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView.ScrollStatusListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

public class RecommendGroup extends Group {

	private Label mTitle;
	private Page page;
	private Grid grid;
	private DetailRelatedRecommendAdapter adapter;
	private CullGroup cullGroup;

	private RemoteData rd;

	private static final String TAG = "RecommendGroup";
	JSONArray programList;
	String mProgramSeriesId;
	public RecommendGroup(Page page,String programSeriesId) {
		super(page);
		setSize(1630, 600);
		this.page = page;
		mProgramSeriesId = programSeriesId;
		rd = new RemoteData(page.getActivity());
		initView();
	}

	private void initView() {

		cullGroup = new CullGroup(page);
		cullGroup.setPosition(0, 0);
		cullGroup.setSize(1630, 600);
		cullGroup.setCullingArea(new Rectangle(-70, -60, 1770, 600));
		//cullGroup.setCullingArea(new Rectangle(0, 0, 1630, 300));
		addActor(cullGroup);

		mTitle = new Label(page);
		mTitle.setPosition(0, 480);
		mTitle.setSize(100, 30);
		mTitle.setFocusAble(false);
		mTitle.setAlignment(Align.left);
		mTitle.setAlpha(0.6f);
		mTitle.setTextSize(50);
		mTitle.setText("相关推荐");
		cullGroup.addActor(mTitle);

		rd.getDetailRelatedRecommend(mProgramSeriesId,"",new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if (response == null) {
					Lg.e(TAG,
							"detail page get detail related recommend response is null");
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

	private void addGrid(JSONArray programList) {
		grid = new Grid(page);
		grid.setPosition(0, 150);
		grid.setSize(1630, 300);
		grid.setRowNum(1);
		grid.setGapLength(15);
		grid.setCull(false);
		grid.setOrientation(Grid.ORIENTATION_LANDSPACE);
		grid.setName("grid");
		grid.setNextFocusRight("grid");
		adapter = new DetailRelatedRecommendAdapter(page);
		adapter.setData(programList);
		grid.setAdapter(adapter);
		
		grid.setCull(false);
		grid.setScrollStatusListener(new ScrollStatusListener() {
			
			@Override
			public void onScrolling(float arg0, float arg1) {
				
			}
			
			@Override
			public void onScrollStop(float arg0, float arg1) {
				grid.setCull(false);
			}
			
			@Override
			public void onScrollStart(float arg0, float arg1) {
				grid.setCull(true);
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
