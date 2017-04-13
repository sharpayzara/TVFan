package tvfan.tv.ui.gdx.widgets;

import java.util.ArrayList;

import tvfan.tv.ui.gdx.programDetail.group.DetailGroup;
import tvfan.tv.ui.gdx.programDetail.group.PersonGroup;
import tvfan.tv.ui.gdx.programDetail.group.RecommendGroup;
import tvfan.tv.ui.gdx.programDetail.group.TagGroup;
import android.os.SystemClock;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.SnapshotArray;
import com.luxtone.lib.gdx.OnHasFocusChangeListener;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.utils.ScreenAdapterUtil;

public class DetailVeritcalViewPager extends Group implements
		OnHasFocusChangeListener {
	private ArrayList<Group> mList = new ArrayList<Group>();
	private float mWidth;
	private float mHeight;

	private float mOffSet = 0;// 游标
	private ScollToAction mScollToAction = null;
	float moldOffSet = 0;
	
	private boolean isFirst;

	private int mCullentSelection = 0;// 当前选中了第几个selection

	public DetailVeritcalViewPager(Page page) {
		super(page);
		
		isFirst = false;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (mScollToAction != null) {
			mScollToAction.act(delta);
		}
	}

	@Override
	public void setSize(float width, float height) {
		this.mWidth = width;
		this.mHeight = height;
		super.setSize(width, height);
	}

	public void addSection(Group a) {
		this.mList.add(a);
//		a.setOnHasFocusChangeListener(this);
	}

	public void setmList(ArrayList<Group> mList) {
		this.mList.clear();
		this.mList.addAll(mList);
		setFousChange();
	}

	/**
	 * 添加几个栏目的焦点改变的监听
	 */
	public void setFousChange(){
		for(int i=0; i<mList.size(); i++){
			mList.get(i).setOnHasFocusChangeListener(this);
		}
	}

	public void setChildFocusChange(Group group) {
		if(group instanceof RecommendGroup)
			((RecommendGroup)group).setOnHasFocusChangeListener(this);
	}

	public void commit() {
		for (int i = 0; i < mList.size(); i++) {
			Group a = mList.get(i);
			if (i == 0) {
				a.setPosition(0, -(550) * i + mOffSet + 350);
			} else if (i == 1) {
				a.setPosition(0, -(550) * i + mOffSet + 300);
			}
			addActor(a);
		}
		mOffSet = 350;
	}

	public void smoothScollTo(float offset, float during) {
		final float start = mOffSet;
		final float end = mOffSet + offset;

		if (start == end) {
			return;
		}
		ScollToAction action = new ScollToAction(start, end);
		if (during < 0) {
			during = 0.2f;
		}
		action.setDuration(during);
		action.setInterpolation(Interpolation.pow4Out);
		setScrollAction(action);

	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		layoutAllGroup(mOffSet);
		super.draw(batch, parentAlpha);
	}

	private void layoutAllGroup(float offSet) {
		if (moldOffSet == mOffSet) {
			return;
		}

		for (int i = 0; i < mList.size(); i++) {
			mList.get(i).setPosition(0, -(550) * i + mOffSet);
		}
		moldOffSet = mOffSet;
	}

	private void setScrollAction(ScollToAction action) {
		this.mScollToAction = action;

	}

	public void stopScolling() {
		if (mScollToAction != null) {
			mScollToAction.finish();
		}
	}

	private final class ScollToAction extends TemporalAction {
		private final float start;
		private final float end;

		public ScollToAction(float start, float end) {
			super();
			this.start = start;
			this.end = end;
		}

		@Override
		protected void update(float arg0) {
			mOffSet = start + (end - start) * arg0;// 修改游标
		}
	}

	@Override
	public void onHasFocusChanged(Group group, boolean hasFocus) {
		System.out.println(group.toString() + " " + hasFocus);

		if (hasFocus) {
			if (group instanceof DetailGroup) {
				isFirst = true;
				smoothScollTo(-group.getY() + 350, 0.8f);
			} else if (group instanceof RecommendGroup) {
				smoothScollTo(-group.getY() + 150, 0.8f);
			}
		}
	}

	@Override
	protected void drawChildren(SpriteBatch batch, float parentAlpha) {
		SnapshotArray<Actor> children = getChildren();
		Actor[] actors2 = children.begin();
		final int count = children.size;
		for (int i = 0; i < count; i++) {
			Actor actor = actors2[i];
			final float x = actor.getRealityX();
			if (x + actor.getWidth() > 0 && x < ScreenAdapterUtil.getWidth()) {
				actor.draw(batch, parentAlpha);
			}
		}
		batch.flush();
		children.end();
	}

	int downY = 0;
	int downX = 0;
	int lastY = 0;
	int lastX = 0;
	int dragY = 0;
	int dragX = 0;
	long downTime = 0;

	@Override
	public boolean dispatchTouchEventDown(int x, int y) {
		stopScolling();

		downY = y;
		downX = x;
		lastY = y;
		lastX = x;
		dragX = x;
		dragY = y;
		downTime = SystemClock.uptimeMillis();

		return super.dispatchTouchEventDown(x, y);
	}

}
