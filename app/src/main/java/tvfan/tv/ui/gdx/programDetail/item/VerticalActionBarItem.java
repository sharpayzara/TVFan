package tvfan.tv.ui.gdx.programDetail.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.CullGroup;

import tvfan.tv.R;
import tvfan.tv.ui.gdx.widgets.NinePatchImage;
import tvfan.tv.ui.gdx.widgets.TVFANLabel;

public class VerticalActionBarItem extends Group implements IListItem{
	
	private TVFANLabel mLabel;
	private CullGroup cullGroup;
	private String text;
	private Image image;
	
	public VerticalActionBarItem(Page page) {
		super(page);
//		setFocusScale(0.2f);
		setSize(180, 90);
		setFocusAble(true);
		cullGroup = new CullGroup(getPage());
		cullGroup.setSize(180, 90);
		cullGroup.setCullingArea(new Rectangle(0, 0, 180, 90));
		addActor(cullGroup);
		mLabel = new TVFANLabel(page);
		mLabel.setPosition(0, 0);
		mLabel.setSize(150, 90);
		mLabel.setAlignment(Align.center);
		mLabel.setTextSize(45);
		mLabel.setColor(Color.valueOf("f0f0f0"));
		mLabel.setAlpha(0.8f);
		mLabel.setFocusAble(false);
		cullGroup.addActor(mLabel);
		
		image = NinePatchImage.make(getPage(),
				this.findTexture(R.drawable.new_foucs),
				new int[]{40,40,40,40});
		image.setSize(210, 120);
		image.setPosition(-30, -15);
		image.setVisible(false);
		addActor(image);
	}
	
	public void setText(String text){
		this.text = text;
		mLabel.setText(text);
		setName(text);
	}
	
	public String getText(){
		return  mLabel.getText().toString();
	}
	
	public void setTextSize(int size){
		mLabel.setTextSize(size);
	}
	
	public Label getLabel(){
		return mLabel;
	}
	
	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);
		image.setVisible(getFocus);
		if(getFocus){
//			mLabel.setColor(Color.valueOf("37a0fb"));
			mLabel.setAlpha(1f);
		}else{
//			mLabel.setColor(Color.valueOf("f0f0f0"));
			mLabel.setAlpha(0.8f);
		}
	}
	
	@Override
	public void onResume() {
		mLabel.setText(text);
		super.onResume();
	}
	
	
	@Override
	public void onRecycle() {
		mLabel.setText("");
	}

	@Override
	public void onSelected(boolean arg0) {
		
	}

}
