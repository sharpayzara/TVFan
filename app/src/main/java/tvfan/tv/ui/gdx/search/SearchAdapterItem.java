package tvfan.tv.ui.gdx.search;

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
import tvfan.tv.lib.Utils;

public class SearchAdapterItem extends Group implements IListItem {

	private Label mNumerLabel;
	private CullGroup mCullGroup;
	private Image mbgFocus;
	private Image mbg;
	private String text;

	public SearchAdapterItem(Page page) {
		super(page);
		setSize(80, 80);
		setFocusAble(true);
		mCullGroup = new CullGroup(page);
		mCullGroup.setSize(80, 80);
		mCullGroup.setCullingArea(new Rectangle(-50, -50, 80 + 150, 80 + 150));
		addActor(mCullGroup);

		mNumerLabel = new Label(page);
		mNumerLabel.setSize(80, 80);
		mNumerLabel.setTextSize(40);
		mNumerLabel.setColor(Color.WHITE);
		mNumerLabel.setMarquee(true);
		mNumerLabel.setAlignment(Align.center);
		mNumerLabel.setAlpha(0.9f);
		mCullGroup.addActor(mNumerLabel);

		mbg = new Image(getPage());
		mbg.setSize(80, 80);
		mbg.setDrawableResource(R.drawable.search_btn);
		mCullGroup.addActor(mbg);

		mbgFocus = new Image(getPage());
		mbgFocus.setPosition(-20, -20);
		mbgFocus.setSize(80 + 40, 80 + 40);
		mbgFocus.setDrawableResource(R.drawable.new_foucs);
		mbgFocus.setVisible(false);
		mCullGroup.addActor(mbgFocus);

	}

	@Override
	public void onRecycle() {
		mNumerLabel.setText("");
		mNumerLabel.setAlignment(Align.center);
		mNumerLabel.setMarquee(true);

	}

	@Override
	public void onSelected(boolean arg0) {

	}

	public void setmNumer(String text) {
		mNumerLabel.setAlignment(Align.center);
		mNumerLabel.setText(text);
		this.text = text;
	}

	@Override
	public void notifyFocusChanged(boolean getFocus) {
		super.notifyFocusChanged(getFocus);
		mbgFocus.setVisible(getFocus);
		setFocusScale(0.1f);
	}
	@Override
	public void onResume() {
		Utils.resetImageSource(mbg, R.drawable.search_btn);
		Utils.resetImageSource(mbgFocus, R.drawable.new_foucs);
		mNumerLabel.setText(text);
		mNumerLabel.setAlignment(Align.center);
		super.onResume();
	}
}
