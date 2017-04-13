package tvfan.tv.ui.gdx.widgets;

import android.annotation.SuppressLint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.image.LoaderListener;
import com.luxtone.lib.image.PageImageLoader;
import com.luxtone.lib.widget.CullGroup;

import java.util.HashMap;

import tvfan.tv.AppGlobalConsts;
import tvfan.tv.R;
import tvfan.tv.lib.Lg;

public class HorizontalIconListItem extends Group implements LoaderListener {

	final private String TAG = "TVFAN.EPG.HorizontalIconListItem";
	PageImageLoader _imageLoader;
	// GdxPageImageBatchLoader _imageLoader;
	CullGroup cullGroup;
	Image _focusImg,  _icon, _smallIcon, _shadowImg, _placeholer,
			_placeholderlogo, _maskImg, infoImg; //
	TVFANLabel _txt, infoTxt;
	IconListItemListener _iconListItemListener;
	HashMap<String, String> _params = new HashMap<String, String>();
	private int _w, _h;
	boolean _keepShowTitle = false;
	Page mPage;

	public interface IconListItemListener {
		public void focusChanged(HorizontalIconListItem selectedItem);
	}

	public HorizontalIconListItem(Page page, int[] size,
			IconListItemListener iconListItemListener) {
		super(page);
		// _imageLoader = imageLoader;
		this.mPage = page;
		_imageLoader = new PageImageLoader(page);
		_iconListItemListener = iconListItemListener;

		_w = size[0];
		_h = size[1];
		setSize(size[0], size[1]);
		setFocusAble(true);
		setFocusScale(AppGlobalConsts.FOCUSSCALE);

		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(size[0], size[1]);
		cullGroup.setPosition(0, 0);
		cullGroup.setCullingArea(new Rectangle(0, 0, size[0], size[1]));

		_icon = new Image(page);
		_icon.setSize(size[0], size[1]);
		_icon.setPosition(0, 0);

		_smallIcon = new Image(page);
		_smallIcon.setSize(140, 140);
		_smallIcon.setPosition(77, 112);
		_smallIcon.setVisible(false);

		_maskImg = new Image(page);
		_maskImg.setSize(size[0], size[1]);
		_maskImg.setPosition(0, 0);
		_maskImg.setDrawableResource(R.drawable.usercentericon_rect);
		_maskImg.setVisible(false);

		_shadowImg = new Image(page);
		_shadowImg.setSize(size[0], 60);
		_shadowImg.setPosition(0, 0);
//		_shadowImg.setDrawableResource(R.drawable.postshadow);
//		_shadowImg.setDrawableResource(R.drawable.jdt1);
		_shadowImg.setDrawableResource(R.drawable.bottom_blue_bg);
		_shadowImg.setVisible(false);

		_focusImg = NinePatchImage.make(getPage(),
				this.findTexture(R.drawable.list_foucs), new int[] { 45, 45,
						45, 45 });
//		_focusImg.setSize(size[0] + 83.5f, size[1] + 83.5f);
		_focusImg.setSize(size[0] + 64f, size[1] + 43.5f);
//		_focusImg.setPosition(-42f, -42f);
		_focusImg.setPosition(-32f, -22f);
		_focusImg.setVisible(false);

		_placeholer = NinePatchImage.make(page,
				page.findTexture(R.drawable.placeholder));
		_placeholer.setSize(size[0], size[1]);
		_placeholer.setPosition(0, 0);

		_placeholderlogo = NinePatchImage.make(page,
				page.findTexture(R.drawable.placeholder_logo));
		_placeholderlogo.setSize(172, 60);
		_placeholderlogo.setPosition((size[0] - 172) / 2, (size[1] - 60) / 2);

		cullGroup.addActor(_placeholer);
		cullGroup.addActor(_placeholderlogo);
		cullGroup.addActor(_icon);
		cullGroup.addActor(_smallIcon);
		cullGroup.addActor(_maskImg);
		cullGroup.addActor(_shadowImg);

		addActor(cullGroup);
		addActor(_focusImg);

		_txt = new TVFANLabel(page);
		_txt.setSize(size[0], 40);
		_txt.setPosition(0, 10);
		_txt.setColor(Color.valueOf("f0f0f0"));
		_txt.setAlignment(Align.center);
		_txt.setTextSize(30);
		_txt.setVisible(false);

		cullGroup.addActor(_txt);
	}

	@SuppressLint("NewApi")
	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);

		if (getFocus && _iconListItemListener != null)
			_iconListItemListener.focusChanged(this);

		boolean isShowText = false;
		if (!_keepShowTitle && _txt != null) {
			isShowText = !_txt.getText().toString().isEmpty();
			_txt.setMarquee(getFocus);
		}

		if (getFocus) {
			if (isShowText) {
				_txt.setVisible(true);
				_txt.addAction(Actions.fadeIn(0.1f));
				_shadowImg.setVisible(getFocus);
			}
			_focusImg.setVisible(true);
			_focusImg.addAction(Actions.fadeIn(0.1f));
		} else {
			_focusImg.addAction(Actions.fadeOut(0.1f));
			if (isShowText) {
				_txt.addAction(Actions.fadeOut(0.1f));
				_shadowImg.setVisible(getFocus);
			}
		}
	}

	public void addIcon(final String url) {
		if (url.startsWith("@")) {
			// int z = _icon.getZIndex();
			// _icon.remove();
			// _icon.dispose();
			// _icon = new Image(getPage());
			Gdx.app.postRunnable(new Runnable() {

				@Override
				public void run() {
					_icon.setDrawableResource(Integer.parseInt(url.substring(1)));
				}
			});
			// _icon.setZIndex(z);
			// _icon.setSize(_w,_h);
			// _icon.setPosition(0, 0);
			// cullGroup.addActor(_icon);
		} else if (url.startsWith("http")) {
			if (_imageLoader != null) {
				_imageLoader.cancelLoad();
			}
			_imageLoader = new PageImageLoader(mPage);
			_imageLoader.startLoadBitmap(url, "hili", this, 0);
		}
	}

	/**
	 * add by wanqi,消息的条数提示
	 * 
	 * @param msg
	 */
	public void updateMsgTip(int msg) {
		if (msg == 0) {
			if (infoImg != null) {
				infoImg.setVisible(false);
			}
			if (infoTxt != null) {
				infoTxt.setVisible(false);
			}
			return;
		}
		if (infoImg == null) {
			infoImg = new Image(mPage);
			infoImg.setSize(85, 45);
			infoImg.setPosition(235, 380);
			infoImg.setDrawableResource(R.drawable.info_dot);
			infoImg.setVisible(true);
			cullGroup.addActor(infoImg);
		}
		if (infoTxt == null) {
			infoTxt = new TVFANLabel(mPage);
			infoTxt.setSize(85, 45);
			infoTxt.setPosition(235, 380);
			infoTxt.setColor(Color.valueOf("f0f0f0"));
			infoTxt.setAlignment(Align.center);
			infoTxt.setTextSize(34);
			infoTxt.setVisible(true);
			cullGroup.addActor(infoTxt);
		}
		infoImg.setVisible(true);
		infoTxt.setVisible(true);
		if (msg > 99) {
			infoTxt.setText("99+");
		} else {
			infoTxt.setText(msg + "");
		}

	}

	public void showMask(boolean isShow) {
		_maskImg.setDrawableResource(R.drawable.usercentericon_rect);
		_maskImg.setVisible(isShow);
	}

	public void showHeadimg(String url, boolean isShow) {
		if (isShow) {
			if (_imageLoader != null) {
				_imageLoader.cancelLoad();
			}
			_imageLoader = new PageImageLoader(mPage);
			_imageLoader.startLoadBitmap(url, "hili", this, 1);
		}
		_smallIcon.setVisible(isShow);
		_icon.setVisible(!isShow);
	}

	public void setTitle(String txt) {
		_txt.setText(txt);
		// _txt.toFront();
	}

	public void keepShowTitle() {
		_keepShowTitle = true;
		_txt.setVisible(true);
	}

	public void putParam(String key, String value) {
		_params.put(key, value);
	}

	public String getParam(String key) {
		return _params.get(key);
	}

	@Override
	public void onLoadComplete(String url, TextureRegion texture, Object tag) {
		_placeholer.setVisible(false);
		_placeholderlogo.setVisible(false);
		int p = (Integer) tag;
		if (p == 0) {
			_icon.addAction(Actions.fadeOut(0));
			_icon.setDrawable(new TextureRegionDrawable(texture));
			_icon.addAction(Actions.fadeIn(0.6f));
		} else if (p == 1) {
			_smallIcon.addAction(Actions.fadeOut(0));
			_smallIcon.setDrawable(new TextureRegionDrawable(texture));
			_smallIcon.addAction(Actions.fadeIn(0.6f));
		}
	}

	/**
	 * wanqi,用来判断当前推荐位上是否已经有图片资源
	 * @return
	 */
	public boolean hasDrawable() {
		Drawable d = _icon.getDrawable();
		if (d == null) {
			Lg.i(TAG, "---------0-----------当前推荐位无图片资源");
			return false;
		}
		Lg.i(TAG, "---------1-----------当前推荐位有图片资源");
		return true;
	}

	@Override
	public void onResume() {
		if (infoImg != null)
			infoImg.setDrawableResource(R.drawable.info_dot);
		super.onResume();
	}
}
