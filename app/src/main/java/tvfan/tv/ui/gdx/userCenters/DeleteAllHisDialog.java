package tvfan.tv.ui.gdx.userCenters;

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

public class DeleteAllHisDialog extends Dialog {

	private Image bgImg;
	private Label titleLabel;
	PlayHistoryItem hisOR;
	tvfan.tv.ui.gdx.userCenters.Page myPage;
	private Button sure, cancal;
	public DeleteAllHisDialog(final Page page, Context context) {
		super(page);
		setSize(1520, 1080);
		setPosition(400, 0);
		this.myPage = (tvfan.tv.ui.gdx.userCenters.Page) page;
		bgImg = new Image(getPage());
		bgImg.setPosition(347, 247);
		bgImg.setSize(736, 412);
		bgImg.setDrawableResource(R.mipmap.dialog_bg);
		addActor(bgImg);

		titleLabel = new Label(getPage());
		titleLabel.setTextSize(40);
		titleLabel.setColor(Color.WHITE);
		titleLabel.setAlpha(0.9f);
		titleLabel.setPosition(390,570);
		titleLabel.setText("点击确认即可删除全部收藏");
		titleLabel.setAlignment(Align.left);
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
				hisOR._deleteAll();
				DeleteAllHisDialog.this.dismiss();
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
				DeleteAllHisDialog.this.dismiss();
			}
		});
		addActor(cancal);

	}

	public void _initData(PlayHistoryItem hisOR){
		this.hisOR = hisOR;
		titleLabel.setText("点击确认即可删除全部历史");
	}
}
