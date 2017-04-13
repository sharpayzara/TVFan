package tvfan.tv.ui.gdx.programDetail.item;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.ui.gdx.widgets.NinePatchImage;

import tvfan.tv.R;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.CullGroup;

public class StagePhotoGridItem extends Group implements IListItem, LoaderListener {
	private Image image;
	private Label label;
	private CullGroup cullGroup;
	private PageImageLoader pageImageLoader;
	private Image shadowImg;
	private Image focusImg;

	public StagePhotoGridItem(Page page) {
		super(page);
		setSize(450, 370);
		setFocusAble(true);
		cullGroup = new CullGroup(getPage());
		cullGroup.setPosition(5, 20);
		cullGroup.setSize(440, 330);
		cullGroup.setCullingArea(new Rectangle(0, 0, 440, 330));
		addActor(cullGroup);

		image = new Image(getPage());
		image.setSize(440, 330);
		image.setPosition(0, 0);
		image.setVisible(true);
		cullGroup.addActor(image);
		
		shadowImg = NinePatchImage.make(page, findTexture(R.drawable.bannerbj), new int[]{10,10,10,10});
		shadowImg.setSize(440, 330);
		shadowImg.setPosition(0, 0);
		shadowImg.setVisible(false);
		cullGroup.addActor(shadowImg);
		
		label = new Label(getPage(), false);
		label.setPosition(0, 15);
		label.setSize(440, 30);
		label.setTextSize(30);
		label.setMaxLine(1);
		label.setVisible(false);
		label.setColor(Color.WHITE);
		label.setAlignment(Align.center);
		cullGroup.addActor(label);
		
		focusImg = NinePatchImage.make(page,findTexture(R.drawable.list_foucs), new int[]{60,60,60,60});
		focusImg.setPosition(-50, -30);
		focusImg.setSize(550, 435);
		focusImg.setVisible(false);
		addActor(focusImg);
		
		
		
		
		setFocusScale(AppGlobalConsts.FOCUSSCALE);
	}

	@Override
	public void onLoadComplete(String url, TextureRegion texture, Object tag) {
		image.setDrawable(new TextureRegionDrawable(texture));
	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		//label.setVisible(getFocus);
		super.notifyFocusChanged(getFocus);
		focusImg.setVisible(getFocus);
		if (getFocus) {
//			image.addAction(Actions.forever(Actions.sequence(Actions.alpha(1),
//					Actions.alpha(0.6f, 1.2f), Actions.alpha(1, 1.2f))));
		} else {
//			image.setAlpha(1f);
//			image.clearActions();
		}
	}
	
	public void update(String url) {
		if (pageImageLoader != null) {
			pageImageLoader.cancelLoad();
		}
		pageImageLoader = new PageImageLoader(getPage());
		//pageImageLoader.startLoadBitmap(url, "list", this, url);
//		url = "http://images.ott.cibntv.net/2015/04/16/huijiadeluyk1.jpg#350*470";
//		pageImageLoader.startLoadBitmap(url, "list", true, AppGlobalConsts.CUTLENGTH, this,url);
		pageImageLoader.startLoadBitmapWithSizeByCut(url, "list", true, AppGlobalConsts.CUTLENGTH, 440, 330, this, "list");
		//label.setText(url);
	}


	@Override
	public void onRecycle() {
		label.setText("");
		label.setMarquee(false);
		image.clearActions();
	}
	

	@Override
	public void onSelected(boolean isSelected) {

	}

	public void setText(String text) {
		label.setText(text);
	}
	

}
