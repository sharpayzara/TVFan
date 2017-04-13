package tvfan.tv.ui.gdx.widgets;

import tvfan.tv.R;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Dialog;
import com.luxtone.lib.gdx.OnClickListener;
import com.luxtone.lib.gdx.OnFocusChangeListener;
import com.luxtone.lib.gdx.Page;

public class ConfirmDialog extends Dialog {

	private Image bgImg;
	private Button sure, cancal;
	private Label titleLabel;
	private ConfirmDialogListener _confirmDialogListener;

	public interface ConfirmDialogListener {
		/**
		 * @param layoutElement 当前元素x,y,width,height
		 * @param dataListItem 当前元素对应的数据节点
		 * @param layoutElementIndex 布局序号
		 * @param posOfList 当前元素相对于整个列表的位置，1:左(上)边缘 2:中间 4:右(下)边缘。如果仅有1列，那么此值为1+4
		 * @param horizontalIconList 当前元素所在的容器对象
		 **/
		public void onOK();
		public void onCancel();
	}

	public ConfirmDialog(final Page page, final String msg, ConfirmDialogListener confirmDialogListener) {
		super(page);
		_confirmDialogListener = confirmDialogListener;
		setSize(1000, 660);
		setPosition(460, 270);
		
		bgImg = new Image(getPage());
		bgImg.setPosition(0, 0);
		bgImg.setSize(1000, 660);
		bgImg.setDrawableResource(R.drawable.tkbj);
		addActor(bgImg);
		
	
		titleLabel = new Label(getPage());
		titleLabel.setSize(1000, 45);
		titleLabel.setTextSize(45);
		titleLabel.setColor(Color.WHITE);
		titleLabel.setAlpha(0.5f);
		titleLabel.setText(msg);
		titleLabel.setPosition(0, 660 - 120 - 45);
		titleLabel.setAlignment(Align.center);
		addActor(titleLabel);

		sure = new Button(getPage(), 350, 100);
		sure.setPosition(125, 80);
		sure.getImage().setDrawableResource(R.drawable.listbj);
		sure.setUnFocusBackGround(R.drawable.listbj);
		sure.setFocusBackGround(R.drawable.listbj);
		sure.setFocusAble(true);
		sure.getLabel().setText("确定");
		sure.getLabel().setTextSize(45);
		sure.getLabel().setColor(Color.WHITE);
		sure.getLabel().setAlpha(0.5f);

		cancal = new Button(getPage(), 350, 100);
		cancal.setPosition(525, 80);
		cancal.getImage().setDrawableResource(R.drawable.listbj);
		cancal.setUnFocusBackGround(R.drawable.listbj);
		cancal.setFocusBackGround(R.drawable.listbj);
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
					sure.getLabel().setAlpha(1.0f);
				}

			}
		});
		//升级确定
		sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(Actor arg0) {
				if(_confirmDialogListener!=null)
					_confirmDialogListener.onOK();
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
				if(_confirmDialogListener!=null)
					_confirmDialogListener.onCancel();
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
		return true;
	}

	
}
