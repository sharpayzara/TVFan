package tvfan.tv.ui.gdx.widgets;

import tvfan.tv.R;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;

/**
 * loading控件
 */
public class CIBNLoadingView extends WidgetGroup {

	private Texture txe;
	private TVFANLabel label;

	private static final int FRAME_COLS = 1;
	private static final int FRAME_ROWS = 8;

	private Animation walkAnimation;
	private TextureRegion[] walkFrames;
	private TextureRegion currentFrame;
	private float width = 1920, height = 1080, fx = 0, fy = 0;
	private float stateTime;

	SpriteBatch mBatch = null;

	public CIBNLoadingView(Page page) {
		super(page);
		// txe = new Texture(Gdx.files.internal("loadings.png"));
		//
		// TextureRegion[][] tmp = TextureRegion.split(txe, txe.getWidth()
		// / FRAME_COLS, txe.getHeight() / FRAME_ROWS);
		//
		// walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
		//
		// int index = 0;
		// for (int i = 0; i < FRAME_ROWS; i++) {
		// for (int j = 0; j < FRAME_COLS; j++) {
		// walkFrames[index++] = tmp[i][j];
		// }
		// }
		// walkAnimation = new Animation(0.16f, walkFrames);
		//
		// walkAnimation.setPlayMode(walkAnimation.LOOP);
		//
		// stateTime = 0f;
		Image image = new Image(page);
		image.setDrawableResource(R.drawable.logo);
		image.setSize(274, 96);
		image.setPosition(width / 2 - image.getWidth() / 2,
				height / 2 + image.getHeight() / 2);
		addActor(image);

		label = new TVFANLabel(page);
		label.setSize(300, 80);
		label.setPosition(width / 2 - image.getWidth() / 2, height / 2 - 50);
		label.setAlignment(Align.center);
		label.setText("正在加载数据,请稍候");
		label.setColor(Color.valueOf("f0f0f0"));
		label.setAlignment(Align.center);
		label.setTextSize(35);
		addActor(label);

	}

	public void setText(String text) {
		label.setText(text);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		mBatch = batch;

		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// 获取系统渲染时间，一般默认是0.173秒
//		stateTime += Gdx.graphics.getDeltaTime();
//
//		currentFrame = walkAnimation.getKeyFrame(stateTime, true);
//
//		mBatch.draw(currentFrame, fx + width / 2 - txe.getWidth() / 2, fy + 30
//				+ height / 2);
		super.draw(batch, parentAlpha);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}

	@Override
	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
		label.setPosition(width / 2 - 300 / 2, height / 2 - 60);
		super.setSize(width, height);
	}

	@Override
	public void setPosition(float x, float y) {
		fx = x;
		fy = y;
		super.setPosition(x, y);
	}

	@Override
	public void clear() {
		txe.dispose();
		super.clear();
	}

}
