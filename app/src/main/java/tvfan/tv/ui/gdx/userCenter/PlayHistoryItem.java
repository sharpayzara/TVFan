package tvfan.tv.ui.gdx.userCenter;

/**
 * 個人中心-播放歷史界面
 * 
 * @author 孫文龍
 * 
 */
import java.util.ArrayList;
import java.util.HashMap;

import tvfan.tv.AppGlobalVars;
import tvfan.tv.BasePage.ACTION_NAME;
import tvfan.tv.dal.PlayRecordHelpler;
import tvfan.tv.dal.models.MPlayRecordInfo;
import tvfan.tv.lib.Lg;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import tvfan.tv.R;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView;
import com.luxtone.lib.widget.AbsListView.OnHasFocusChangedListener;
import com.luxtone.lib.widget.AbsListView.OnItemClickListener;
import com.luxtone.lib.widget.AbsListView.OnItemSelectedChangeListener;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid;
//import com.umeng.analytics.MobclickAgent;

public class PlayHistoryItem extends Group {

	public PlayHistoryItem(Page page, Context context) {
		super(page);
		setSize(1540, 1080);
		this.context = context;
		this.page = (tvfan.tv.ui.gdx.userCenter.Page) page;
		_initView();
	}

	private void _initView() {
		label = new Label(getPage());
		label.setPosition(100, 930);
		label.setTextSize(50);
		label.setTextColor(Color.parseColor("#ffffff"));
		label.setText("播放历史");
		label.setAlpha(0.9f);
		addActor(label);

		// 初始化页码
		pageInfo = new Label(getPage());
		pageInfo.setTextColor(Color.parseColor("#ffffff"));
		pageInfo.setAlpha(0.9f);
		pageInfo.setTextSize(40);
		pageInfo.setSize(200, 60);
		pageInfo.setAlignment(Align.right);
		pageInfo.setPosition(1130, 924);
		addActor(pageInfo);

		bootLabel = new Label(getPage());
		bootLabel.setPosition(350, 934);
		bootLabel.setTextColor(Color.parseColor("#ffffff"));
		bootLabel.setTextSize(32);
		bootLabel.setText("按");
		bootLabel.setAlpha(0.7f);
		addActor(bootLabel);

		bootLabel2 = new Label(getPage());
		bootLabel2.setPosition(400, 934);
		bootLabel2.setTextColor(Color.parseColor("#ffffff"));
		bootLabel2.setTextSize(32);
		bootLabel2.setAlpha(0.7f);
		bootLabel2.setText("菜单");
		addActor(bootLabel2);

		image = new Image(getPage());
		image.setSize(75, 50);
		image.setPosition(395, 926);
		image.setDrawableResource(R.drawable.btn);
		addActor(image);

		bootLabel3 = new Label(getPage());
		bootLabel3.setPosition(480, 934);
		bootLabel3.setTextColor(Color.parseColor("#ffffff"));
		bootLabel3.setTextSize(32);
		bootLabel3.setAlpha(0.7f);
		bootLabel3.setText("键可删除相关播放历史");
		addActor(bootLabel3);

		gridcullGroup = new CullGroup(getPage());
		gridcullGroup.setSize(1330, 890);
		gridcullGroup.setPosition(0, 0);
		gridcullGroup.setCullingArea(new Rectangle(-60, -30, 1330 + 120,
				890 + 70));
		addActor(gridcullGroup);

		hisNo = new Label(getPage());
		hisNo.setPosition(555, 440);
		hisNo.setTextSize(50);
		hisNo.setTextColor(Color.parseColor("#ffffff"));
		hisNo.setText("暂无最近观看");
		hisNo.setAlpha(0.9f);
		hisNo.setVisible(false);
		addActor(hisNo);
	}

	public String _deleteName() {
		return "\"" + allFilmList.get(deletePos).getPlayerName() + "\"";
	}

	public void _updateGrid() {
		if (mPlayRecordOpt == null)
			mPlayRecordOpt = new PlayRecordHelpler(context);
		mPlayRecordOpt
				.deletePlayRecordBy(allFilmList.get(deletePos).getEpgId());
		allFilmList.remove(deletePos);
		_updatePageInfo(0, allFilmList.size(), 4);
		if (allFilmList != null && allFilmList.size() > 0) {
			int pos = deletePos < allFilmList.size() - 1 ? deletePos
					: allFilmList.size() - 1;
			mGrid.notifyDataChanged();
			mGrid.setSelection(pos, true);
		} else {
			_visibleView(true);
			page.setMenuGroupFouce(0);
		}

	}

	public void _initData() {
		if (mPlayRecordOpt == null)
			mPlayRecordOpt = new PlayRecordHelpler(context);
		allFilmList = mPlayRecordOpt.getAllPlayRecord();
		totalnumber = allFilmList.size();
		_updatePageInfo(0, totalnumber, 4);
		Lg.e("mv", "allFilmList"+allFilmList.size());
		if (allFilmList.isEmpty()) {
			// 暂无消费记录
			_visibleView(true);
			return;
		}
		_addGrid();
	}

	private void _visibleView(boolean visible) {
		if (hisNo != null)
			hisNo.setVisible(visible);
		if (mGrid != null)
			mGrid.setVisible(!visible);
	}

	private void _addGrid() {
		if (mGrid == null) {
			mGrid = new Grid(getPage());
			mGrid.setPosition(100, 0);
			mGrid.setSize(1230, 890);
			mGrid.setGapLength(10);
			mGrid.setOrientation(Grid.ORIENTATION_VERTICAL);
			mGrid.setRowNum(4);
			mGrid.setCull(false);
			mGrid.setCullingArea(new Rectangle(-60, 0, 1230 + 100, 890));
			dataAdapter = new PlayHistoryGridAdapter(getPage());
			dataAdapter.setData(allFilmList);
			mGrid.setAdapter(dataAdapter);
			if(backFromDetail){
				mGrid.setSelection(clickPos, true);
				backFromDetail = false;
			}
			mGrid.setAdjustiveScrollLengthForBackward(320);
			mGrid.setAdjustiveScrollLengthForForward(320);
			mGrid.setItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(Actor itemView, int position,
						AbsListView grid) {
					Lg.e("PlayHistoryItem", "clickPos====="+clickPos);
					clickPos = position;
					backFromDetail = true;
					
					MPlayRecordInfo mPlayRecordInfo = allFilmList.get(position);
					Bundle options = new Bundle();
					
					options.putString("id", allFilmList.get(position)
							.getEpgId());
					page.doAction(ACTION_NAME.OPEN_DETAIL, options);
					
					
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("UID", AppGlobalVars.getIns().USER_ID);
					map.put("PROGRAM_ID", mPlayRecordInfo.getEpgId());
					map.put("WAY_NAME", "历史-"+ mPlayRecordInfo.getPlayerName());
					map.put("U_I_N",
							AppGlobalVars.getIns().USER_ID + "|"
									+  mPlayRecordInfo.getEpgId() + "|"
									+ mPlayRecordInfo.getPlayerName());
//					MobclickAgent.onEvent(page.getActivity().getApplicationContext(),
//							"event_detail", map);
					Lg.i("TAG", "历史："+ mPlayRecordInfo.getPlayerName());
				}
			});
			
			mGrid.setOnItemSelectedChangeListener(new OnItemSelectedChangeListener() {

				@Override
				public void onSelectedChanged(int position, Actor actor) {
					// TODO Auto-generated method stub
					deletePos = position;
					_updatePageInfo(position, totalnumber, 4);
				}
			});
			gridcullGroup.addActor(mGrid);
			_visibleView(false);
		} else {
			_visibleView(false);
			dataAdapter.setData(allFilmList);
			mGrid.setAdapter(dataAdapter);
			if(backFromDetail){
				mGrid.setSelection(clickPos, true);
				backFromDetail = false;
			}
		}
	}

	@Override
	public void onResume() {
		image.setDrawableResource(R.drawable.btn);
		label.setText("最近观看");
		bootLabel.setText("按");
		bootLabel2.setText("菜单");
		bootLabel3.setText("键可删除收藏影片");
		pageInfo.setText(pagenow + "/" + totalpage);
		hisNo.setText("暂无最近观看");
		super.onResume();
	}

	/**
	 * 更新页码显示
	 * @param pos
	 * @param total
	 * @param pagenums
	 */
	public void _updatePageInfo(int pos, int total, int pagenums) {
		pagenow = pos/pagenums+1;
		totalpage = total/pagenums;		
		if (pagenums >= total) {
			totalpage = 1;
		} else {
			if (total%pagenums>0) {
				totalpage = totalpage + 1;
			}
		}
		pageInfo.setText(pagenow+"/"+totalpage);
	}

	private Label label, hisNo, pageInfo, bootLabel, bootLabel2, bootLabel3;
	private Image image;
	private PlayHistoryGridAdapter dataAdapter;
	private Grid mGrid;
	Context context;
	private ArrayList<MPlayRecordInfo> allFilmList;
	private int totalnumber = 0;
	private int deletePos = -1;
	private int pagenow = 0;
	private int totalpage = 0;
	private CullGroup gridcullGroup;
	private PlayRecordHelpler mPlayRecordOpt;
	private tvfan.tv.ui.gdx.userCenter.Page page;
	
	private int clickPos = -1;//最近观看列表点击的position
	private boolean backFromDetail = false;//mGid是否已经点击了
}
