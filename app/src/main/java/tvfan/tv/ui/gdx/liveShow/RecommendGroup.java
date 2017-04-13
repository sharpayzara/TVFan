package tvfan.tv.ui.gdx.liveShow;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.dal.models.LiveShowPageItem;
import tvfan.tv.ui.gdx.widgets.NinePatchImage;

import tvfan.tv.R;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.CullGroup;

public class RecommendGroup extends Group implements IListItem, LoaderListener{

	private CullGroup cullGroup;
	private Image image;
	private Image shadowImg, _placeholer, _placeholderlogo;;
	private Image focusImg;
	private Label title, when, where, price,
						 whenL,whereL,priceL;
	
	private PageImageLoader pageImageLoader;
	private String url;
	private String action;
	
	private String parentCatgId, playTime, liveUrl, bigPic, pRice;
	
	
	public RecommendGroup(Page page, int iPostWidth, int iPostHeight, LiveShowPageItem lspi) {
		super(page);
		setSize(iPostWidth, iPostHeight);
		setFocusAble(true);
		setFocusScale(AppGlobalConsts.FOCUSSCALE);
		
		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(iPostWidth, iPostHeight);
		cullGroup.setPosition(0, 0);
		cullGroup.setCullingArea(new Rectangle(0, 0, iPostWidth, iPostHeight));
		
		//背景
		_placeholer = NinePatchImage.make(page, page.findTexture(R.drawable.placeholder),new int[]{60,60,60,60});
		_placeholer.setSize(iPostWidth, iPostHeight);
		_placeholer.setPosition(0, 0);

		_placeholderlogo = NinePatchImage.make(page, page.findTexture(R.drawable.placeholder_logo),new int[]{60,60,60,60});
		_placeholderlogo.setSize(172, 60);
		_placeholderlogo.setPosition((iPostWidth-172)/2, (iPostHeight-60)/2);

		addActor(_placeholer);
		addActor(_placeholderlogo);
		//背景 END
		
		
		image = new Image(getPage());
		image.setSize(iPostWidth, iPostHeight);
		image.setPosition(0, 0);
		
		shadowImg = new Image(getPage());
		shadowImg.setSize(iPostWidth, iPostHeight);
		shadowImg.setPosition(0, 0);
		shadowImg.setDrawableResource(R.drawable.postshadow);
		
		// 光标选中效果
		focusImg = NinePatchImage.make(getPage(),
				this.findTexture(R.drawable.list_foucs), new int[] { 45, 45,
			45, 45 });
		focusImg.setSize(iPostWidth + 85, iPostHeight +85);
		focusImg.setPosition(-44, -44);
		focusImg.setVisible(false);

		
		cullGroup.addActor(image);
		cullGroup.addActor(shadowImg);	
		
		addActor(cullGroup);
		addActor(focusImg);	
		
		parentCatgId = lspi.getId();
		playTime = lspi.getPlayTime();
		liveUrl = lspi.getLiveUrl();
		bigPic = lspi.getBigPic();
		pRice = lspi.getPrice();
		
		try {
			action = lspi.getAction();
			if (action.equals("OPEN_LIVE_SHOW_PAY")) {
				title = new Label(page);
				title.setSize(380, 40);
				title.setPosition(30, 160);
				title.setColor(Color.valueOf("D7D7D7"));
				title.setTextSize(37);
				title.setText(lspi.getName());
				cullGroup.addActor(title);
				
				whenL = new Label(page);
				whenL.setSize(80, 35);
				whenL.setPosition(30, 110);
				whenL.setColor(Color.valueOf("7A7A7C"));
				whenL.setTextSize(33);
				whenL.setText("时间:");
				cullGroup.addActor(whenL);
				
				when = new Label(page);
				when.setSize(80, 300);
				when.setPosition(110, 110);
				when.setColor(Color.valueOf("D7D7D7"));
				when.setTextSize(33);
				when.setText(lspi.getPlayTime());
				cullGroup.addActor(when);
				
				
				whereL = new Label(page);
				whereL.setSize(80, 35);
				whereL.setPosition(30, 70);
				whereL.setColor(Color.valueOf("7A7A7C"));
				whereL.setTextSize(33);
				whereL.setText("场馆:");
				cullGroup.addActor(whereL);
				
				where = new Label(page);
				where.setSize(80, 300);
				where.setPosition(110, 70);
				where.setColor(Color.valueOf("D7D7D7"));
				where.setTextSize(33);
				where.setText(lspi.getPlace());
				cullGroup.addActor(where);
				
				priceL = new Label(page);
				priceL.setSize(80, 35);
				priceL.setPosition(30, 30);
				priceL.setColor(Color.valueOf("7A7A7C"));
				priceL.setTextSize(33);
				priceL.setText("票价:");
				cullGroup.addActor(priceL);
				
				
				price = new Label(page);
				price.setSize(80, 300);
				price.setPosition(110, 30);
				price.setColor(Color.valueOf("FFEB01"));
				price.setTextSize(33);
				price.setText("￥"+lspi.getPrice());
				cullGroup.addActor(price);
				
			} else if (action.equals("OPEN_LIVE_SHOW_LIST") && lspi.getRank().size()<=0) {
				title = new Label(page);
				title.setSize(410, 40);
				title.setPosition(30, 30);
				title.setColor(Color.valueOf("D7D7D7"));
				title.setAlignment(Align.center);
				title.setMarquee(false);
				title.setTextSize(42);
				title.setText(lspi.getName());
				cullGroup.addActor(title);
				
				/*titleCullGroup = new CullGroup(getPage());
				titleCullGroup.setSize(iPostWidth, iPostHeight);
				titleCullGroup.setPosition(0, 0);
				titleCullGroup.setCullingArea(new Rectangle(30, 0, iPostWidth-60, iPostHeight));
				titleCullGroup.addActor(title);
				addActor(titleCullGroup);*/
				
			} else if (action.equals("OPEN_LIVE_SHOW_LIST") && lspi.getRank().size()>0) {
				for (int i = 0; i < lspi.getRank().size(); i++) {
					Image noImg = new Image(getPage());
					noImg.setSize(40, 40);
					noImg.setPosition(30, 170-i*70);
					if (i == 0)
					noImg.setDrawableResource(R.drawable.no1);
					else if(i == 1)
					noImg.setDrawableResource(R.drawable.no2);
					else if(i == 2)
					noImg.setDrawableResource(R.drawable.no3);
					cullGroup.addActor(noImg);
					
					Label cl = new Label(page);
					cl.setSize(410, 40);
					cl.setPosition(30+40, 170-i*70);
					cl.setColor(Color.valueOf("D7D7D7"));
					cl.setAlignment(Align.left);
					//cl.setMarquee(true);
					cl.setTextSize(42);
					cl.setText(lspi.getRank().get(i).getProgramName());
					cullGroup.addActor(cl);
				}
				
			} else if (action.equals("OPEN_LIVE_SHOW_DETAIL")) {
				title = new Label(page);
				title.setSize(410, 40);
				title.setPosition(30, 30);
				title.setColor(Color.valueOf("D7D7D7"));
				title.setAlignment(Align.center);
				title.setMarquee(false);
				title.setTextSize(42);
				title.setText(lspi.getName());
				cullGroup.addActor(title);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
	}
	
	
	public void update(String url,String name) {
		this.url = url;
		if (pageImageLoader != null){
			pageImageLoader.cancelLoad();
		}
		pageImageLoader = new PageImageLoader(getPage());
		pageImageLoader.startLoadBitmap(this.url, "list", this, "postimg");
		//pageImageLoader.startLoadBitmapWithSizeByCut(this.url, "list", true,AppGlobalConsts.CUTLENGTH,image.getWidth(),image.getHeight(),this,"postimg");		
		//label.setText(name);
	}
	
	@Override
	public void onRecycle() {
		/*label.setText("");
		label.setMarquee(false);
		label.setAlignment(Align.center);*/
	}
	
	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);
		//label.setMarquee(getFocus);
		if(getFocus){
			focusImg.setVisible(true);
			focusImg.addAction(Actions.fadeIn(0.1f));
			if (title != null)
			title.setMarquee(true);
		}else{
			focusImg.addAction(Actions.fadeOut(0.1f));		
			if (title != null)
			title.setMarquee(false);
		}
	}

	@Override
	public void onLoadComplete(String imageUrl, TextureRegion textureRegion, Object imageTag) {
		TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(textureRegion);
		image.addAction(Actions.fadeOut(0));
		image.setDrawable(textureRegionDrawable);
		image.addAction(Actions.fadeIn(0.6f));
	}


	@Override
	public void onSelected(boolean arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	public String getParentCatgId() {
		return parentCatgId;
	}


	public void setParentCatgId(String parentCatgId) {
		this.parentCatgId = parentCatgId;
	}


	public String getPlayTime() {
		return playTime;
	}


	public void setPlayTime(String playTime) {
		this.playTime = playTime;
	}


	public String getLiveUrl() {
		return liveUrl;
	}


	public void setLiveUrl(String liveUrl) {
		this.liveUrl = liveUrl;
	}


	public String getBigPic() {
		return bigPic;
	}


	public void setBigPic(String bigPic) {
		this.bigPic = bigPic;
	}


	public String getpRice() {
		return pRice;
	}


	public void setpRice(String pRice) {
		this.pRice = pRice;
	}
}
