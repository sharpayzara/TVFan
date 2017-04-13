package tvfan.tv.ui.gdx.programDetail.group;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tvfan.tv.dal.RemoteData;
import tvfan.tv.dal.HttpResponse.Listener4JSONObject;
import tvfan.tv.lib.Lg;
import tvfan.tv.ui.gdx.programDetail.adapter.StagePhotoAdapter;
import tvfan.tv.ui.gdx.programDetail.dialog.StagePhotoDialog;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;

public class StagePhotoGroup extends Group {

	private TVFANLabel mTitle;
	private Page page;
	private Grid grid;
	private StagePhotoAdapter adapter;
	private CullGroup cullGroup;

	private static final String TAG = "StagePhotoGroup";
	private JSONArray data;
	private RemoteData rd;

	public StagePhotoGroup(Page page) {
		super(page);
		setSize(1590, 600);
		this.page = page;
		rd = new RemoteData(page.getActivity());
		initView();
	}

	private void initView() {

		cullGroup = new CullGroup(page);
		cullGroup.setPosition(330, 0);
		cullGroup.setSize(1590, 600);
		cullGroup.setCullingArea(new Rectangle(-330, 0, 2250, 600));
		addActor(cullGroup);

		mTitle = new TVFANLabel(page);
		mTitle.setPosition(0, 480);
		mTitle.setSize(100, 30);
		mTitle.setFocusAble(false);
		mTitle.setAlignment(Align.left);
		mTitle.setAlpha(0.6f);
		mTitle.setTextSize(50);
		mTitle.setText("相关剧照");
		cullGroup.addActor(mTitle);

		rd.getRelatedPicture(new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if (response == null) {
					Lg.e(TAG,
							"detail page get stagePhotoGroup response is null");
					return;
				}
				try {
					data = response.getJSONArray("data");
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							addGrid(data);
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

	private void addGrid(JSONArray data) {
		if (data.length() > 0) {

			try {
				JSONArray array = ((JSONObject) data.get(0))
						.optJSONArray("image");

				grid = new Grid(page);
				grid.setPosition(0, 0);
				grid.setSize(1560, 470);
				grid.setRowNum(1);
				grid.setCull(false);
				grid.setGapLength(0);
				grid.setOrientation(Grid.ORIENTATION_LANDSPACE);
				adapter = new StagePhotoAdapter(page);

				adapter.setData(array);
				grid.setAdapter(adapter);
				cullGroup.addActor(grid);
				grid.setItemClickListener(itemClickListener);
				grid.setName("grid");
				grid.setNextFocusRight("grid");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public Grid getGrid() {
		return grid;
	}

	public CullGroup getCullGroup() {
		return cullGroup;
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(Actor actor, int position, AbsListView grid) {
			StagePhotoDialog spd;
			try {
				JSONObject obj = (JSONObject) data.get(0);
				spd = new StagePhotoDialog(page, obj.getJSONArray("image"),
						position);
				spd.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

}
