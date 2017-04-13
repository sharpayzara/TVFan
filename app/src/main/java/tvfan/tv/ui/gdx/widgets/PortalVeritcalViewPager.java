package tvfan.tv.ui.gdx.widgets;

import android.os.SystemClock;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.SnapshotArray;
import com.luxtone.lib.gdx.IFocusContainer;
import com.luxtone.lib.gdx.OnHasFocusChangeListener;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.utils.ScreenAdapterUtil;

import java.util.ArrayList;

public class PortalVeritcalViewPager extends Group implements
		OnHasFocusChangeListener {
	private ArrayList<Group> mList = new ArrayList<Group>();
	private VeritcalViewPagerListener _veritcalViewPagerListener = null;
	private float mWidth, mHeight;
	private int mCullentSelection = 0;// 当前选中了第几个selection
	private OnSectionChangeListener onSectionChangeListener;
	private float mOffSet = 0;
	private ScollToAction mScollToAction = null;
	float moldOffSet = 0;

	public interface VeritcalViewPagerListener {
		public void focusChanged(Group actor);
	}

	public PortalVeritcalViewPager(Page page) {
		super(page);
	}

	public void setOnSectionChangeListener(
			OnSectionChangeListener onSectionChangeListener) {
		this.onSectionChangeListener = onSectionChangeListener;
	}

	public void setOnPageChanged(
			VeritcalViewPagerListener veritcalViewPagerListener) {
		_veritcalViewPagerListener = veritcalViewPagerListener;
	}

	@Override
	public void act(float delta) {
		if(delta<0){
			delta=0;
		}
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
		a.setName(String.valueOf(this.mList.size()));
		this.mList.add(a);
		a.setOnHasFocusChangeListener(this);
	}

	/**
	 * 将横向的ListView添加到ViewPager中并进行相对定位
	 */
	public void commit() {
		for (int i = 0; i < mList.size(); i++) {
			Group a = mList.get(i);
			//a.setPosition(0, -(1080) * i + mOffSet);
			a.setPosition((1920+1000) * i + mOffSet, 0);
			addActor(a);
		}
	}

	/**
	 * 进行切换不同界面的平移动画
	 * @param offset
	 * @param during
	 */
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

	public void turnPage(int pageNum) {
		if (pageNum > -1 && pageNum < mList.size())
			onHasFocusChanged(mList.get(pageNum), true);
	}

	public void setFocus(int pageNum) {
		// wanqi，解决umeng上报此处可能出现空指针问题,20150813
		if (mList != null && mList.size() > pageNum && mList.get(pageNum) != null) {
			mList.get(pageNum).requestFocus();
			IFocusContainer container = mList.get(pageNum).getFocusContainer();
			if (container != null) {
				Actor a = container.getFocusActor();
				if (a != null)
					a.toFront(); // wanqi,解决焦点凹进去问题.
			}
		}
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
			//mList.get(i).setPosition(0, -(1080) * i + mOffSet);
			mList.get(i).setPosition((1920+1000) * i + mOffSet, 0);
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

	/**
	 * 平移动画相关的类
	 * @author ddd
	 *
	 */
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
	public void onHasFocusChanged(Group arg0, boolean arg1) {
		System.out.println(arg0.toString() + " " + arg1);
		if (arg1) {
			//smoothScollTo(-arg0.getY(), 0.8f);
			smoothScollTo(-arg0.getX(), 1.0f);
			if (_veritcalViewPagerListener != null)
				_veritcalViewPagerListener.focusChanged(arg0);
		}
	}

	@Override
	protected void drawChildren(SpriteBatch batch, float parentAlpha) {
		SnapshotArray<Actor> children = getChildren();
		Actor[] actors2 = children.begin();
		final int count = children.size;
		for (int i = 0; i < count; i++) {
			Actor actor = actors2[i];
			if(actor != null){
				final float x = actor.getRealityX();
				if (x + actor.getWidth() > 0 && x < ScreenAdapterUtil.getWidth()) {
					actor.draw(batch, parentAlpha);
				}
			}
		}
		batch.flush();
		children.end();
	}

	private int getHasFocusId(Group a) {
		for (int i = 0; i < mList.size(); i++) {
			if (a == mList.get(i)) {
				return i;
			}
		}
		return 0;
	}

	public interface OnSectionChangeListener {
		public void onSectionChange(int i);
	}

	long time = 0;

	public void setSection(int i) {
		float during = 0.5f;
		long tmp = System.currentTimeMillis();
		during = ((tmp - time) > 200) ? 0.5f : 0.2f;
		time = tmp;
		if (getChildren() != null && i < getChildren().size
				&& getChildren().get(i) != null) // wanqi，解决友盟上报数组空指针问题
			//smoothScollTo(-getChildren().get(i).getY(), during);
			smoothScollTo(-getChildren().get(i).getX(), 1.0f);

		// smoothScrollTo((i) * 1280 - mOffset, during);
		this.mCullentSelection = i;
		if (onSectionChangeListener != null) {
			onSectionChangeListener.onSectionChange(mCullentSelection);
		}

	}

	float volucity = 0;
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

	@Override
	public boolean notifyTouchDragged(int direction, int downX, int downY,
			int upX, int upY) {
		if (!this.getRect().contains(downX, downY)) {
			return false;
		}
		final int THRESHOLD = 150;
		if (Math.abs(upY - downY) < THRESHOLD) {
			return false;
		}

		int scroll = upY - downY;

		final long timeDuration = SystemClock.uptimeMillis() - downTime;// down事件与up事件的时间间隔
		if (timeDuration != 0) {
			if (scroll < 0) {
				if (mCullentSelection == 0) {
					setSection(0);
				} else {
					setSection(mCullentSelection - 1);
				}
			} else {
				if (mCullentSelection == getChildren().size - 1) {
					setSection(mCullentSelection);
				} else {
					setSection(mCullentSelection + 1);
				}

			}

			// smoothScrollTo(scroll, 0.1f);
		}

		return super.notifyTouchDragged(direction, downX, downY, upX, upY);
	}
}
