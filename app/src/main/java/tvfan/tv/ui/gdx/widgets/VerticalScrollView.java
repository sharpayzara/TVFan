package tvfan.tv.ui.gdx.widgets;

import java.util.ArrayList;
import java.util.List;

import tvfan.tv.AppGlobalConsts;
import android.os.SystemClock;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.SnapshotArray;
import com.luxtone.lib.gdx.FocusFinder;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.resource.Sound;
import com.luxtone.lib.utils.ScreenAdapterUtil;
import com.luxtone.lib.widget.CullGroup;

public class VerticalScrollView extends CullGroup {
	private float mOffset = 0.0F;
	private float mOldOffset = 0.0F;
	private List<Actor> mList = new ArrayList();
	private ScrollToAction mScrollAction = null;
	private float mHorPadding = 70.0F;
	private Actor mFocus = null;
	private float mCanvasLength = 0.0F;

	public VerticalScrollView(Page page) {
		super(page);
	}

	public void configHorPadding(float mHorpadding) {
		this.mHorPadding = mHorpadding;
	}

	public void setCullAble(boolean cull) {
		if (cull) {
			setCullingArea(new Rectangle(0.0F, 0.0F, getWidth(), getHeight()));
		}
	}

	public void act(float delta) {
		super.act(delta);
		if (this.mScrollAction != null) {
			this.mScrollAction.act(delta);
		}
	}

	public void draw(SpriteBatch batch, float parentAlpha) {
		layoutAllActorPostion(this.mOffset);
		super.draw(batch, parentAlpha);
	}

	private void layoutAllActorPostion(float mOffset) {
		if (this.mOldOffset == mOffset) {
			return;
		}
		for (int i = 0; i < this.mList.size(); i++) {
			((Actor) this.mList.get(i)).setPosition(
					((Actor) this.mList.get(i)).getX(),
					((Actor) this.mList.get(i)).getY() + this.mOldOffset
							- mOffset);
		}
		this.mOldOffset = mOffset;
	}

	private final class ScrollToAction extends TemporalAction {
		private final float start;

		private final float end;

		public ScrollToAction(float start, float end) {
			this.start = start;
			this.end = end;
		}

		protected void update(float percent) {
			VerticalScrollView.this.mOffset = (this.start + (this.end - this.start)
					* percent);
		}

		protected void end() {
		}
	}

	private boolean getIsScrollTo(float offset, float duration) {
		float start = this.mOffset;
		float end = clampOffset(this.mOffset + offset);

		if (start == end) {
			return false;
		}
		return true;
	}

	public void smoothScrollTo(float offset, float duration) {
		float start = this.mOffset;
		float end = clampOffset(this.mOffset + offset);

		if (start == end) {
			return;
		}

		ScrollToAction action = new ScrollToAction(start, end);

		if (duration < 0.0F) {
			float scrollLength = Math.abs(end - start);
			float rate = 0.001F;

			duration = scrollLength * rate > 0.4F ? 0.4F : scrollLength * rate;
			duration = duration < 0.2F ? 0.2F : duration;
		}

		action.setDuration(duration);

		action.setInterpolation(Interpolation.pow5Out);

		setScrollAction(action);
	}

	private float clampOffset(float mOffset) {
		if (mOffset < 0.0F) {
			return 0.0F;
		}

		if (mOffset > this.mCanvasLength - getHeight() + this.mHorPadding) {
			return this.mCanvasLength - getHeight() + this.mHorPadding;
		}
		return mOffset;
	}

	private void setScrollAction(ScrollToAction action) {
		setScrollAction(action, true);
	}

	private void setScrollAction(TemporalAction action, boolean cancalCurrent) {
		if (cancalCurrent)
			stopScrolling();
		this.mScrollAction = ((ScrollToAction) action);
	}

	public void stopScrolling() {
		if (this.mScrollAction != null) {
			this.mScrollAction.finish();
		}
	}

	public boolean onKey(int keycode) {
		boolean handled = dispatchKeyEvent(keycode);

		if (handled) {
			return handled;
		}
		return super.onKey(keycode);
	}

	long lastDownTime = 0L;
	int lastKeyCode = -1;
	static final int THRESHOLD_TIME = 200;
	int downDuration = 0;

	private boolean dispatchKeyEvent(int keyCode) {
		boolean handle = false;
		long downTime = SystemClock.uptimeMillis();
		if ((keyCode != this.lastKeyCode) || (this.lastDownTime == 0L)) {
			this.downDuration = 0;
			handle = onSingleTap(keyCode);
		} else {
			long deltaTime = downTime - this.lastDownTime;

			if (deltaTime < 200L) {
				this.downDuration = ((int) (this.downDuration + deltaTime));
				handle = onLongKey(keyCode, deltaTime <= 0L ? 50L : deltaTime,
						this.downDuration);
			} else {
				this.downDuration = 0;
				handle = onSingleTap(keyCode);
			}
		}
		this.lastDownTime = downTime;
		this.lastKeyCode = keyCode;
		return handle;
	}

	private boolean onLongKey(int keyCode, long l, float downDuration) {
		return handleKeyEvent(keyCode, 0.2F);
	}

	private boolean onSingleTap(int keyCode) {
		return handleKeyEvent(keyCode, 0.5F);
	}

	private boolean handleKeyEvent(int keycode, float downDuration) {
		boolean handle = false;

		if (this.mFocus != null) {
			handle = this.mFocus.onKey(keycode);
			if (handle) {
				return handle;
			}
		}
		if (handle)
			return handle;
		switch (keycode) {
		case 19:

		case 20:

		case 21:

		case 22:
			return onKeyEvent(keycode, downDuration);
		}

		return false;
	}

	private boolean onKeyEvent(int keycode, float downDuration) {
		boolean handler = false;
		if (this.mFocus == null) {
			this.mFocus = getFocusContainer().getFocusActor();
		}
		int direction = getDirection(keycode);
		Actor mNextActor = FocusFinder.findNextActorInGroup(this.mFocus,
				direction, this);
		if (mNextActor != null) {
			if (mNextActor.getY() < this.mHorPadding) {
				smoothScrollTo(mNextActor.getY() - this.mHorPadding,
						downDuration);
				handler = true;
				mNextActor.requestFocus();
			} else if (mNextActor.getY() + mNextActor.getHeight() > 720.0F - this.mHorPadding) {
				smoothScrollTo(mNextActor.getY() + mNextActor.getHeight()
						- 720.0F + this.mHorPadding, downDuration);
				handler = true;
				mNextActor.requestFocus();
			} else {
				handler = false;
			}
			this.mFocus = mNextActor;
		}

		Sound.movePlay();
		return handler;
	}

	public void addActor(Actor actor) {
		float oldY = actor.getY();
		actor.setPosition(actor.getX(), oldY + this.mHorPadding);
		if ((actor.getFocusAble())
				&& (this.mCanvasLength < oldY + this.mHorPadding
						+ actor.getHeight()))
			this.mCanvasLength = (oldY + this.mHorPadding + actor.getHeight());
		this.mList.add(actor);

		super.addActor(actor);
	}

	public static int getDirection(int keycode) {
		switch (keycode) {
		case 21:
			return 3;
		case 22:
			return 4;
		case 19:
			return 2;
		case 20:
			return 1;
		}

		return 0;
	}

	protected void drawChildren(SpriteBatch batch, float parentAlpha) {
		SnapshotArray<Actor> children = getChildren();
		Actor[] actors2 = (Actor[]) children.begin();
		int count = children.size;
		for (int i = 0; i < count; i++) {
			Actor actor = actors2[i];
			float x = actor.getRealityX();
			if ((x + actor.getWidth() > 0.0F)
					&& (x < ScreenAdapterUtil.getWidth())) {
				actor.draw(batch, parentAlpha);
			}
		}
		batch.flush();
		children.end();
	}

	public void smoothToTop(float during) {
		smoothScrollTo(-this.mOffset, during);
		this.mFocus = null;
	}

	public void smoothToButtom(float during) {
		if (this.mCanvasLength + this.mHorPadding > 720.0F)
			smoothScrollTo(this.mCanvasLength + this.mHorPadding - 720.0F
					- this.mOffset, during);
		this.mFocus = null;
	}

	public void notifyHasFocusChanged(Group group, boolean hasFocus) {
		if (hasFocus) {
			this.mFocus = getFocusContainer().getFocusActor();
			if (this.mFocus != null) {
				if (this.mFocus.getY() < this.mHorPadding) {
					smoothScrollTo(this.mFocus.getY() - this.mHorPadding, 0.5F);
				} else if (this.mFocus.getY() + this.mFocus.getHeight() > 720.0F - this.mHorPadding) {
					smoothScrollTo(this.mFocus.getY() + this.mFocus.getHeight()
							- 720.0F + this.mHorPadding, 0.5F);
				}
			}
		}

		super.notifyHasFocusChanged(group, hasFocus);
	}

	float volucity = 0.0F;
	int downY = 0;
	int downX = 0;
	int lastY = 0;
	int lastX = 0;
	int dragY = 0;
	int dragX = 0;
	long downTime = 0L;

	public boolean dispatchTouchEventDown(int x, int y) {
		if (!getRect().contains(x, y)) {
			return false;
		}

		stopScrolling();

		this.downY = y;
		this.downX = x;
		this.lastY = y;
		this.lastX = x;
		this.dragX = x;
		this.dragY = y;
		this.downTime = SystemClock.uptimeMillis();
		return super.dispatchTouchEventDown(x, y);
	}

	public boolean dispatchTouchEventDragged(int x, int y) {
		if (!getRect().contains(this.downX, this.downY)) {
			return super.dispatchTouchEventDown(x, y);
		}
		int scroll = 0;
		if (this.lastY > 0) {
			scroll = this.lastY - y;
		}

		boolean handle = onTouchDragged(x, y, scroll, 0.0F);

		this.lastY = y;
		this.lastX = x;
		if (handle) {
			return handle;
		}
		return super.dispatchTouchEventDown(x, y);
	}

	protected boolean onTouchDragged(int x, int y, int scroll, float time) {
		if (!getIsScrollTo(scroll, time)) {
			return false;
		}

		smoothScrollTo(scroll, time);
		this.dragX = x;
		this.dragY = y;
		return true;
	}

	public boolean notifyTouchDragged(int direction, int downX, int downY,
			int upX, int upY) {
		if (!getRect().contains(downX, downY)) {
			return super.dispatchTouchEventUp(upX, upY);
		}

		int scroll = downY - upY;

		if ((this.mOffset == 0.0F)
				|| (this.mOffset == this.mCanvasLength - getHeight()
						+ this.mHorPadding)) {
			return false;
		}

		long timeDuration = SystemClock.uptimeMillis() - this.downTime;
		boolean handle = getIsScrollTo(scroll, 0.0F);

		if (timeDuration != 0L) {
			smoothScrollTo(scroll, AppGlobalConsts.FOCUSSCALE);
		}

		return true;
	}

}