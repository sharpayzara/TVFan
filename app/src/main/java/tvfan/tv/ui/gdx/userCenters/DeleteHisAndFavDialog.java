package tvfan.tv.ui.gdx.userCenters;

import android.content.Context;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Dialog;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.OnFocusChangeListener;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.CullGroup;

import tvfan.tv.R;
import tvfan.tv.ui.gdx.widgets.Button;

public class DeleteHisAndFavDialog extends Dialog {

	private Image bgImg;
	private Label titleLabel;
	private String dName = "";
	private String type = "";
	FavoritesItem favOR;
	PlayHistoryItem hisOR;
	tvfan.tv.ui.gdx.userCenters.Page myPage;
	private Button sure, cancal;
	public DeleteHisAndFavDialog(final Page page, Context context) {
		super(page);
		setSize(1520, 1080);
		setPosition(400, 0);
		this.myPage = (tvfan.tv.ui.gdx.userCenters.Page) page;
		bgImg = new Image(getPage());
		bgImg.setPosition(347, 247);
		bgImg.setSize(736, 412);
		bgImg.setDrawableResource(R.mipmap.dialog_bg);
		addActor(bgImg);

/*
		CullGroup group = new CullGroup(getPage());
		group.setPosition(320,670);
		group.setCullingArea(new Rectangle(0,-120,320,670));*/
		titleLabel = new Label(getPage());
		titleLabel.setTextSize(40);
		titleLabel.setSize(650,100);
		titleLabel.setMaxLine(2);
		titleLabel.setColor(Color.WHITE);
		titleLabel.setAlpha(0.9f);
		titleLabel.setPosition(390,525);
		titleLabel.setText("点击确认即可删除");
		titleLabel.setAlignment(Align.left);
	/*	group.addActor(titleLabel);
		addActor(group);*/
		addActor(titleLabel);

		sure = new Button(getPage(), 211, 73);
		sure.setPosition(487, 283);
		sure.getImage().setDrawableResource(R.mipmap.dialog_enter_normal);
		sure.setUnFocusBackGround(R.mipmap.dialog_enter_normal);
		sure.setFocusBackGround(R.mipmap.dialog_enter_selected);
		sure.setFocusAble(true);
		sure.getLabel().setTextSize(45);
		sure.getLabel().setColor(Color.WHITE);
		sure.getLabel().setAlpha(0.5f);

		cancal = new Button(getPage(), 211, 74);
		cancal.setPosition(725, 283);
		cancal.getImage().setDrawableResource(R.mipmap.dialog_esc_normal);
		cancal.setUnFocusBackGround(R.mipmap.dialog_esc_normal);
		cancal.setFocusBackGround(R.mipmap.dialog_esc_selected);
		cancal.setFocusAble(true);
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
			this.favOR = (FavoritesItem) favORhis;
		} else if (type.equals("his")) {
			this.hisOR = (PlayHistoryItem) favORhis;
		}
		titleLabel.setText("点击确认即可删除" + dName);
	}
}
