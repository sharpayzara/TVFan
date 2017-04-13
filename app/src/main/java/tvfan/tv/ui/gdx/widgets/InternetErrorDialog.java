package tvfan.tv.ui.gdx.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Dialog;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.OnFocusChangeListener;
import com.luxtone.lib.gdx.Page;

import tvfan.tv.R;
//import com.umeng.analytics.MobclickAgent;

public class InternetErrorDialog extends Dialog {

	private Image bgImg;
	private Button sure, cancal;
	private Label titleLabel;
	private Page page;
	private ConfirmDialogListener _confirmDialogListener;

	public interface ConfirmDialogListener {
		
		public void onOK(Dialog dialog);

		public void onCancel(Dialog dialog);
	}

	public InternetErrorDialog(final Page page, final String msg,
			ConfirmDialogListener confirmDialogListener) {
		super(page);
		_confirmDialogListener = confirmDialogListener;
		setSize(1000, 660);
		setPosition(460, 270);
		this.page = page;

		bgImg = new Image(getPage());
		bgImg.setPosition(0, 0);
		bgImg.setSize(1000, 660);
		bgImg.setDrawableResource(R.drawable.tkbj);
		addActor(bgImg);

		titleLabel = new Label(getPage());
		titleLabel.setSize(700, 150);
		titleLabel.setTextSize(45);
		titleLabel.setColor(Color.WHITE);
		titleLabel.setSpacingadd(20);
		titleLabel.setAlpha(0.5f);
		titleLabel.setText(msg);
		titleLabel.setPosition(160, 300);
		titleLabel.setMaxLine(2);
		titleLabel.setAlignment(Align.center);
		addActor(titleLabel);

		sure = new Button(getPage(), 350, 150);
		sure.setPosition(125, 80);
		sure.getImage().setDrawableResource(R.drawable.paybtn);
		sure.setUnFocusBackGround(R.drawable.paybtn);
		sure.setFocusBackGround(R.drawable.new_foucs);
		sure.setFocusAble(true);
		sure.getLabel().setText("退出");
		sure.getLabel().setTextSize(45);
		sure.getLabel().setColor(Color.WHITE);
		sure.getLabel().setAlpha(0.5f);

		cancal = new Button(getPage(), 350, 150);
		cancal.setPosition(525, 80);
		cancal.getImage().setDrawableResource(R.drawable.paybtn);
		cancal.setUnFocusBackGround(R.drawable.paybtn);
		cancal.setFocusBackGround(R.drawable.new_foucs);
		cancal.setFocusAble(true);
		cancal.getLabel().setText("重试 ");
		cancal.getLabel().setTextSize(45);
		cancal.getLabel().setColor(Color.WHITE);
		cancal.getLabel().setAlpha(0.5f);

		sure.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChanged(Actor arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					cancal.getLabel().setAlpha(0.5f);
					sure.getLabel().setAlpha(1.0f);
				}

			}
		});
		sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(Actor arg0) {
				if (_confirmDialogListener != null)
					_confirmDialogListener.onOK(InternetErrorDialog.this);
			}
		});
		addActor(sure);

		cancal.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChanged(Actor arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					sure.getLabel().setAlpha(0.5f);
					cancal.getLabel().setAlpha(1.0f);
				}

			}
		});
		cancal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(Actor arg0) {
				if (_confirmDialogListener != null)
					_confirmDialogListener.onCancel(InternetErrorDialog.this);
			}
		});
		addActor(cancal);

	}

	@Override
	public boolean dispatchKeyEvent(int keycode, Actor focusActor) {
		if (expectHasFocusTemp) {

		}
		return super.dispatchKeyEvent(keycode, focusActor);
	}

	@Override
	public boolean onBackKeyDown() {
//		MobclickAgent.onKillProcess(page.getActivity());
		page.finish();
		page.getActivity().finish();
		System.exit(0);
		return true;
	}

}
