package tvfan.tv.ui.gdx.programDetail.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.CullGroup;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.R;
import tvfan.tv.lib.Utils;
import tvfan.tv.ui.gdx.widgets.NinePatchImage;

public class DetailTagItem extends Group implements IListItem, LoaderListener {
	private Image image,imageHolder;
	private Label label;
	private PageImageLoader pageImageLoader;
	private Image focusImg;
	private Image focus;
	private CullGroup cullGroup;
	private String url;
	private Image buyImg;
	private Image cornerImg;
	private int leftTr,rightTr;
	
	private Page page;

	public DetailTagItem(Page page) {
		super(page);
		this.page = page;
		setSize(300, 375);
		setFocusAble(true);
		setFocusScale(AppGlobalConsts.FOCUSSCALE);
		cullGroup = new CullGroup(getPage());
		cullGroup.setPosition(0, 0);
		cullGroup.setSize(300, 375);
		cullGroup.setCullingArea(new Rectangle(0, 0, 300, 375));
		addActor(cullGroup);
		
		
		imageHolder = new Image(page);
		imageHolder.setDrawableResource(R.drawable.placeholder);	
		imageHolder.setSize(300, 375);
		imageHolder.setPosition(0, 0);
		cullGroup.addActor(imageHolder);


		image = new Image(page);
		image.setSize(172, 60);
		image.setPosition((300-172)/2, (375-60)/2);
		image.setVisible(true);
		image.setDrawableResource(R.drawable.placeholder_logo);
		cullGroup.addActor(image);
		
		
		focusImg = NinePatchImage.make(page,findTexture(R.drawable.titlebj_normal), new int[]{10,10,10,10});
		focusImg.setSize(300, 100);
		focusImg.setPosition(0, 0);
		focusImg.setVisible(true);
		cullGroup.addActor(focusImg);
		
		// 光标选中效果
		focus =  NinePatchImage.make(page,findTexture(R.drawable.list_foucs), new int[]{45,45,45,45});
		focus.setSize(300+88, 375+88);
		focus.setPosition(-44, -44);
		focus.setVisible(false);
		addActor(focus);

		buyImg = new Image(page);
		buyImg.setSize(100, 100);
		buyImg.setPosition(0, 300);
		buyImg.setFocusAble(false);
		buyImg.setVisible(false);
		cullGroup.addActor(buyImg);
		
		
		cornerImg = new Image(page);
		cornerImg.setSize(100, 100);
		cornerImg.setPosition(200, 200);
		cornerImg.setFocusAble(false);
		cornerImg.setVisible(false);
		cullGroup.addActor(cornerImg);
		
		label = new Label(getPage(), false);
		label.setPosition(0, 15);
		label.setSize(300, 45);
		label.setTextSize(38);
		label.setColor(Color.valueOf("f0f0f0"));
		label.setAlignment(Align.center);
		label.setMarquee(false);
		cullGroup.addActor(label);
		
	}
	
	
	@Override
	public void onResume() {
		imageHolder.setDrawableResource(R.drawable.placeholder);
		image.setDrawableResource(R.drawable.placeholder_logo);
		update(url);
		focus.setDrawable(new NinePatchDrawable(new NinePatch(findRegion(R.drawable.list_foucs), 45, 45, 45, 45)));
		focusImg.setDrawable(new NinePatchDrawable(new NinePatch(findRegion(R.drawable.titlebj_normal), 10, 10, 10, 10)));
		if (leftTr != -1 && Utils.isResourceRecycled(buyImg))
			buyImg.setDrawableResource(leftTr);
		if (rightTr != -1 && Utils.isResourceRecycled(cornerImg))
			cornerImg.setDrawableResource(rightTr);
		super.onResume();
	}

	public void update(String url) {
		this.url = url;
		if (pageImageLoader != null) {
			pageImageLoader.cancelLoad();
		}
		image.setVisible(true);
		imageHolder.setDrawableResource(R.drawable.placeholder);
		pageImageLoader = new PageImageLoader(getPage());
		pageImageLoader.startLoadBitmap(url, "list",this,url);
		//pageImageLoader.startLoadBitmap(url, "list", true, AppGlobalConsts.CUTLENGTH,this,url);
	}	
	
	public void setText(String text) {
		label.setText(text);
	}	

	@Override
	public void onRecycle() {
		label.setText("");
		label.setMarquee(false);
		label.setAlignment(Align.center);
	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);
		label.setMarquee(getFocus);
		focus.setVisible(getFocus);
//		if (getFocus) {
//			label.setAlpha(0.8f);
//		} else {
//			label.setAlpha(0.6f);
//		}

	}
	
	public void setRightCornerImage(String tr) {
		cornerImg.setVisible(false);
		int id = Utils.getSuperScriptImageResourceId(page, "right", tr);
		rightTr = id;
		if (id != -1) {
			TextureRegion cp = findRegion(id);
			cornerImg.setSize(cp.getRegionWidth(), cp.getRegionWidth());
			cornerImg.setPosition(300 - cp.getRegionWidth(),
					400 - cp.getRegionHeight());
			cornerImg.setVisible(true);
			cornerImg.setDrawable(cp);
		}
	}

	public void setLeftCornerImage(String tr) {
		buyImg.setVisible(false);
		int id = Utils.getSuperScriptImageResourceId(page, "left", tr);
		leftTr = id;
		if (id != -1) {
			TextureRegion ct = findRegion(id);
			buyImg.setSize(ct.getRegionWidth(), ct.getRegionWidth());
			buyImg.setPosition(0, 375 - ct.getRegionHeight());
			buyImg.setVisible(true);
			buyImg.setDrawable(ct);
		}
	}

	@Override
	public void onSelected(boolean isSelected) {

	}

	@Override
	public void onLoadComplete(String imageUrl, TextureRegion textureRegion, Object imageTag) {
		imageHolder.setDrawable(new TextureRegionDrawable(textureRegion));
		image.setVisible(false);
	}

}
