package tvfan.tv.ui.gdx.widgets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.luxtone.lib.gdx.Page;

final public class NinePatchImage {

	/**
	 * @param page Context
	 * @param texture image
	 * @param borders left, right, top, bottom
	 **/
	public static Image make(Page page, Texture texture, int[] borders) {
		/*
		Example:
		Image np = NinePatchImage.make(this,
				this.findTexture(R.drawable.ok),
				new int[]{60,60,60,60});
		np.setSize(1500, 1000);
		np.setPosition(50, 50);
		addActor(np);
		*/
		return new Image(page, new NinePatch(texture,
				borders[0],
				borders[1],
				borders[2],
				borders[3]));
	}

	/**
	 * @param page Context
	 * @param texture image
	 **/
	public static Image make(Page page, Texture texture) {
		/*
		Example:
		Image np = NinePatchImage.make(this,
				this.findTexture(R.drawable.ok));
		np.setSize(1500, 1000);
		np.setPosition(50, 50);
		addActor(np);
		*/
		return new Image(page, new NinePatch(texture));
	}
}
