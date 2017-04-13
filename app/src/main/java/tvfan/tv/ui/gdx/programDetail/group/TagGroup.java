package tvfan.tv.ui.gdx.programDetail.group;

import org.json.JSONArray;
import org.json.JSONObject;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.dal.RemoteData;
import tvfan.tv.lib.Lg;
import tvfan.tv.ui.gdx.programDetail.dialog.DetailTagDialog;
import tvfan.tv.ui.gdx.widgets.Button;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.CullGroup;

public class TagGroup extends Group implements OnClickListener {

	private TVFANLabel mTitle;
	private Page page;
	private CullGroup cullGroup;

	private Button[] btns;

	private static final int COLUMN = 5;

	private RemoteData rd;

	private static final String TAG = "TagGroup";

	private JSONArray tagList;
	private JSONObject tagObj;
	private String seriesId;
	private String movieName;

	public TagGroup(Page page,String seriesId,String movieName,JSONObject tagObj) {
		super(page);
		this.page = page;
		this.tagObj = tagObj;
		this.seriesId = seriesId;
		this.movieName = movieName;
		setSize(1770, 600);
		initView();
	}

	private void initView() {

		cullGroup = new CullGroup(page);
		cullGroup.setPosition(120, 0);
		cullGroup.setSize(1770, 600);
		cullGroup.setCullingArea(new Rectangle(-40, 0, 1630, 600));
		addActor(cullGroup);

		mTitle = new TVFANLabel(page);
		mTitle.setPosition(0, 480);
		mTitle.setSize(100, 30);
		mTitle.setFocusAble(false);
		mTitle.setAlignment(Align.left);
//		mTitle.setAlpha(0.6f);
		mTitle.setTextSize(50);
		mTitle.setText("相关标签");
		mTitle.setColor(Color.valueOf("f0f0f0"));
		cullGroup.addActor(mTitle);

		rd = new RemoteData(page.getActivity());
		
		try {
			tagList = tagObj.getJSONArray("data");
			addTags(tagList);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		/*rd.getRelatedLabelList(seriesId,movieName,new Listener4JSONObject() {

			@Override
			public void onResponse(JSONObject response) {
				if(page.isDisposed()){
					return ;
				}
				if (response == null) {
					Lg.e(TAG, "detail page get related tag response is null");
					return;
				}
				try {
					tagList = response.getJSONArray("data");
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							addTags(tagList);
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
*/
	}

	private void addTags(JSONArray tags) {
		try {
			// btns = new Button[tags.length()];
			int t = tags.length();
			if (t > 10) {
				t = 10;
			}
//			t = 10;
			btns = new Button[t];
			int row = t % COLUMN == 0 ? t / COLUMN : t / COLUMN + 1;

			for (int i = 0; i < row; i++) {
				for (int j = 0; j < COLUMN; j++) {
					if (i * COLUMN + j < t) {
						btns[i * COLUMN + j] = new Button(page, 360, 150);
						btns[i * COLUMN + j].setName(i + "r" + j + "c");// 几行几列的标志，
						if (i > 0) {
							btns[i * COLUMN + j].setNextFocusUp(i - 1 + "r" + j
									+ "c");
						}
						if (j > 0) {
							btns[i * COLUMN + j].setNextFocusLeft(i + "r"
									+ (j - 1) + "c");
						}
						if (i < COLUMN - 1) {
							btns[i * COLUMN + j].setNextFocusDown(i + 1 + "r"
									+ j + "c");
						}
						if (j < COLUMN - 1) {
							btns[i * COLUMN + j].setNextFocusRight(i + "r"
									+ (j + 1) + "c");
						}
						btns[i * COLUMN + j].setPosition(305 * j - 25,
								330 - 98 * i);
						 btns[i * COLUMN + j].getLabel().setText(
						 tags.getJSONObject(i * COLUMN + j)
						 .optString("name").toString());
						//btns[i * COLUMN + j].getLabel().setText("后宫宫廷");
						btns[i * COLUMN + j].getLabel().setTextSize(40);
//						btns[i * COLUMN + j].getLabel().setAlpha(0.6f);
						btns[i * COLUMN + j].getLabel().setTag( tags.getJSONObject(i * COLUMN + j)
						 .optString("id").toString());
						btns[i * COLUMN + j].setFocusAble(true);
						btns[i * COLUMN + j]
								.setButtonFocusScale(AppGlobalConsts.FOCUSSCALE);
						btns[i * COLUMN + j].setOnClickListener(this);
						cullGroup.addActor(btns[i * COLUMN + j]);
					}
				}
			}
		} catch (Exception e) {
			Lg.e(TAG, e.getMessage().toString());
			e.printStackTrace();
		}
	}

	public CullGroup getCullGroup() {
		return cullGroup;
	}

	@Override
	public void onClick(Actor actor) {
		if(actor instanceof Button){
		new DetailTagDialog(page,((Button)actor).getLabel().getText().toString(), ((Button)actor).getLabel().getTag().toString()).show();
		}
	}

	public int getBtnsCount() {
		return btns.length;
	}
	
	

}
