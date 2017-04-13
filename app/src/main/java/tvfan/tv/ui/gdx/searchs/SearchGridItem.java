package tvfan.tv.ui.gdx.searchs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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

import tvfan.tv.App;
import tvfan.tv.AppGlobalConsts;
import tvfan.tv.R;
import tvfan.tv.ui.gdx.widgets.NinePatchImage;

public class SearchGridItem extends Group implements IListItem, LoaderListener {
    private Image image,imageHolderLogo,bottomShadowImage;
    private Image focusImg,searchIcon;
    //private Image shadowImg;
    private Label label,tipLable;
    private PageImageLoader pageImageLoader;
    private CullGroup cullGroup;
    private String url;
    private String text;
    private Page page;

    public SearchGridItem(Page page, int cullx, int cully) {
        super(page);
        setSize(220, 352);
        setFocusAble(true);
        setFocusScale(AppGlobalConsts.FOCUSSCALE);
        this.page = page;
        // 海报图片
        image = new Image(getPage());
        image.setSize(220, 302);
        image.setPosition(0, 48);
        image.setDrawableResource(R.drawable.placeholder);

        imageHolderLogo = new Image(page);
        imageHolderLogo.setDrawableResource(R.drawable.placeholder_logo);
        imageHolderLogo.setSize(172,53);
        imageHolderLogo.setPosition((220-172)/2, (352-50)/2);

	/*	// 海报阴影
		shadowImg = NinePatchImage.make(getPage(),
				findTexture(R.drawable.bannerbj), new int[] { 10, 10, 10, 10 });
		shadowImg.setSize(300, 400);
		shadowImg.setPosition(0, 0);
		shadowImg.setDrawableResource(R.drawable.postshadow);*/
        // 海报字幕
        label = new Label(getPage());
        label.setPosition(0, 10);
        label.setSize(220, 30);
        label.setColor(Color.WHITE);
        label.setAlignment(Align.center);
        label.setTextSize(30);
        label.setAlpha(0.9f);
        label.setMarquee(false);

        bottomShadowImage = new Image(getPage());
        bottomShadowImage.setPosition(0,0);
        bottomShadowImage.setSize(220,50);
        bottomShadowImage.setDrawable(findRegion(R.mipmap.image_name_bg2));


        // 外框
        cullGroup = new CullGroup(getPage());
        cullGroup.setSize(220, 50);
        cullGroup.setPosition(0, 0);
        cullGroup.setCullingArea(new Rectangle(0, 0, 220, 50));
        cullGroup.addActor(bottomShadowImage);
        cullGroup.addActor(label);

        // 光标选中效果
        focusImg = NinePatchImage.make(page,findTexture(R.mipmap.detail_foucs), new int[]{45,45,45,45});
        focusImg.setSize(224, 354);
        focusImg.setPosition(-2,-2);
        focusImg.setVisible(false);
        addActor(focusImg);


        // 载入场景
        addActor(image);
        addActor(imageHolderLogo);
        addActor(cullGroup);
    }

    public void update(String url) {
        if (url == null) {
            return;
        }
        if (url.isEmpty()) {
            return;
        }
        this.url = url;

        // 在grid滑动时，恢复默认的背景
        image.setDrawableResource(R.drawable.placeholder);

        if (pageImageLoader != null) {
            pageImageLoader.reuse();
            pageImageLoader.cancelLoad();
        }
        pageImageLoader = new PageImageLoader(getPage());
        pageImageLoader.startLoadBitmap(this.url, "list", this, "postimg");
    }

    public void setText(String text) {
        label.setText(text);
        label.setAlignment(Align.center);
        this.text = text;
    }

    @Override
    public void onRecycle() {
        label.setText("");
        label.setMarquee(false);
        image.clearActions();
        image.setDrawableResource(R.drawable.placeholder);
        imageHolderLogo.setVisible(true);
    }

    @Override
    public void notifyFocusChanged(boolean getFocus) {
        super.notifyFocusChanged(getFocus);
        focusImg.setVisible(getFocus);
        label.setMarquee(getFocus);
        if(getFocus){
            bottomShadowImage.setDrawableResource(R.drawable.bottom_blue_bg);
        }else{
            bottomShadowImage.setDrawableResource(R.drawable.bottom_dark_bg);
        }
        label.toFront();
        label.setAlignment(Align.center);
    }

    @Override
    public void onLoadComplete(String imageUrl, TextureRegion textureRegion,
                               Object imageTag) {
        // TODO Auto-generated method stub
        if (this.url.equals(url)) {
            image.addAction(Actions.fadeOut(0f));
            image.setDrawable(new TextureRegionDrawable(textureRegion));
            image.addAction(Actions.fadeIn(0.6f));
            imageHolderLogo.setVisible(false);
        } else {
            textureRegion.getTexture().dispose();
        }
    }


    @Override
    public void onResume() {
	/*	Utils.resetImageSource(image, R.drawable.list_mr);
		shadowImg.setDrawable(new NinePatchDrawable(new NinePatch(
				findRegion(R.drawable.bannerbj), 10, 10, 10, 10)));*/
        //Utils.resetImageSource(focusImg, R.drawable.list_foucs);
        label.setText(text);
        image.setDrawableResource(R.drawable.placeholder);
        focusImg.setDrawable(new NinePatchDrawable(new NinePatch(findRegion(R.mipmap.detail_foucs), 45, 45, 45, 45)));
        update(url);
        super.onResume();
    }

    @Override
    public void onSelected(boolean isfocus) {

    }
}
