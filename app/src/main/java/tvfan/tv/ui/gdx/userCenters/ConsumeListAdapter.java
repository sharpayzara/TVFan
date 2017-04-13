package tvfan.tv.ui.gdx.userCenters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.luxtone.lib.gdx.Page;
import com.luxtone.lib.widget.AbsListView.IListItem;
import com.luxtone.lib.widget.CullGroup;
import com.luxtone.lib.widget.Grid.GridAdapter;

import java.util.ArrayList;
import java.util.List;

import tvfan.tv.R;
import tvfan.tv.dal.models.ConsumeList;
import tvfan.tv.lib.Utils;

public class ConsumeListAdapter extends GridAdapter {

	private Page page;
	List<ConsumeList> data = new ArrayList<ConsumeList>();;

	public ConsumeListAdapter(Page page) {
		this.page = page;
	}

	@Override
	public Actor getActor(int position, Actor convertActor) {
		ConsumeGridItem item = null;

		if (convertActor == null) {
			item = new ConsumeGridItem(page);
		} else {
			item = (ConsumeGridItem) convertActor;
		}
		try {
			item.setScale(1f);
			item.update(data.get(position).getOrderid(), data.get(position)
					.getOrderTime(), data.get(position).getProductName(), data
					.get(position).getExpireTime(), data.get(position)
					.getValidTime(), data.get(position).getStatus(),
					data.get(position).getPayMethod(), data.get(position)
							.getPrice());
		} catch (Exception e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		return item;
	}

	public void setData(List<ConsumeList> list) {
		this.data = list;
	}

	class ConsumeGridItem extends Group implements IListItem {
		private Image image;
		Image focusImg;
		CullGroup cullGroup, cullGroupOrderid, cullGroupOrderName;
		private Label orderid, orderidcontent, expireTime, expireTimeContent,
				label12, label13, label14, label22, label23, label33, label32,
				label42, label43, label24, label34, label44;
		String orderId, orderTime, productName, expiretime, validTime, status,
				price, payMethod;

		// int pos =

		public ConsumeGridItem(Page page) {
			super(page);
			setSize(1230, 305);
			setFocusAble(true);

			cullGroup = new CullGroup(getPage());
			cullGroup.setSize(1230, 305);
			cullGroup.setPosition(0, 0);
			cullGroup.setCullingArea(new Rectangle(-50, -20, 1330, 365));
			addActor(cullGroup);

			image = new Image(getPage());
			image.setSize(1230, 305);
			image.setDrawableResource(R.drawable.new_foucs);
			image.setPosition(0, 10);
			cullGroup.addActor(image);

			orderid = new Label(getPage(), false);
			orderid.setSize(305, 66);
			orderid.setTextSize(35);
			orderid.setText("订单编号");
			orderid.setPosition(0, 305 - 59);
			orderid.setMarquee(false);
			orderid.setColor(Color.WHITE);
			orderid.setAlpha(0.9f);
			orderid.setAlignment(Align.center);
			cullGroup.addActor(orderid);

			cullGroupOrderid = new CullGroup(getPage());
			cullGroupOrderid.setSize(305, 88);
			cullGroupOrderid.setPosition(0, 305 - 144);
			cullGroupOrderid.setCullingArea(new Rectangle(10, 0, 285, 88));
			cullGroup.addActor(cullGroupOrderid);

			orderidcontent = new Label(getPage(), false);
			orderidcontent.setSize(285, 88);
			orderidcontent.setTextSize(40);
			orderidcontent.setPosition(10, 0);
			orderidcontent.setMarquee(false);
			orderidcontent.setColor(Color.WHITE);
			orderidcontent.setAlpha(0.9f);
			orderidcontent.setAlignment(Align.center);
			cullGroupOrderid.addActor(orderidcontent);

			expireTime = new Label(getPage(), false);
			expireTime.setSize(305, 66);
			expireTime.setTextSize(35);
			expireTime.setText("有效期");
			expireTime.setPosition(0, 95);
			expireTime.setMarquee(false);
			expireTime.setColor(Color.WHITE);
			expireTime.setAlpha(0.9f);
			expireTime.setAlignment(Align.center);
			cullGroup.addActor(expireTime);

			expireTimeContent = new Label(getPage(), false);
			expireTimeContent.setSize(305, 88);
			expireTimeContent.setTextSize(40);
			expireTimeContent.setPosition(0, 10);
			expireTimeContent.setMarquee(false);
			expireTimeContent.setColor(Color.WHITE);
			expireTimeContent.setAlignment(Align.center);
			expireTimeContent.setAlpha(0.9f);
			cullGroup.addActor(expireTimeContent);

			label12 = new Label(getPage(), false);
			label12.setSize(305, 66);
			label12.setTextSize(35);
			label12.setText("购买时间");
			label12.setPosition(305, 305 - 59);
			label12.setMarquee(false);
			label12.setColor(Color.WHITE);
			label12.setAlpha(0.9f);
			label12.setAlignment(Align.center);
			cullGroup.addActor(label12);

			label22 = new Label(getPage(), false);
			label22.setSize(305, 88);
			label22.setTextSize(40);
			label22.setPosition(305, 305 - 144);
			label22.setMarquee(false);
			label22.setColor(Color.WHITE);
			label22.setAlpha(0.9f);
			label22.setAlignment(Align.center);
			cullGroup.addActor(label22);

			label32 = new Label(getPage(), false);
			label32.setSize(305, 66);
			label32.setTextSize(35);
			label32.setText("订单状态");
			label32.setPosition(305, 95);
			label32.setMarquee(false);
			label32.setColor(Color.WHITE);
			label32.setAlpha(0.9f);
			label32.setAlignment(Align.center);
			cullGroup.addActor(label32);

			label42 = new Label(getPage(), false);
			label42.setSize(305, 88);
			label42.setTextSize(40);
			label42.setPosition(305, 10);
			label42.setMarquee(false);
			label42.setColor(Color.WHITE);
			label42.setAlpha(0.9f);
			label42.setAlignment(Align.center);
			cullGroup.addActor(label42);

			label13 = new Label(getPage(), false);
			label13.setSize(305, 66);
			label13.setTextSize(35);
			label13.setText("产品名称");
			label13.setPosition(610, 305 - 59);
			label13.setMarquee(false);
			label13.setColor(Color.WHITE);
			label13.setAlpha(0.9f);
			label13.setAlignment(Align.center);
			cullGroup.addActor(label13);

			cullGroupOrderName = new CullGroup(getPage());
			cullGroupOrderName.setSize(300, 88);
			cullGroupOrderName.setPosition(615, 305 - 144);
			cullGroupOrderName.setCullingArea(new Rectangle(10, 0, 280, 88));
			cullGroup.addActor(cullGroupOrderName);

			label23 = new Label(getPage(), false);
			label23.setSize(280, 88);
			label23.setTextSize(40);
			label23.setPosition(10, 0);
			label23.setMarquee(false);
			label23.setColor(Color.WHITE);
			label23.setAlpha(0.9f);
			label23.setAlignment(Align.center);
			cullGroupOrderName.addActor(label23);

			label33 = new Label(getPage(), false);
			label33.setSize(305, 66);
			label33.setTextSize(35);
			label33.setText("支付方式");
			label33.setPosition(610, 95);
			label33.setMarquee(false);
			label33.setColor(Color.WHITE);
			label33.setAlpha(0.9f);
			label33.setAlignment(Align.center);
			cullGroup.addActor(label33);

			label43 = new Label(getPage(), false);
			label43.setSize(305, 88);
			label43.setTextSize(40);
			label43.setPosition(610, 10);
			label43.setMarquee(false);
			label43.setColor(Color.WHITE);
			label43.setAlpha(0.9f);
			label43.setAlignment(Align.center);
			cullGroup.addActor(label43);

			label14 = new Label(getPage(), false);
			label14.setSize(305, 66);
			label14.setTextSize(35);
			label14.setText("截至有效期");
			label14.setPosition(915, 305 - 59);
			label14.setMarquee(false);
			label14.setColor(Color.WHITE);
			label14.setAlpha(0.9f);
			label14.setAlignment(Align.center);
			cullGroup.addActor(label14);

			label24 = new Label(getPage(), false);
			label24.setSize(305, 88);
			label24.setTextSize(40);
			label24.setPosition(915, 305 - 144);
			label24.setMarquee(false);
			label24.setColor(Color.WHITE);
			label24.setAlpha(0.9f);
			label24.setAlignment(Align.center);
			cullGroup.addActor(label24);

			label34 = new Label(getPage(), false);
			label34.setSize(305, 66);
			label34.setTextSize(35);
			label34.setText("金额");
			label34.setPosition(915, 95);
			label34.setMarquee(false);
			label34.setColor(Color.WHITE);
			label34.setAlpha(0.9f);
			label34.setAlignment(Align.center);
			cullGroup.addActor(label34);

			label44 = new Label(getPage(), false);
			label44.setSize(305, 88);
			label44.setTextSize(40);
			label44.setPosition(915, 10);
			label44.setMarquee(false);
			label44.setColor(Color.WHITE);
			label44.setAlpha(0.9f);
			label44.setAlignment(Align.center);
			cullGroup.addActor(label44);

			// 光标选中效果
			focusImg = new Image(getPage());
			focusImg.setSize(1330, 400);
			focusImg.setDrawableResource(R.drawable.new_foucs);
			focusImg.setPosition(-50, -35);
			focusImg.setVisible(false);
			cullGroup.addActor(focusImg);

		}

		@Override
		public void notifyFocusChanged(boolean getFocus) {
			super.notifyFocusChanged(getFocus);
			focusImg.setVisible(getFocus);
			orderidcontent.setMarquee(getFocus);
			label23.setMarquee(getFocus);
			if (getFocus) {
				focusImg.addAction(Actions.fadeIn(0.1f));
			} else {
				focusImg.addAction(Actions.fadeOut(0.1f));
			}

		}

		public void update(String orderId, String orderTime,
				String productName, String expiretime, String validTime,
				String status, String payMethod, String price) {
			this.orderId = orderId;
			this.orderTime = orderTime;
			this.productName = productName;
			this.expiretime = expiretime;
			this.validTime = validTime;
			this.status = status;
			this.payMethod = payMethod;
			this.price = price;
			orderidcontent.setText(orderId);
			label22.setText(orderTime);
			label23.setText(productName);
			label24.setText(expiretime);
			expireTimeContent.setText(validTime);
			label42.setText(status);
			label44.setText(price);
			label43.setText(payMethod);
		}

		@Override
		public void onRecycle() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onSelected(boolean arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onResume() {
			Utils.resetImageSource(focusImg, R.drawable.new_foucs);
			Utils.resetImageSource(image, R.drawable.new_foucs);
			orderid.setText("订单编号");
			orderidcontent.setText(orderId);
			expireTime.setText("有效期");
			expireTimeContent.setText(validTime);
			label12.setText("购买时间");
			label22.setText(orderTime);
			label32.setText("订单状态");
			label42.setText(status);
			label13.setText("产品名称");
			label23.setText(productName);
			label33.setText("支付方式");
			label43.setText(payMethod);
			label14.setText("截至有效期");
			label24.setText(expiretime);
			label34.setText("金额");
			label44.setText(price);

			super.onResume();
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}
}
