package tvfan.tv.ui.gdx.ranking;
/**
 * 排名
 * 
 * @author sunwenlong
 * 
 */
import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.SnapshotArray;
import com.luxtone.lib.gdx.FocusFinder;
import com.luxtone.lib.gdx.OnHasFocusChangeListener;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.utils.ScreenAdapterUtil;

public class RankingVeritcalViewPager extends Group implements
		OnHasFocusChangeListener {
	private ArrayList<Group> mList = new ArrayList<Group>();
	private float mWidth;
	private float mHeight;
	private Actor downFocus;
	private float mOffSet = 0;// 游标
	private ScollToAction mScollToAction = null;
	private  boolean firstFocus = false;
	float moldOffSet = 0;

	public RankingVeritcalViewPager(Page page) {
		super(page);
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
		super.setSize(mWidth, mHeight);
	}

	public void addSection(Group a) {
		this.mList.add(a);
		a.setOnHasFocusChangeListener(this);
	}

	public void commit() {
		for (int i = 0; i < mList.size(); i++) {
			Group a = mList.get(i);
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
			mList.get(i).setPosition(0, -(530) * i + mOffSet);
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

		if (hasFocus&&firstFocus) {
			downFocus = FocusFinder.findNextActorInGroup(group,
					FocusFinder.DOWN, RankingVeritcalViewPager.this);
			if (downFocus == null)
				smoothScollTo(-group.getY(), 0.8f);
			else
				smoothScollTo(-group.getY() + 350, 0.8f);
		}
		firstFocus = true;
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
}
