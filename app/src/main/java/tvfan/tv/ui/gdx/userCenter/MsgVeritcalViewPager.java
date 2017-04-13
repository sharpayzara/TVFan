package tvfan.tv.ui.gdx.userCenter;

/**
 * 個人中心-播放歷史界面
 * 
 * @author 孫文龍
 * 
 */
import tvfan.tv.ui.gdx.widgets.Button;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.SnapshotArray;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.utils.ScreenAdapterUtil;

public class MsgVeritcalViewPager extends Group {
	// private ArrayList<?> mList = new ArrayList<Group>();
	private float mWidth;
	private float mHeight;
	private float mOffSet = 200;// 游标
	private int msgHight;
	private int msgBtHight;
	private ScollToAction mScollToAction = null;
	float moldOffSet = 0;
	private Image lineImg, mesImg;
	private Label mesLabel1, titleLabel, timeLabel;
	private Button mesButton;
	private int mark = 2;

	public MsgVeritcalViewPager(Page page) {
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

	public void addLabelSection(Label a, int mesLabel1Hight) {
		mesLabel1 = a;
		msgHight = mesLabel1Hight;
		addActor(a);
	}

	public void addButtonSection(Button a, int mesBHight) {
		mesButton = a;
		msgBtHight = mesBHight;
		addActor(a);
	}

	public void addImageSection(Image a) {
		lineImg = a;
		addActor(a);
	}

	public void addTitleLabelSection(Label a) {
		titleLabel = a;
		addActor(a);
	}

	public void addTimeLabelSection(Label a) {
		timeLabel = a;
		addActor(a);
	}

	public void addMesImgSection(Image a) {
		mesImg = a;
		addActor(a);
	}

	/*
	 * public void commit() { for (int i = 0; i < mList.size(); i++) { Group a =
	 * mList.get(i); addActor(a); }
	 * 
	 * }
	 */

	public void smoothScollTo(float offset, float during, int m) {
		this.mark = m;
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
		// Lg.e("mv", "layoutAllGroup");

		if (moldOffSet == mOffSet) {
			return;
		}
		moldOffSet = mOffSet;

		// down
		if (mark == 1) {
			if (mesButton != null) {
				if (msgBtHight - 350 + mOffSet > 50) {
					return;
				}
			} else if (mesLabel1 != null) {
				if (msgHight - 200 + mOffSet > 50) {
					return;
				}
			} else if (mesImg != null) {
				if (230 + mOffSet > 50) {
					return;
				}
			}
		} else if (mark == 0) {
			// up
			if (1080 - 350 + mOffSet < 920) {
				return;
			}
		}
		
		titleLabel.setPosition(150, 1080 - 350 + mOffSet);
		timeLabel.setPosition(1920 - 400 - 150, 1080 - 390 + mOffSet);
		lineImg.setPosition(150, 1080 - 401 + mOffSet);
		if (mesImg != null)
			mesImg.setPosition(660, 230 + mOffSet);
		if (mesLabel1 != null)
			mesLabel1.setPosition(150, msgHight - 200 + mOffSet);
		if (mesButton != null)
			mesButton.setPosition(785, msgBtHight - 350 + mOffSet);

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
			// down
			if (mark == 1) {
				if (mesButton != null) {
					if (msgBtHight - 350 + mOffSet > 50) {
						return;
					}
				} else if (mesLabel1 != null) {
					if (msgHight - 200 + mOffSet > 50) {
						return;
					}
				} else if (mesImg != null) {
					if (230 + mOffSet > 50) {
						return;
					}
				}
			} else if (mark == 0) {
				// up
				if (1080 - 350 + mOffSet < 920) {
					return;
				}
			}
			mOffSet = start + (end - start) * arg0;// 修改游标
		}
	}

	/*
	 * @Override public void onHasFocusChanged(Group group, boolean hasFocus) {
	 * 
	 * if (hasFocus) { downFocus = FocusFinder.findNextActorInGroup(group,
	 * FocusFinder.DOWN, MsgVeritcalViewPager.this); smoothScollTo(-group.getY()
	 * + 0, 0.8f); if (downFocus == null) smoothScollTo(-group.getY(), 0.8f);
	 * else smoothScollTo(-group.getY() + 360, 0.8f);
	 * 
	 * } }
	 */

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
