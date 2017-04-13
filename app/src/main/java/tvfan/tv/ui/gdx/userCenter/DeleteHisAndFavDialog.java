package tvfan.tv.ui.gdx.userCenter;

import android.content.Context;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Dialog;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.OnFocusChangeListener;
import com.luxtone.lib.gdx.Page;

import tvfan.tv.R;
import tvfan.tv.ui.gdx.widgets.Button;

public class DeleteHisAndFavDialog extends Dialog {

	private Image bgImg;
	private Label titleLabel;
	private String dName = "";
	private String type = "";
	tvfan.tv.ui.gdx.userCenter.FavoritesItem favOR;
	tvfan.tv.ui.gdx.userCenter.PlayHistoryItem hisOR;
	tvfan.tv.ui.gdx.userCenter.Page myPage;
	private Button sure, cancal;
	public DeleteHisAndFavDialog(final Page page, Context context) {
		super(page);
		setSize(1000, 660);
		setPosition(460, 270);
		this.myPage = (tvfan.tv.ui.gdx.userCenter.Page) page;
		bgImg = new Image(getPage());
		bgImg.setPosition(0, 0);
		bgImg.setSize(1000, 660);
		bgImg.setDrawableResource(R.drawable.playercenter_layout_background);
		addActor(bgImg);

		titleLabel = new Label(getPage());
		titleLabel.setSize(1000, 45);
		titleLabel.setTextSize(45);
		titleLabel.setColor(Color.WHITE);
		titleLabel.setAlpha(0.9f);
		titleLabel.setPosition(0, 660 - 120 - 45);
		titleLabel.setAlignment(Align.center);
		addActor(titleLabel);
		
		sure = new Button(getPage(), 350, 150);
		sure.setPosition(125, 80);
		sure.getImage().setDrawableResource(R.drawable.paybtn);
		sure.setUnFocusBackGround(R.drawable.paybtn);
		sure.setFocusBackGround(R.drawable.new_foucs);
		sure.setFocusAble(true);
		sure.getLabel().setText("确认");
		sure.getLabel().setTextSize(45);
		sure.getLabel().setColor(Color.WHITE);
		sure.getLabel().setAlpha(0.5f);

		cancal = new Button(getPage(), 350, 150);
		cancal.setPosition(525, 80);
		cancal.getImage().setDrawableResource(R.drawable.paybtn);
		cancal.setUnFocusBackGround(R.drawable.paybtn);
		cancal.setFocusBackGround(R.drawable.new_foucs);
		cancal.setFocusAble(true);
		cancal.getLabel().setText("取消");
		cancal.getLabel().setTextSize(45);
		cancal.getLabel().setColor(Color.WHITE);
		cancal.getLabel().setAlpha(0.5f);

		sure.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChanged(Actor arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					cancal.getLabel().setAlpha(0.5f);
					sure.getLabel().setAlpha(0.8f);
				}

			}
		});
		sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(Actor arg0) {
				if (DeleteHisAndFavDialog.this.type.equals("fav")) {
					favOR._updateGrid();
				} else if (DeleteHisAndFavDialog.this.type.equals("his")) {
					hisOR._updateGrid();
				}
				DeleteHisAndFavDialog.this.dismiss();
			}
		});
		addActor(sure);

		cancal.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChanged(Actor arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					sure.getLabel().setAlpha(0.5f);
					cancal.getLabel().setAlpha(0.8f);
				}

			}
		});
		cancal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(Actor arg0) {
				DeleteHisAndFavDialog.this.dismiss();
			}
		});
		addActor(cancal);

	}
	
	public void _initData(final String type, String dName, Group favORhis){
		this.dName = dName;
		this.type = type;
		if (type.equals("fav")) {
			this.favOR = (tvfan.tv.ui.gdx.userCenter.FavoritesItem) favORhis;
		} else if (type.equals("his")) {
			this.hisOR = (tvfan.tv.ui.gdx.userCenter.PlayHistoryItem) favORhis;
		}
		titleLabel.setText("点击确认即可删除" + dName);
	}
}
