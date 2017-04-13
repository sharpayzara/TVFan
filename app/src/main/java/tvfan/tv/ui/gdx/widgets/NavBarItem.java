package tvfan.tv.ui.gdx.widgets;

import org.json.JSONObject;

import tvfan.tv.R;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.CullGroup;

public class NavBarItem extends CullGroup {

	private Label lab;
	private Image dot = null;
	private Image focusImage;
	public Image showImage;

	public NavBarItem(Page page, JSONObject listitem, int itemIdx) {
		super(page);
		setFocusAble(false);
		setSize(240, 200);
		setPosition(0, 0);
		setName(String.valueOf(itemIdx));
		this.toFront();
		// 选中背景图
		focusImage = new Image(page);
		focusImage.setDrawableResource(R.drawable.home_light_big);
		focusImage.setSize(200f, 215f);
		focusImage.setPosition(28, -45);
		focusImage.setVisible(false);
		addActor(focusImage);

		lab = new Label(page);
//		lab.setPosition(0, 0);
		lab.setSize(240, 200);
		lab.setAlignment(Align.center_Horizontal);
		lab.setTextSize(30);
		lab.setFocusAble(false);
		lab.setColor(Color.valueOf("2e94e8"));
		lab.setName("m" + String.valueOf(itemIdx));
//		lab.setAlpha(0.8f);
		try {
			lab.setText(listitem.getString("name"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		addActor(lab);

		// 显示的图片
		showImage = new Image(page);
		if (lab.getText().toString().equals("推荐")) {
			showImage.setDrawableResource(R.drawable.home_recommend_normal);
		}
		if (lab.getText().toString().equals("直播")) {
			showImage.setDrawableResource(R.drawable.home_live_normal);
		}
		if (lab.getText().toString().equals("电影")) {
			showImage.setDrawableResource(R.drawable.home_movie_normal);
		}
		if (lab.getText().toString().equals("电视剧")) {
			showImage.setDrawableResource(R.drawable.home_tv_series_normal);
		}
		if (lab.getText().toString().equals("综艺")) {
			showImage.setDrawableResource(R.drawable.home_variety_normal);
		}
		if (lab.getText().toString().equals("体育")) {
			showImage.setDrawableResource(R.drawable.home_sports_normal);
		}
		if (lab.getText().toString().equals("动漫")) {
			showImage.setDrawableResource(R.drawable.home_cartoon_normal);
		}
		if (lab.getText().toString().equals("更多")) {
			showImage.setDrawableResource(R.drawable.home_more_normal);
		}
		showImage.setSize(70f, 70f);
		showImage.setPosition(85, 55);
//		showImage.setAlign(Align.center_Horizontal + Align.center_Vertical);
		showImage.setVisible(true);
		addActor(showImage);

		if (lab.getText().toString().equals("用户")) {
			dot = new Image(page);
			dot.setDrawableResource(R.drawable.reddot);
			dot.setSize(13, 13);
			dot.setPosition(150, 50);
			dot.setVisible(false);
			addActor(dot);
		}
	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		focusImage.setVisible(getFocus);
		if (getFocus) {
//			lab.setAlpha(1f);
//			lab.setTextSize(55);
			lab.setColor(Color.WHITE);
			if (lab.getText().toString().equals("推荐")) {
				showImage.setDrawableResource(R.drawable.home_recommend_selected);
			}
			if (lab.getText().toString().equals("直播")) {
				showImage.setDrawableResource(R.drawable.home_live_selected);
			}
			if (lab.getText().toString().equals("电影")) {
				showImage.setDrawableResource(R.drawable.home_movie_selected);
			}
			if (lab.getText().toString().equals("电视剧")) {
				showImage.setDrawableResource(R.drawable.home_tv_series_selected);
			}
			if (lab.getText().toString().equals("综艺")) {
				showImage.setDrawableResource(R.drawable.home_variety_selected);
			}
			if (lab.getText().toString().equals("体育")) {
				showImage.setDrawableResource(R.drawable.home_sports_selected);
			}
			if (lab.getText().toString().equals("动漫")) {
				showImage.setDrawableResource(R.drawable.home_cartoon_selected);
			}
			if (lab.getText().toString().equals("更多")) {
				showImage.setDrawableResource(R.drawable.home_more_selected);
			}
		} else {
//			lab.setAlpha(0.8f);
//			lab.setTextSize(50);
			lab.setColor(Color.valueOf("2e94e8"));
			if (lab.getText().toString().equals("推荐")) {
				showImage.setDrawableResource(R.drawable.home_recommend_normal);
			}
			if (lab.getText().toString().equals("直播")) {
				showImage.setDrawableResource(R.drawable.home_live_normal);
			}
			if (lab.getText().toString().equals("电影")) {
				showImage.setDrawableResource(R.drawable.home_movie_normal);
			}
			if (lab.getText().toString().equals("电视剧")) {
				showImage.setDrawableResource(R.drawable.home_tv_series_normal);
			}
			if (lab.getText().toString().equals("综艺")) {
				showImage.setDrawableResource(R.drawable.home_variety_normal);
			}
			if (lab.getText().toString().equals("体育")) {
				showImage.setDrawableResource(R.drawable.home_sports_normal);
			}
			if (lab.getText().toString().equals("动漫")) {
				showImage.setDrawableResource(R.drawable.home_cartoon_normal);
			}
			if (lab.getText().toString().equals("更多")) {
				showImage.setDrawableResource(R.drawable.home_more_normal);
			}

		}

		super.notifyFocusChanged(getFocus);
	}

	public void showReddot(boolean isShow) {
		if (dot == null)
			return;
		dot.setVisible(isShow);
	}

	public String getText() {
		return lab.getText().toString();
	}

}
